package org.iotwarehouse.extension.core.messaging;

import org.iotwarehouse.extension.core.param.ExplicitParams;

import java.util.List;
import java.util.Optional;

public interface TopicPattern {

    List<String> getTopicFilters(ExplicitParams params);

    Optional<ExplicitParams> parseTopic(String topic);

    String[] requiredParams();

    boolean match(String topic);

    String fillWildcards(String[] wildcardSegments);

    String  fillParams(ExplicitParams params);
}
