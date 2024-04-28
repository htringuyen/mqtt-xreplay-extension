package org.iotwarehouse.extension.core.external.sqlstore.pooling;

import org.iotwarehouse.extension.core.external.sqlstore.exception.DatabaseConnectException;
import org.iotwarehouse.extension.core.external.sqlstore.exception.UnrecognizedConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

class ConnectionPoolImpl implements ConnectionPool {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionPoolImpl.class);

    private static final String DEFAULT_MSSQL_URL = "jdbc:sqlserver://128.128.128.22:1433";

    private static final int MIN_CONNECTIONS = 3;

    private static final int MAX_CONNECTIONS = 10;

    private static final int CONNECT_FAILED_RETRY = 5;

    private static final long RETRY_FAILED_SLEEP_MILLIS = 200;

    private final AtomicInteger connectionCount = new AtomicInteger(0);

    private final DbProperties dbProperties = new DbProperties();

    private final BlockingQueue<PooledConnection> freeConnections = new LinkedBlockingQueue<>();

    private final BlockingQueue<PooledConnection> busyConnections = new LinkedBlockingQueue<>();

    // singleton instance
    private static final ConnectionPoolImpl INSTANCE = new ConnectionPoolImpl();

    static ConnectionPoolImpl getInstance() {
        return INSTANCE;
    }


    ConnectionPoolImpl() {
        requestConnectionsRetry(CONNECT_FAILED_RETRY);
    }


    PooledConnection takeConnection() {
        try {
            requestConnectionsRetry(CONNECT_FAILED_RETRY);
            var connection = freeConnections.take();
            busyConnections.add(connection);
            return connection;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    void release(PooledConnection connection) {
        if (freeConnections.contains(connection)) {
            return;
        }

        if (!busyConnections.contains(connection)) {
            throw new UnrecognizedConnectionException("Attempt to release unrecognized pooled connection.");
        }
        if (!busyConnections.remove(connection)) {
            throw new RuntimeException("Unexpected removing connection failed");
        }
        if (!connection.isBroken()) {
            freeConnections.add(connection);
        }
    }

    private void requestConnectionsRetry(int retry) {
        for (int i = 1; i <= retry; i++) {
            try {
                requestConnections();
                return;
            } catch (SQLException e) {
                if (i == retry) {
                    throw new DatabaseConnectException(e);
                } else {
                    logger.debug("Request connection failed. Retry {}", i, e);
                    doSleep(RETRY_FAILED_SLEEP_MILLIS);
                }
            }
        }
    }

    private void doSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            logger.debug("Exception while sleeping", e);
        }
    }

    private synchronized void requestConnections() throws SQLException  {
        if (connectionCount.get() >= MAX_CONNECTIONS) {
            return;
        }
        var currentCount = connectionCount.get();
        var createNextTotal = Math.max(MIN_CONNECTIONS - currentCount, 1);

        for (int i = 0; i < createNextTotal; i++) {
            var connection = DriverManager.getConnection(DEFAULT_MSSQL_URL, buildProperties());
            freeConnections.add(new PooledConnectionImpl(connection));
            connectionCount.getAndIncrement();
        }
    }


    private Properties buildProperties() {
        var properties = new Properties();
        properties.setProperty("encrypt", "false");
        properties.setProperty("trustServerCertificate", "true");

        dbProperties.propertyEntries()
                .forEach(e -> properties.setProperty(e.getKey(), e.getValue()));
        return properties;
    }
}

























