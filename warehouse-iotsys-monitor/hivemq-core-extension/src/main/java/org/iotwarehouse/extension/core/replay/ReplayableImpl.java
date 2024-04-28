package org.iotwarehouse.extension.core.replay;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

public final class ReplayableImpl implements Replayable {

    @Getter
    private final long timestamp;

    @Getter
    private final int subscribePacketId;

    @Getter
    private final String clientId;

    @Getter
    private final List<String> topics;

    @Builder
    ReplayableImpl(int subscribePacketId, String clientId, List<String> topics) {
        this.subscribePacketId = subscribePacketId;
        this.clientId = clientId;
        this.topics = topics;
        this.timestamp = Instant.now().toEpochMilli();
    }

    @Override
    public String toString() {
        return String.format("Replayable{ packetId=%d, timestamp=%d, clientId=%s, topics=%s }",
                subscribePacketId, timestamp, clientId, topics);
    }
}
