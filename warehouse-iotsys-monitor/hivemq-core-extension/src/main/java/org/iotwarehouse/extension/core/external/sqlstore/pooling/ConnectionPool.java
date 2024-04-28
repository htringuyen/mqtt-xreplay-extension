package org.iotwarehouse.extension.core.external.sqlstore.pooling;

public interface ConnectionPool {

    public static PooledConnection takeConnection() {
        return ConnectionPoolImpl.getInstance().takeConnection();
    }

    public static void release(PooledConnection connection) {
        ConnectionPoolImpl.getInstance().release(connection);
    }
}
