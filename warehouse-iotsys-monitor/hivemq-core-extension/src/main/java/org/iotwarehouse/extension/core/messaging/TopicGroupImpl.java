package org.iotwarehouse.extension.core.messaging;

import com.hivemq.extension.sdk.api.auth.parameter.TopicPermission;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import org.iotwarehouse.extension.core.param.ExplicitParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TopicGroupImpl implements TopicGroup {

    private final String name;

    private final TopicPattern pattern;

    private final List<Role> publishRoles;

    private final List<Role> subscribeRoles;

    public TopicGroupImpl(String name, String pattern, List<Role> publishRoles, List<Role> subscribeRoles) {
        this.name = name;
        this.pattern = new TopicPatternImpl(pattern);
        this.publishRoles = publishRoles;
        this.subscribeRoles = subscribeRoles;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TopicPattern getTopicPattern() {
        return pattern;
    }

    @Override
    public Optional<ExplicitParams> parseTopic(String topic) {
        return pattern.parseTopic(topic);
    }

    @Override
    public boolean patternMatch(String topic) {
        return pattern.match(topic);
    }

    @Override
    public String getTopicWithWildcardsFilledBy(String[] wildcardSegments) {
        return pattern.fillWildcards(wildcardSegments);
    }

    @Override
    public String getTopicWithParams(ExplicitParams params) {
        return pattern.fillParams(params);
    }

    @Override
    public List<TopicPermission> getPermissionsFor(Role role, ExplicitParams params) {
        var permissions = new ArrayList<TopicPermission>();

        if (publishPermittedFor(role)) {
            var topicFilters = pattern.getTopicFilters(params);
            permissions.addAll(
                    createPermissionsFrom(topicFilters, TopicPermission.MqttActivity.PUBLISH));
        }

        if (subscribePermittedFor(role)) {
            var topicFilters = pattern.getTopicFilters(params);
            permissions.addAll(
                    createPermissionsFrom(topicFilters, TopicPermission.MqttActivity.SUBSCRIBE));
        }

        return permissions;
    }

    private List<TopicPermission> createPermissionsFrom(List<String> topicFilters,
                                                        TopicPermission.MqttActivity activity) {
        return topicFilters.stream()
                .map(tf -> Builders.topicPermission()
                        .activity(activity)
                        .topicFilter(tf)
                        .qos(TopicPermission.Qos.ALL)
                        .type(TopicPermission.PermissionType.ALLOW)
                        .build())
                .collect(Collectors.toList());
    }

    private boolean publishPermittedFor(Role role) {
        return publishRoles.contains(role);
    }

    private boolean subscribePermittedFor(Role role) {
        return subscribeRoles.contains(role);
    }

    @Override
    public String toString() {
        return String.format("TopicGroup{ name=%s, pattern=%s, pubRoles=%s, subRoles=%s }",
                name, pattern, publishRoles, subscribeRoles);
    }


}







































