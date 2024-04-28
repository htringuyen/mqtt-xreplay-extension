package org.iotwarehouse.extension.core.tracking;

import java.util.Optional;

public interface TrackingService {

    void start(ConnectTracking connectTracking);

    Optional<ConnectTracking> endConnectTrackingForClient(String clientId);
}
