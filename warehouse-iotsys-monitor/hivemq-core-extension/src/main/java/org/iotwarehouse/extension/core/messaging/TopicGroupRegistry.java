package org.iotwarehouse.extension.core.messaging;

import java.util.List;

public interface TopicGroupRegistry {
    TopicGroup register(String name, String pattern, List<Role> pubRoles, List<Role> subRoles);
}
