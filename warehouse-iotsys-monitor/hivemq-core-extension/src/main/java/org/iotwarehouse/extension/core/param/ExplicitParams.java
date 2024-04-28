package org.iotwarehouse.extension.core.param;

import java.util.Collection;
import java.util.Optional;

public interface ExplicitParams {

    Optional<ExplicitParam> get(String name);

    public boolean contains(String paramName);

    boolean isSubsetOf(ExplicitParams other);

    static ExplicitParams from(ExplicitParam... params) {
        return new ExplicitParamsImpl(params);
    }

    static ExplicitParams from(Collection<ExplicitParam> params) {
        return new ExplicitParamsImpl(params);
    }
}
