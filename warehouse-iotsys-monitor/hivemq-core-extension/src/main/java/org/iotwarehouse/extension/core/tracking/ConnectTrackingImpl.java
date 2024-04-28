package org.iotwarehouse.extension.core.tracking;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.iotwarehouse.extension.core.param.ExplicitParams;

import java.time.Instant;

public class ConnectTrackingImpl implements ConnectTracking {

    @Getter
    private final String clientId;

    @Getter
    private final ExplicitParams explicitParams;

    @Getter
    private final long timestamp;

    @Builder
    public ConnectTrackingImpl(@NonNull String clientId, @NonNull ExplicitParams explicitParams) {
        this.clientId = clientId;
        this.explicitParams = explicitParams;
        this.timestamp = Instant.now().toEpochMilli();
    }
}
