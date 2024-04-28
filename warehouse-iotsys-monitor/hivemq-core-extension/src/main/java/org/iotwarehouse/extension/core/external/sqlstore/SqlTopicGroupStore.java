package org.iotwarehouse.extension.core.external.sqlstore;

import org.iotwarehouse.extension.core.external.TopicGroupStore;
import org.iotwarehouse.extension.core.external.exception.ExternalServiceException;
import org.iotwarehouse.extension.core.external.sqlstore.pooling.ConnectionPool;
import org.iotwarehouse.extension.core.messaging.Role;
import org.iotwarehouse.extension.core.messaging.RoleType;
import org.iotwarehouse.extension.core.messaging.TopicGroupInfo;
import org.iotwarehouse.extension.core.replay.RecordPolicy;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class SqlTopicGroupStore implements TopicGroupStore {
    private static final String QUERY = "SELECT t.name as topicGroupName, t.topicPattern as topicPattern, " +
            "t.recordMaxLength as recordMaxLength, " +
            "t.recordPolicy as recordPolicy, " +
            "p.activity as activity, r.name as roleName, r.isSensor as isSensorRole " +
            "FROM sensorcomm.TopicGroup t " +
            "LEFT JOIN sensorcomm.Permission p ON t.name = p.topicName " +
            "LEFT JOIN sensorcomm.Role r ON r.name = p.roleName ORDER BY topicName;";

    @Override
    public List<TopicGroupInfo> getTopicGroupInfos() throws ExternalServiceException {
        try (var conn = ConnectionPool.takeConnection()) {

            var stmt = conn.prepareStatement(QUERY);
            var rst = stmt.executeQuery();
            var builderMap = new HashMap<String, TopicGroupInfo.Builder>();

            while (rst.next()) {

                var topicGroupName = getColumn(rst, "topicGroupName", String.class);
                var topicPattern = getColumn(rst, "topicPattern", String.class);
                var recordMaxLength = getColumn(rst, "recordMaxLength", Long.class);
                var recordPolicy = RecordPolicy.valueOf(getColumn(rst, "recordPolicy", String.class));
                var activity = getColumn(rst, "activity", String.class);
                var roleName = getColumn(rst, "roleName", String.class);
                var isSensorRole = getColumn(rst, "isSensorRole", Boolean.class);

                if (!builderMap.containsKey(topicGroupName)) {

                    var builder = TopicGroupInfo.builder();
                    builder.topicGroupName(topicGroupName)
                            .topicPattern(topicPattern)
                            .recordMaxLength(recordMaxLength)
                            .recordPolicy(recordPolicy);

                    builderMap.put(topicGroupName, builder);
                }

                addPermissionRole(builderMap.get(topicGroupName), roleName, isSensorRole, activity);
            }
            return builderMap.values()
                    .stream()
                    .map(TopicGroupInfo.Builder::build)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new ExternalServiceException("Error when retrieve topic info");
        }
    }

    private <T> T getColumn(ResultSet rst, String columnName, Class<T> clazz) throws SQLException {
        var obj = rst.getObject(columnName, clazz);
        if (rst.wasNull()) {
            return null;
        }
        return obj;
    }

    private void addPermissionRole(TopicGroupInfo.Builder builder, String roleName, Boolean isSensor, String activity) {
        if (roleName == null || activity == null) {
            return;
        }

        var role = Role.builder()
                .name(roleName)
                .roleType(isSensor ? RoleType.SENSOR : RoleType.OBSERVER)
                .build();

        if (activity.equals("SUBSCRIBE")) {
            builder.addSubscribeRole(role);
        } else if (activity.equals("PUBLISH")) {
            builder.addPublishRole(role);
        }
    }
}


















