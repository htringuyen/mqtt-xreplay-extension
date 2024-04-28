package org.iotwarehouse.extension.core.param;

import lombok.Getter;

@Getter
public abstract class BaseExplicitParam implements ExplicitParam {

    protected final String name;

    protected BaseExplicitParam(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("ExplicitParam{ name=%s, value=%s }", getName(), getValueSet());
    }
}
