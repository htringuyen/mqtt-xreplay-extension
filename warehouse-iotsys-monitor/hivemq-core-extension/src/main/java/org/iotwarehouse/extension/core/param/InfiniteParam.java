package org.iotwarehouse.extension.core.param;

import lombok.Builder;
import lombok.NonNull;

public class InfiniteParam extends BaseExplicitParam {

    @Builder
    private InfiniteParam(@NonNull String name) {
        super(name);
    }

    @Override
    public ValueSet getValueSet() {
        return ValueSet.ofInfinite();
    }
}
