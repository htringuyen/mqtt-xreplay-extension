package org.iotwarehouse.extension.external;

import org.iotwarehouse.extension.core.external.ExternalServices;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class TopicGroupStoreTest {

    private static final Logger logger = LoggerFactory.getLogger(TopicGroupStoreTest.class);

    @Test
    public void testGetAllTopics() throws Exception {
        var topicStore = ExternalServices.getTopicStore();
        var topics = assertDoesNotThrow(topicStore::getTopicGroupInfos);
        topics.forEach(t -> logger.info(t.toString()));
    }
}
