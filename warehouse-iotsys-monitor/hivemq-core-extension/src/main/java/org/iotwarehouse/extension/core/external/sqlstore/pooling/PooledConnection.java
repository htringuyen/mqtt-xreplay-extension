package org.iotwarehouse.extension.core.external.sqlstore.pooling;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public interface PooledConnection extends AutoCloseable {

    @Override
    public void close();

    public PreparedStatement prepareStatement(String sql) throws SQLException;

    public Statement createStatement() throws SQLException;

    public boolean isBroken();
}
