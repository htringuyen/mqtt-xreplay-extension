package org.iotwarehouse.extension.core.external.sqlstore;

import org.iotwarehouse.extension.core.external.ObserverStore;
import org.iotwarehouse.extension.core.external.exception.ExternalServiceException;
import org.iotwarehouse.extension.core.external.sqlstore.pooling.ConnectionPool;
import org.iotwarehouse.extension.core.param.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class SqlObserverStore implements ObserverStore {

    private static final Logger logger = LoggerFactory.getLogger(SqlObserverStore.class);

    private static final String AUTHENTICATE_QUERY = "SELECT COUNT(*) as matchCount " +
            "FROM sensorcomm.Observer " +
            "WHERE name = ? AND password = ?;";

    private static final String FIND_PARAMS_QUERY = "SELECT warehouseCode as warehouse_code, " +
            "containerOrdinal as container_ordinal, " +
            "sensorOrdinal as sensor_ordinal " +
            "FROM sensorcomm.ObserverParam " +
            "WHERE observerName = ?";

    @Override
    public boolean authenticate(String observerName, String password) throws ExternalServiceException {
        try (var conn = ConnectionPool.takeConnection()) {
            var stmt = conn.prepareStatement(AUTHENTICATE_QUERY);
            stmt.setString(1, observerName);
            stmt.setString(2, password);
            var rst = stmt.executeQuery();
            if (rst.next()) {
                var matchCount = rst.getInt("matchCount");
                return matchCount == 1;
            }
            return false;
        } catch (SQLException e) {
            throw new ExternalServiceException("Error when retrieve observer credentials");
        }
    }

    @Override
    public Optional<ExplicitParams> findParamsForName(String observerName) throws ExternalServiceException {
        try (var conn = ConnectionPool.takeConnection()) {
            var stmt = conn.prepareStatement(FIND_PARAMS_QUERY);
            stmt.setString(1, observerName);
            var rst = stmt.executeQuery();
            var vagueParamsBuilder = DefaultVagueParams.builder()
                    .infiniteMark("ALL")
                    .notApplicableMark("N/A");

            var hasFirst = rst.next();
            if (!hasFirst) {
                return Optional.empty();
            }

            do {
                vagueParamsBuilder.add("warehouse_code", rst.getString("warehouse_code"));
                vagueParamsBuilder.add("container_ordinal", rst.getString("container_ordinal"));
                vagueParamsBuilder.add("sensor_ordinal", rst.getString("sensor_ordinal"));
            } while (rst.next());

            logger.debug("Find observer params successfully");

            return Optional.of(vagueParamsBuilder.build().toExplicitParams());

        } catch (SQLException e) {
            throw new ExternalServiceException("Error when retrieve observer params", e);
        }
    }
}
