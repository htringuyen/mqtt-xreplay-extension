package org.iotwarehouse.extension.core.param;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import lombok.Builder;
import lombok.NonNull;

public class SingularParam extends BaseExplicitParam {

    private final String value;


    @Builder
    private SingularParam(@NotNull String name, @NonNull String value) {
        super(name);
        this.value = value;
    }

    public ValueSet getValueSet() {
        return ValueSet.of(value);
    }
}
