package org.iotwarehouse.extension.core.external;

import org.iotwarehouse.extension.core.external.sqlstore.SqlObserverStore;
import org.iotwarehouse.extension.core.external.sqlstore.SqlRoleStore;
import org.iotwarehouse.extension.core.external.sqlstore.SqlSensorStore;
import org.iotwarehouse.extension.core.external.sqlstore.SqlTopicGroupStore;

public class ExternalServices {

    private static final RoleStore roleStore = new SqlRoleStore();

    private static final SqlTopicGroupStore topicStore = new SqlTopicGroupStore();

    private static final SqlSensorStore sensorStore = new SqlSensorStore();

    private static final SqlObserverStore observerStore = new SqlObserverStore();

    public static RoleStore getRoleStore() {
        return roleStore;
    }

    public static TopicGroupStore getTopicStore() {
        return topicStore;
    }

    public static SensorStore getSensorStore() {
        return sensorStore;
    }

    public static ObserverStore getObserverStore() {
        return observerStore;
    }
}
