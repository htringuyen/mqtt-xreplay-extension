package org.iotwarehouse.extension.core.messaging;

import lombok.Builder;
import lombok.NonNull;

public class RoleImpl implements Role {
    private final RoleType type;
    private final String name;

    @Builder(builderClassName = "RoleBuilder")
    public RoleImpl(@NonNull RoleType roleType, @NonNull String name) {
        this.type = roleType;
        this.name = name;
    }

    @Override
    public RoleType getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("Role{ name=%s, type=%s }", getName(), getType());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RoleImpl) {
            var other = (RoleImpl) obj;
            return getName().equals(other.getName()) && getType().equals(other.getType());
        }
        return false;
    }
}
