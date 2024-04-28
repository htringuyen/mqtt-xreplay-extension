package org.iotwarehouse.extension.core.param;

public interface ExplicitParam {

    public String getName();

    public ValueSet getValueSet();

    default boolean includeValuesFrom(ExplicitParam other) {
        if (!other.getName().equals(getName())) {
            return false;
        }
        return getValueSet().values().containsAll(other.getValueSet().values());
    }
}
