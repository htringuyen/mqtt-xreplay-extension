package org.iotwarehouse.extension.core.replay;

import java.util.Arrays;

public class RecordKey implements Comparable<RecordKey> {

    public static final String HIGHEST_SEGMENT_VALUE = "+INF";

    public static final String LOWEST_SEGMENT_VALUE = "-INF";

    private final String[] segments;

    private final boolean hasHighest;

    private final boolean hasLowest;

    public RecordKey(String... segments) {
        this.segments = segments;
        var segmentList = Arrays.asList(segments);
        hasHighest = segmentList.contains(HIGHEST_SEGMENT_VALUE);
        hasLowest = segmentList.contains(LOWEST_SEGMENT_VALUE);
    }

    public String getSegment(int index) {
        if (index < 0 || index >= segments.length) {
            throw new IllegalArgumentException(
                    "Segment index range from 0 to " + segments.length + " but input index " + index);
        }
        return segments[index];
    }

    public String[] getSegments() {
        return segments;
    }


    @Override
    public int compareTo(RecordKey other) {
        if (segments.length != other.segments.length) {
            throw new IllegalArgumentException("Cannot compare two keys with different segment length");
        }

        for (int i = 0; i < segments.length; i++) {
            var thisSegment = segments[i];
            var otherSegment = other.segments[i];

            if (this.hasHighest || other.hasHighest) {
                var highestThis = thisSegment.equals(HIGHEST_SEGMENT_VALUE);
                var highestOther = otherSegment.equals(HIGHEST_SEGMENT_VALUE);

                if (highestThis && !highestOther) {
                    return 1;
                } else if (!highestThis && highestOther) {
                    return -1;
                } else if (highestThis && highestOther) {
                    return 0;
                }
            }

            if (this.hasLowest || other.hasLowest) {
                var lowestThis = thisSegment.equals(LOWEST_SEGMENT_VALUE);
                var lowestOther = otherSegment.equals(LOWEST_SEGMENT_VALUE);

                if (lowestThis && !lowestOther) {
                    return -1;
                } else if (!lowestThis && lowestOther) {
                    return 1;
                } else if (lowestThis && lowestOther) {
                    return 0;
                }
            }

            var compareResult = thisSegment.compareTo(otherSegment);
            if (compareResult != 0) {
                return compareResult;
            }
        }
        return 0;
    }



}
