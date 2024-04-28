package org.iotwarehouse.extension.external.sqlstore.pooling;

import org.iotwarehouse.extension.core.external.sqlstore.pooling.ConnectionPool;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConnectionPoolTest {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionPoolTest.class);

    @Test
    public void testTakeFromConnectionPool() throws InterruptedException {
        var es = Executors.newCachedThreadPool();
        for (int i = 0; i < 100; i++) {
            var ordinal = i;
            es.submit(() -> doTakeConnection(String.format("Task-%02d", ordinal)));
        }
        es.shutdown();
        if (es.awaitTermination(30, TimeUnit.SECONDS)) {
            logger.info("ExecutorService cleanly terminated.");
        } else {
            logger.info("ExecutorService terminated before all thread completed.");
        }
    }

    private void doTakeConnection(String taskName) {
        try (var conn = ConnectionPool.takeConnection()) {
            logger.info("{} take connection", taskName);
            var stmt = conn.prepareStatement("SELECT COUNT(*) FROM business.Warehouse");
            stmt.executeQuery();
            doSleep(1000);
        } catch (SQLException e) {
            logger.info("Error", e);
        }
        logger.info("{} done and try to release connection", taskName);
    }

    private void doSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
