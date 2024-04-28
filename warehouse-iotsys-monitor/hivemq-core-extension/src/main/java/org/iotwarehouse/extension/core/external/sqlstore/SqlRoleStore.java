package org.iotwarehouse.extension.core.external.sqlstore;

import org.iotwarehouse.extension.core.external.RoleStore;
import org.iotwarehouse.extension.core.external.exception.ExternalServiceException;
import org.iotwarehouse.extension.core.external.sqlstore.pooling.ConnectionPool;
import org.iotwarehouse.extension.core.messaging.Role;
import org.iotwarehouse.extension.core.messaging.RoleType;

import java.sql.SQLException;
import java.util.Optional;

public class SqlRoleStore implements RoleStore {

    private static final String QUERY =
            "SELECT name, isSensor FROM sensorcomm.Role WHERE authName = ? AND authPassword = ?";

    public Optional<Role> authenticateRole(String authName, String authPassword) throws ExternalServiceException {
        try (var conn = ConnectionPool.takeConnection()) {
            var stmt = conn.prepareStatement(QUERY);
            stmt.setString(1, authName);
            stmt.setString(2, authPassword);
            var rst = stmt.executeQuery();
            if (rst.next()) {
                var roleName = rst.getString("name");
                var isSensor = rst.getBoolean("isSensor");

                var role = Role.builder()
                        .name(roleName)
                        .roleType(isSensor ? RoleType.SENSOR : RoleType.OBSERVER)
                        .build();
                return Optional.of(role);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new ExternalServiceException("Error when retrieve auth info");
        }
    }

}
