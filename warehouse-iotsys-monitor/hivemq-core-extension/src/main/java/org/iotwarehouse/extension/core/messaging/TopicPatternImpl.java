package org.iotwarehouse.extension.core.messaging;

import org.iotwarehouse.extension.core.messaging.exception.TopicFilterParseException;
import org.iotwarehouse.extension.core.param.ExplicitParam;
import org.iotwarehouse.extension.core.param.ExplicitParams;
import org.iotwarehouse.extension.core.param.InfiniteParam;
import org.iotwarehouse.extension.core.param.SingularParam;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TopicPatternImpl implements TopicPattern {

    private static final String WILDCARD_SEGMENT = "+";

    private static final String SEGMENT_SEP = "/";

    private final String pattern;

    private final String[] segments;

    private final String[] requiredParams;

    public TopicPatternImpl(String pattern) {
        this.pattern = pattern;
        this.segments = pattern.split("/");
        requiredParams = Arrays.stream(segments)
                .filter(this::isPlaceholder)
                .map(this::paramNameFrom)
                .toArray(String[]::new);
    }

    @Override
    public String[] requiredParams() {
        return requiredParams;
    }

    @Override
    public boolean match(String topic) {
        var iSegments = topic.split(SEGMENT_SEP);
        if (iSegments.length != segments.length) {
            return false;
        }

        for (int i = 0; i < segments.length; i++) {
            var segment = segments[i];
            var iSegment = iSegments[i];
            if (!isPlaceholder(segment) && !iSegment.equals(segment)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String fillParams(ExplicitParams params) {
        var builder = new StringBuilder();
        for (int i = 0; i < segments.length; i++) {
            var segment = segments[i];
            if (isPlaceholder(segment)) {
                var paramOpt = params.get(paramNameFrom(segment));
                if (paramOpt.isEmpty()) {
                    return null;
                }

                var valueSet = paramOpt.get().getValueSet();
                if (valueSet.isInfinite() || valueSet.size() != 1) {
                    throw new IllegalStateException("Infinite or plural number of value is not excepted");
                }

                builder.append(valueSet.firstValue());
            } else {
                builder.append(segment);
            }

            if (i != segments.length - 1) {
                builder.append(SEGMENT_SEP);
            }
        }
        return builder.toString();
    }

    @Override
    public String fillWildcards(String[] wildcardSegments) {
        var builder = new StringBuilder();
        var wildcardIndex = 0;
        for (int i = 0; i < segments.length; i++) {
            var segment = segments[i];
            if (isPlaceholder(segment)) {
                builder.append(wildcardSegments[wildcardIndex++]);
            } else {
                builder.append(segment);
            }

            if (i != segments.length - 1) {
                builder.append(SEGMENT_SEP);
            }
        }
        return builder.toString();
    }

    @Override
    public List<String> getTopicFilters(ExplicitParams params) {
        var resultStream = Stream.of("");
        for (var segment: segments) {
            if (isPlaceholder(segment)) {

                var paramName = paramNameFrom(segment);
                var param = params.get(paramName);
                if (param.isEmpty()) {
                    throw new TopicFilterParseException("Required param [" +paramName + "] not found.");
                }
                var valueSet = param.get().getValueSet();
                if (valueSet.isInfinite()) {
                    resultStream = concatSegmentForEachIn(resultStream, WILDCARD_SEGMENT);
                } else {
                    resultStream = concatEachSegmentForEachIn(resultStream, valueSet.values());
                }
            } else {
                resultStream = concatSegmentForEachIn(resultStream, segment);
            }
        }
        return resultStream.collect(Collectors.toList());
    }

    @Override
    public Optional<ExplicitParams> parseTopic(String topic) {
        var iSegments = topic.split(SEGMENT_SEP);
        if (iSegments.length != segments.length) {
            return Optional.empty();
        }

        var paramList = new ArrayList<ExplicitParam>();
        for (var i = 0; i < iSegments.length; i++) {
            var segment = segments[i];
            var iSegment = iSegments[i];
            if (isPlaceholder(segment)) {
                if (iSegment.equals(WILDCARD_SEGMENT)) {
                    paramList.add(InfiniteParam.builder()
                            .name(paramNameFrom(segment))
                            .build());
                } else {
                    paramList.add(SingularParam.builder()
                            .name(paramNameFrom(segment))
                            .value(iSegment)
                            .build());
                }
            } else {
                if (!iSegment.equals(segment)) {
                    return Optional.empty();
                }
            }
        }

        return Optional.of(ExplicitParams.from(paramList));
    }

    private Stream<String> concatEachSegmentForEachIn(Stream<String> stream, Collection<String> segments) {
        return stream.flatMap( s -> segments.stream().map(segment -> s.isEmpty() ? segment : s + SEGMENT_SEP + segment));
    }

    private Stream<String> concatSegmentForEachIn(Stream<String> stream, String segment) {
        return stream.map(s -> s.isEmpty() ? segment : s + SEGMENT_SEP + segment);
    }

    private String paramNameFrom(String s) {
        return s.substring(1);
    }

    private boolean isPlaceholder(String s) {
        return s.charAt(0) == '?';
    }

    @Override
    public String toString() {
        return pattern;
    }
}




































