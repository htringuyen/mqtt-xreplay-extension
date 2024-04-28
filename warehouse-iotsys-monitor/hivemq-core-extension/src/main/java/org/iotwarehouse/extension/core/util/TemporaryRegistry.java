package org.iotwarehouse.extension.core.util;

import lombok.NonNull;

import java.util.Optional;

public interface TemporaryRegistry<T extends HasTimestamp> {

    void register(@NonNull String key, @NonNull T recordable);

    Optional<T> deregister(@NonNull String key);
}
