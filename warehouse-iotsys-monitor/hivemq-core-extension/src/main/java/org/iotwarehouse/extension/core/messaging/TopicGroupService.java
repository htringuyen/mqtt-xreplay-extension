package org.iotwarehouse.extension.core.messaging;

import com.hivemq.extension.sdk.api.auth.parameter.TopicPermission;
import org.iotwarehouse.extension.core.param.ExplicitParams;

import java.util.List;
import java.util.Optional;

public interface TopicGroupService {

    List<TopicPermission> getTopicPermissionsFor(Role role, ExplicitParams params);

    Optional<TopicGroup> findTopicGroupMatchTopic(String topic);

    Optional<TopicGroup> findTopicGroupByName(String name);
}
