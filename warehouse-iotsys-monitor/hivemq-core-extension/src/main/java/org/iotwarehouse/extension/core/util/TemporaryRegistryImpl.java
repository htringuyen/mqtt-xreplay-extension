package org.iotwarehouse.extension.core.util;

import lombok.NonNull;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class TemporaryRegistryImpl<T extends HasTimestamp> implements TemporaryRegistry<T> {

    private final Map<String, T> temporaryMap = Collections.synchronizedMap(new HashMap<>());

    private final long cleanupPeriod;

    private final long elementExpiry;

    private final ScheduledExecutorService scheduler;

    private final AtomicBoolean shouldScheduleCleaning = new AtomicBoolean(true);

    public TemporaryRegistryImpl(long cleanupPeriod, long elementExpiry, ScheduledExecutorService scheduler) {
        this.cleanupPeriod = cleanupPeriod;
        this.elementExpiry = elementExpiry;
        this.scheduler = scheduler;
        scheduleCleaning();
    }

    @Override
    public void register(@NonNull String key, @NonNull T value) {
        temporaryMap.put(key, value);
    }

    @Override
    public Optional<T> deregister(@NonNull String key) {
        var value = temporaryMap.get(key);
        return value == null ? Optional.empty() : Optional.of(value);
    }

    private void scheduleCleaning() {
        scheduler.scheduleWithFixedDelay(() -> {
            var anchor = Instant.now().toEpochMilli();
            synchronized (temporaryMap) {
                temporaryMap.forEach((key, value) -> {
                    if (anchor - value.getTimestamp() >= elementExpiry) {
                        temporaryMap.remove(key);
                    }
                });
            }
        }, cleanupPeriod, cleanupPeriod, TimeUnit.MILLISECONDS);
    }
}


















