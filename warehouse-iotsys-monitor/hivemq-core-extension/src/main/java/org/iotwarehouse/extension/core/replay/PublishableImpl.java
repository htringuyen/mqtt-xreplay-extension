package org.iotwarehouse.extension.core.replay;

import com.hivemq.extension.sdk.api.services.builder.Builders;
import com.hivemq.extension.sdk.api.services.publish.Publish;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.nio.ByteBuffer;

@Builder(builderClassName = "Builder")
public class PublishableImpl implements Publishable {

    @Getter
    private final PayloadMessage message;

    @Getter
    private final String topic;

    private PublishableImpl(@NonNull PayloadMessage message, @NonNull String topic) {
        this.message = message;
        this.topic = topic;
    }

    @Override
    public Publish toPublish() {
        return Builders.publish()
                .topic(topic)
                .payload(toByteBuffer(message.toString()))
                .build();
    }

    private ByteBuffer toByteBuffer(String message) {
        return ByteBuffer.wrap(message.getBytes());
    }

}
