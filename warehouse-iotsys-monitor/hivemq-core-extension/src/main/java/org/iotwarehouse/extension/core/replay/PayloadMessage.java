package org.iotwarehouse.extension.core.replay;

import java.nio.ByteBuffer;

public interface PayloadMessage {

    long timestamp();

    byte[] content();

    String contentAsString();

    ByteBuffer contentAsByteBuffer();

    Publishable publishableWith(String topic);

    String toJson();

    static PayloadMessageImpl.Builder builder() {
        return PayloadMessageImpl.builder();
    }
}
