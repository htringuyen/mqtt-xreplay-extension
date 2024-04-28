package org.iotwarehouse.extension.core.messaging;

import org.iotwarehouse.extension.core.replay.RecordPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface TopicGroupInfo {

    String name();

    String pattern();

    Long recordMaxLength();

    RecordPolicy recordPolicy();

    List<Role> publishRoles();

    List<Role> subscribeRoles();

    static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String pattern;
        private Long recordMaxLength;
        private RecordPolicy recordPolicy;
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

        public Builder recordMaxLength(Long length) {
            this.recordMaxLength = length;
            return this;
        }

        public Builder recordPolicy(RecordPolicy policy) {
            this.recordPolicy = policy;
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

        public TopicGroupInfo build() {
            Objects.requireNonNull(name);
            Objects.requireNonNull(pattern);
            Objects.requireNonNull(recordMaxLength);
            Objects.requireNonNull(recordPolicy);
            return new TopicGroupInfoImpl(name, pattern, recordMaxLength, recordPolicy, publishRoles, subscribeRoles);
        }

    }
}
