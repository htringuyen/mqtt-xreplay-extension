package org.iotwarehouse.extension.core.messaging;

public interface Role {

    RoleType getType();

    String getName();

    public static RoleImpl.RoleBuilder builder() {
        return new RoleImpl.RoleBuilder();
    }
}
