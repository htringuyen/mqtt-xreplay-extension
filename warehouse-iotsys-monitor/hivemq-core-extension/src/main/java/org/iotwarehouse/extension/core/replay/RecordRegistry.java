package org.iotwarehouse.extension.core.replay;

import org.iotwarehouse.extension.core.messaging.TopicGroup;

public interface RecordRegistry {

    void register(RecordOptions config, TopicGroup topicGroup);
}
