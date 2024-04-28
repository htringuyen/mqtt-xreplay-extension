package org.iotwarehouse.extension.core.service;

import org.iotwarehouse.extension.core.external.ExternalServices;
import org.iotwarehouse.extension.core.external.exception.ExternalServiceException;
import org.iotwarehouse.extension.core.messaging.TopicGroupManager;
import org.iotwarehouse.extension.core.messaging.TopicGroupRegistry;
import org.iotwarehouse.extension.core.messaging.TopicGroupService;
import org.iotwarehouse.extension.core.replay.PublishRecorder;
import org.iotwarehouse.extension.core.replay.RecordOptions;
import org.iotwarehouse.extension.core.replay.RecordRegistry;
import org.iotwarehouse.extension.core.replay.RecordService;
import org.iotwarehouse.extension.core.tracking.TrackingService;
import org.iotwarehouse.extension.core.tracking.TrackingServiceImpl;

public final class ExtensionServices {

    private static final PublishRecorder publishRecorder = PublishRecorder.getInstance();

    private static final TopicGroupManager topicGroupManager = TopicGroupManager.getInstance();

    private static final TrackingService trackingService = new TrackingServiceImpl();



    public static RecordService recordService() {
        return publishRecorder;
    }

    public static RecordRegistry recordRegistry() {
        return publishRecorder;
    }

    public static TopicGroupRegistry topicGroupRegistry() {
        return topicGroupManager;
    }

    public static TopicGroupService topicGroupService() {
        return topicGroupManager;
    }

    public static TrackingService trackingService() {
        return trackingService;
    }

    public static void initialize() {
        var topicGroupRegistry = ExtensionServices.topicGroupRegistry();
        var recordRegistry = ExtensionServices.recordRegistry();

        try {
            var topicGroupInfos = ExternalServices.getTopicStore().getTopicGroupInfos();

            for (var info: topicGroupInfos) {
                var topicGroup = topicGroupRegistry.register(info.name(), info.pattern(), info.publishRoles(), info.subscribeRoles());
                var recordOptions = RecordOptions.builder()
                        .recordMaxLength(info.recordMaxLength())
                        .recordPolicy(info.recordPolicy())
                        .build();
                recordRegistry.register(recordOptions, topicGroup);
            }

        } catch (ExternalServiceException e) {
            throw new RuntimeException("Load topic group info using external services failed", e);
        }
    }


}
