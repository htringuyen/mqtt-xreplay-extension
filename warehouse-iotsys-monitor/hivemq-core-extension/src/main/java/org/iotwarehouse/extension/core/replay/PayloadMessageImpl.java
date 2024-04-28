package org.iotwarehouse.extension.core.replay;

import lombok.Builder;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

@Builder(builderClassName = "Builder")
public class PayloadMessageImpl implements PayloadMessage {

    private final byte[] content;

    private final long timestamp;

    public PayloadMessageImpl(byte[] content, Long timestamp) {
        this.content = content;
        this.timestamp = timestamp == null ? Instant.now().getEpochSecond() : timestamp;
    }


    @Override
    public byte[] content() {
        return content;
    }

    @Override
    public String contentAsString() {
        return new String(content);
    }

    @Override
    public ByteBuffer contentAsByteBuffer() {
        return ByteBuffer.wrap(content);
    }

    @Override
    public long timestamp() {
        return timestamp;
    }

    @Override
    public String toJson() {
        return String.format("{\"content\":%s,\"timestamp\":%d}", contentAsString(), timestamp);
    }

    @Override
    public Publishable publishableWith(String topic) {
        return Publishable.builder()
                .message(this)
                .topic(topic)
                .build();
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PayloadMessage) {
            var other = (PayloadMessage) obj;
            return timestamp() == other.timestamp() && Arrays.equals(content(), other.content());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(content), timestamp);
    }

    @Override
    public String toString() {
        return String.format("PayloadMessageImpl{ content=%s, timestamp=%d }", contentAsString(), timestamp());
    }
}


































