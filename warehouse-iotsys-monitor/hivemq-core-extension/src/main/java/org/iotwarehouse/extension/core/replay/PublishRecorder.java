package org.iotwarehouse.extension.core.replay;

import com.hivemq.extension.sdk.api.services.Services;
import lombok.NonNull;
import org.iotwarehouse.extension.core.messaging.TopicGroup;
import org.iotwarehouse.extension.core.param.ExplicitParams;
import org.iotwarehouse.extension.core.service.ExtensionServices;
import org.iotwarehouse.extension.core.util.TemporaryRegistry;
import org.iotwarehouse.extension.core.util.TemporaryRegistryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

public class PublishRecorder implements RecordRegistry, RecordService {

    private static final long ABLE_ACTION_CLEANUP_PERIOD = 2000L;

    private static final long ABLE_ACTION_EXPIRY = 200L;

    private static final PublishRecorder INSTANCE = new PublishRecorder();

    private static final Logger logger = LoggerFactory.getLogger(PublishRecorder.class);

    public static PublishRecorder getInstance() {
        return INSTANCE;
    }

    private final Map<String, RecordGroup> recordGroups = new HashMap<>();

    private final TemporaryRegistry<Recordable> recordableRegistry =
            new TemporaryRegistryImpl<>(ABLE_ACTION_CLEANUP_PERIOD, ABLE_ACTION_EXPIRY, Services.extensionExecutorService());

    private final TemporaryRegistry<Replayable> replayableRegistry =
            new TemporaryRegistryImpl<>(ABLE_ACTION_CLEANUP_PERIOD, ABLE_ACTION_EXPIRY, Services.extensionExecutorService());

    private PublishRecorder() {

    }

    @Override
    public void register(RecordOptions options, TopicGroup topicGroup) {

       var recordFactory = MessageRecordFactory.with(options);

       var requiredParams = topicGroup.getTopicPattern().requiredParams();
       var recordGroup = new RecordGroupImpl(recordFactory, requiredParams);

       recordGroups.put(topicGroup.getName(), recordGroup);
    }

    @Override
    public void markRecordable(@NonNull Recordable recordable) {
        var key = generateRecordableKey(recordable.getPublishPacketId(), recordable.getClientId());
        recordableRegistry.register(key, recordable);
    }

    @Override
    public boolean recordIfAble(int packetId, @NonNull String clientId) {
        var key = generateRecordableKey(packetId, clientId);

        var recordableOpt = recordableRegistry.deregister(key);
        if (recordableOpt.isEmpty()) return false;

        var recordable = recordableOpt.get();

        var topic = recordable.getTopic();
        var topicGroup = findTopicGroup(topic);
        var params = parseTopicParams(topic, topicGroup);

        var recordGroup = recordGroups.get(topicGroup.getName());
        var record = recordGroup.getRecordOrCreateIfAbsentWith(params);
        record.addMessage(createPayloadMessageFrom(recordable));
        return true;
    }

    @Override
    public void markReplayable(@NonNull Replayable replayable) {
        var key = generateReplayableKey(replayable.getSubscribePacketId(), replayable.getClientId());
        replayableRegistry.register(key, replayable);
    }

    @Override
    public boolean replayIfAble(int packetId, @NonNull String clientId, List<Integer> topicsIndexes) {
        var key = generateReplayableKey(packetId, clientId);

        var replayableOpt = replayableRegistry.deregister(key);
        if (replayableOpt.isEmpty()) return false;

        var replayable = replayableOpt.get();

        var topics = replayable.getTopics();

        topicsIndexes.stream()
                .map(topics::get)
                .forEach(topic -> replayForClient(topic, clientId));
        return true;
    }

    @Override
    public void replayForClient(String topic, String clientId) {
        var publishService = Services.publishService();
        replayForConsumer(topic, publishable -> {
            var pubFuture = publishService.publishToClient(publishable.toPublish(), clientId);
            pubFuture.whenComplete((result, throwable) -> {
                if (throwable != null) {
                    logger.debug("Error when publish replay", throwable);
                } else {
                    logger.debug("Publish replay result: {}", result.name());
                }
            });
        });
    }

    @Override
    public void replayForConsumer(String topic, Consumer<Publishable> consumer) {
        var topicGroup = findTopicGroup(topic);

        var params = parseTopicParams(topic, topicGroup);

        var recordGroup = recordGroups.get(topicGroup.getName());
        var recordEntries = recordGroup.findRecordsWith(params);

        for (var e: recordEntries) {
            var key = e.getKey();
            var publishTopic = topicGroup.getTopicWithWildcardsFilledBy(key.getSegments());
            var record = e.getValue();

            for (var message: record.getMessages()) {
                logger.debug("Replay message: {}", message);
                consumer.accept(message.publishableWith(publishTopic));
            }
        }
    }

    private TopicGroup findTopicGroup(String topic) {
        var topicGroupOpt = ExtensionServices.topicGroupService().findTopicGroupMatchTopic(topic);
        if (topicGroupOpt.isEmpty()) {
            throw new IllegalArgumentException("Topic not found " + topic);
        }

        return topicGroupOpt.get();
    }

    private ExplicitParams parseTopicParams(String topic, TopicGroup group) {
        var paramsOpt = group.parseTopic(topic);
        if (paramsOpt.isEmpty()) {
            throw new IllegalArgumentException("Can not parse param for topic " + topic);
        }
        return paramsOpt.get();
    }

    private PayloadMessage createPayloadMessageFrom(Recordable recordable) {
        return PayloadMessage.builder()
                .content(recordable.getPayload())
                .timestamp(recordable.getTimestamp())
                .build();
    }

    private String generateRecordableKey(int packetId, String clientId) {
        return clientId + "-" + packetId;
    }

    private String generateReplayableKey(int packetId, String clientId) {
        return clientId + "-" + packetId;
    }

}




























