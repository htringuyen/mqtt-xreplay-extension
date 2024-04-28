package org.iotwarehouse.extension.core.param;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

public class ValueSetImpl implements ValueSet {

    private final TreeSet<String> values;

    @Getter
    private final boolean infinite;

    ValueSetImpl(boolean infinite, String... values) {
        if (infinite && values.length > 0) {
            throw new IllegalStateException("Cannot pass values to an infinite set");
        }

        this.infinite = infinite;
        this.values = infinite ? null : new TreeSet<>(Arrays.asList(values));
    }

    public Set<String> values() {
        if (isInfinite()) {
            throw new InfiniteValueSetException("Cannot get values from an infinite set");
        }
        return values;
    }

    @Override
    public String toString() {
        return isInfinite() ? "...INFINITE_SET..." : values.toString();
    }

    @Override
    public Optional<String> firstValueOptional() {
        if (values.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(values.first());
    }

    @Override
    public String firstValue() {
        return values.first();
    }

    @Override
    public int size() {
        return values.size();
    }
}
