package org.iotwarehouse.extension.core.messaging;

import com.hivemq.extension.sdk.api.auth.parameter.TopicPermission;
import org.iotwarehouse.extension.core.param.ExplicitParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class TopicGroupManager implements TopicGroupRegistry, TopicGroupService {

    private static final Logger logger = LoggerFactory.getLogger(TopicGroupManager.class);

    private static final TopicGroupManager INSTANCE = new TopicGroupManager();

    public static TopicGroupManager getInstance() {
        return INSTANCE;
    }


    private final Map<String, TopicGroup> topicGroupMap = new HashMap<>();

    private TopicGroupManager() {

    }

    @Override
    public List<TopicPermission> getTopicPermissionsFor(Role role, ExplicitParams params) {
        return topicGroupMap.values()
                .stream()
                .flatMap(t -> t.getPermissionsFor(role, params).stream())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TopicGroup> findTopicGroupMatchTopic(String s) {
        return topicGroupMap.values()
                .stream()
                .filter(tg -> tg.patternMatch(s))
                .findFirst();
    }

    @Override
    public TopicGroup register(String name, String pattern, List<Role> pubRoles, List<Role> subRoles) {
        var topicGroup = new TopicGroupImpl(name, pattern, pubRoles, subRoles);
        topicGroupMap.put(name, topicGroup);
        return topicGroup;
    }

    @Override
    public Optional<TopicGroup> findTopicGroupByName(String name) {
        var group = topicGroupMap.get(name);
        if (group == null) {
            return Optional.empty();
        }
        return Optional.of(group);
    }
}
