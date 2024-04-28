package org.iotwarehouse.extension.core.replay;

import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import lombok.NonNull;

import java.util.List;
import java.util.function.Consumer;

public interface RecordService {

    boolean recordIfAble(int packetId, @NonNull String clientId);

    void markRecordable(@NonNull Recordable recordable);

    void markReplayable(@NonNull Replayable replayable);

    boolean replayIfAble(int packetId, @NonNull String clientId, List<Integer> topicIndexes);

    void replayForClient(String topic, String clientId);

    void replayForConsumer(String topic, Consumer<Publishable> consumer);
}
