package org.iotwarehouse.extension.core.external.sqlstore.pooling;

import java.util.*;

public class DbProperties {

    public static final String USERNAME = "username";

    public static final String PASSWORD = "password";

    public static final String DB_NAME = "databaseName";

    private final Map<String, String> propertyMap = new HashMap<>();

    public DbProperties() {
        loadInlinedProperties();
    }

    public Optional<String> get(String name) {
        if (!propertyMap.containsKey(name)) {
            return Optional.empty();
        }
        return Optional.of(propertyMap.get(name));
    }

    public Set<Map.Entry<String, String>> propertyEntries() {
        return propertyMap.entrySet();
    }

    private void loadInlinedProperties() {
        propertyMap.put(USERNAME, "sa");
        propertyMap.put(PASSWORD, "snowJ@5722");
        propertyMap.put(DB_NAME, "warehousesys");
    }


}
