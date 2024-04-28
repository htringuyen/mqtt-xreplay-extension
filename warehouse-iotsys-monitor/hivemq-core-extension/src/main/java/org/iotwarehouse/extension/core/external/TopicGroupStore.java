package org.iotwarehouse.extension.core.external;

import org.iotwarehouse.extension.core.external.exception.ExternalServiceException;
import org.iotwarehouse.extension.core.messaging.TopicGroup;
import org.iotwarehouse.extension.core.messaging.TopicGroupInfo;

import java.util.List;

public interface TopicGroupStore {

    public List<TopicGroupInfo> getTopicGroupInfos() throws ExternalServiceException;

}
