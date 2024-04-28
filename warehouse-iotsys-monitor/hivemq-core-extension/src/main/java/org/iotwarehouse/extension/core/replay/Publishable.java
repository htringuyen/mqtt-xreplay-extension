package org.iotwarehouse.extension.core.replay;

import com.hivemq.extension.sdk.api.services.publish.Publish;

public interface Publishable {

    PayloadMessage getMessage();

    String getTopic();

    Publish toPublish();

    /* builder */
    static PublishableImpl.Builder builder() {
        return new PublishableImpl.Builder();
    }
}
