package org.iotwarehouse.extension.core.param;

import java.util.Optional;
import java.util.Set;

public interface ValueSet {

    boolean isInfinite();

    Set<String> values();

    Optional<String> firstValueOptional();

    String firstValue();


    int size();

    static ValueSet of(String... values) {
        return new ValueSetImpl(false, values);
    }

    static ValueSet ofInfinite() {
        return new ValueSetImpl(true);
    }
}