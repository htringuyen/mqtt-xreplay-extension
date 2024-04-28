package org.iotwarehouse.extension.core.tracking;

import com.hivemq.extension.sdk.api.services.Services;
import org.iotwarehouse.extension.core.util.TemporaryRegistry;
import org.iotwarehouse.extension.core.util.TemporaryRegistryImpl;

import java.util.Optional;

public class TrackingServiceImpl implements TrackingService {

    private static final long INFO_CLEANUP_PERIOD = 5000L;

    private static final long INFO_EXPIRY = 200L;

    private final TemporaryRegistry<ConnectTracking> temporaryRegistry =
            new TemporaryRegistryImpl<>(
                    INFO_CLEANUP_PERIOD, INFO_EXPIRY, Services.extensionExecutorService());

    public TrackingServiceImpl() {

    }

    @Override
    public void start(ConnectTracking connectTracking) {
        temporaryRegistry.register(connectTracking.getClientId(), connectTracking);
    }

    @Override
    public Optional<ConnectTracking> endConnectTrackingForClient(String clientId) {
        return temporaryRegistry.deregister(clientId);
    }
}
