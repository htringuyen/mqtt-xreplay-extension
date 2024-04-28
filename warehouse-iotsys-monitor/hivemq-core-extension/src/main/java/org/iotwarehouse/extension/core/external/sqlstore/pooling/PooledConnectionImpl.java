package org.iotwarehouse.extension.core.external.sqlstore.pooling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class PooledConnectionImpl implements PooledConnection {

    private static final Logger logger = LoggerFactory.getLogger(PooledConnectionImpl.class);
    private final Map<String, PreparedStatement> cachedStatementMap = new HashMap<>();

    private final Connection internal;

    public PooledConnectionImpl(Connection connection) {
        this.internal = connection;
    }

    @Override
    public void close() {
        try {
            ConnectionPool.release(this);
        } catch (Exception e) {
            logger.debug("Error", e);
        }
    }

    @Override
    public boolean isBroken() {
        try {
            return internal.isClosed();
        } catch (SQLException e) {
            return true;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        if (cachedStatementMap.containsKey(sql)) {
            var stmt = cachedStatementMap.get(sql);
            stmt.clearParameters();
            return stmt;
        }
        return internal.prepareStatement(sql);
    }

    @Override
    public Statement createStatement() throws SQLException {
        return internal.createStatement();
    }


}
