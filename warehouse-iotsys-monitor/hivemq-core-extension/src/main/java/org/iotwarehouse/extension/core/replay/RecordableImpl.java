package org.iotwarehouse.extension.core.replay;

import lombok.Builder;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Objects;

public class RecordableImpl implements Recordable {

    private final String topic;

    private final byte[] payload;

    private final long timestamp;

    private final int cachedHashCode;

    private final int packetId;

    private final String clientId;

    @Builder()
    protected RecordableImpl(@NonNull String topic, byte[] payload, long timestamp, int packetId, @NonNull String clientId) {

        if (payload == null || timestamp == 0) {
            throw new IllegalStateException(
                    "There are no default values for payload and timestamp so they must be set");
        }
        this.topic = topic;
        this.payload = payload;
        this.timestamp = timestamp;
        this.packetId = packetId;
        this.clientId = clientId;
        this.cachedHashCode = calculateHashCode();
    }

    @Override
    public String getTopic() {
        return topic;
    }

    @Override
    public byte[] getPayload() {
        return payload;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int getPublishPacketId() {
        return packetId;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    private int calculateHashCode() {
        return Objects.hash(topic, Arrays.hashCode(payload), timestamp);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Recordable) {
            var other = (Recordable) obj;
            return other.getTimestamp() == this.getTimestamp()
                    && Arrays.equals(other.getPayload(), this.getPayload())
                    && other.getTopic().equals(this.getTopic());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return cachedHashCode;
    }

    @Override
    public String toString() {
        return String.format("Recordable{ topic=%s, payload=%s, timestamp=%s }",
                getTopic(), new String(getPayload()), getTimestamp());
    }


}
























