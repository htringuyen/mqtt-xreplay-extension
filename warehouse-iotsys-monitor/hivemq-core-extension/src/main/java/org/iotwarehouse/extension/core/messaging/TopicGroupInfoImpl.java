package org.iotwarehouse.extension.core.messaging;

import lombok.Builder;
import org.iotwarehouse.extension.core.replay.RecordPolicy;

import java.util.List;

@Builder(builderClassName = "Builder")
public class TopicGroupInfoImpl implements TopicGroupInfo {

    private final String name;

    private final String pattern;

    private final Long recordMaxLength;

    private final RecordPolicy recordPolicy;

    private final List<Role> publishRoles;

    private final List<Role> subscribeRoles;

    public String name() {
        return name;
    }

    public String pattern() {
        return pattern;
    }

    public Long recordMaxLength() {
        return recordMaxLength;
    }

    public RecordPolicy recordPolicy() {
        return recordPolicy;
    }

    public List<Role> publishRoles() {
        return publishRoles;
    }

    public List<Role> subscribeRoles() {
        return subscribeRoles;
    }
}
