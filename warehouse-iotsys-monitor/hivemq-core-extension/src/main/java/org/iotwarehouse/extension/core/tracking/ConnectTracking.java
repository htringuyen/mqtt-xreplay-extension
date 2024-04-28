package org.iotwarehouse.extension.core.tracking;

import org.iotwarehouse.extension.core.param.ExplicitParams;
import org.iotwarehouse.extension.core.util.HasTimestamp;

public interface ConnectTracking extends HasTimestamp {

    String getClientId();

    ExplicitParams getExplicitParams();

    static ConnectTrackingImpl.ConnectTrackingImplBuilder builder() {
        return new ConnectTrackingImpl.ConnectTrackingImplBuilder();
    }
}
