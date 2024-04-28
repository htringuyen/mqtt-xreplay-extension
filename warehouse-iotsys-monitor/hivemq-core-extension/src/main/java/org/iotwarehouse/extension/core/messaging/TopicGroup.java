package org.iotwarehouse.extension.core.messaging;

import com.hivemq.extension.sdk.api.auth.parameter.TopicPermission;
import org.iotwarehouse.extension.core.param.ExplicitParams;

import java.util.*;

public interface TopicGroup {

    String getName();

    List<TopicPermission> getPermissionsFor(Role role, ExplicitParams params);

    Optional<ExplicitParams> parseTopic (String topic);

    TopicPattern getTopicPattern();

    boolean patternMatch(String topic);

    String getTopicWithWildcardsFilledBy(String[] wildcardSegments);

    String getTopicWithParams(ExplicitParams params);


    /****** Builder ********/

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String pattern;
        private final List<Role> publishRoles = new ArrayList<>();
        private final List<Role> subscribeRoles = new ArrayList<>();

        private Builder() {

        }

        public Builder topicGroupName(String name) {
            this.name = name;
            return this;
        }

        public Builder topicPattern(String pattern) {
            this.pattern = pattern;
            return this;
        }

        public Builder addPublishRole(Role role) {
            publishRoles.add(role);
            return this;
        }

        public Builder addSubscribeRole(Role role) {
            subscribeRoles.add(role);
            return this;
        }

        public TopicGroup build() {
            Objects.requireNonNull(name);
            Objects.requireNonNull(pattern);
            return new TopicGroupImpl(name, pattern, publishRoles, subscribeRoles);
        }

    }
}
