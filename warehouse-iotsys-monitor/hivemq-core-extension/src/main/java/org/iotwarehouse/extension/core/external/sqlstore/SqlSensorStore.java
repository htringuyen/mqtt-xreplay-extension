package org.iotwarehouse.extension.core.external.sqlstore;

import org.iotwarehouse.extension.core.external.SensorStore;
import org.iotwarehouse.extension.core.external.exception.ExternalServiceException;
import org.iotwarehouse.extension.core.external.sqlstore.pooling.ConnectionPool;
import org.iotwarehouse.extension.core.param.ExplicitParam;
import org.iotwarehouse.extension.core.param.ExplicitParams;
import org.iotwarehouse.extension.core.param.SingularParam;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class SqlSensorStore implements SensorStore {

    private static final String QUERY = "SELECT warehouseCode as warehouse_code, " +
            "containerOrdinal as container_ordinal " +
            "FROM sensorcomm.SensorParam " +
            "WHERE sensorId = ?;";

    @Override
    public Optional<ExplicitParams> findParamsForId(String sensorId) throws ExternalServiceException {
        try (var conn = ConnectionPool.takeConnection()) {
            var stmt = conn.prepareStatement(QUERY);
            stmt.setString(1, sensorId);
            var rst = stmt.executeQuery();
            if (rst.next()) {
                var params = ExplicitParams.from(
                        getParamFrom(rst, "warehouse_code"),
                        getParamFrom(rst, "container_ordinal"));
                return Optional.of(params);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new ExternalServiceException("Error when retrieve sensor param");
        }
    }

    private ExplicitParam getParamFrom(ResultSet rst, String paramName) throws SQLException{
        return SingularParam.builder()
                .name(paramName)
                .value(rst.getString(paramName))
                .build();
    }
}



















