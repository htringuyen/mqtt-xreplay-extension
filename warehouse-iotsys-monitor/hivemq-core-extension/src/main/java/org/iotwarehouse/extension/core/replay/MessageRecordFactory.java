package org.iotwarehouse.extension.core.replay;

@FunctionalInterface
public interface MessageRecordFactory {

    public MessageRecord getInstance();

    public static MessageRecordFactory with(RecordOptions options) {
        switch (options.recordPolicy()) {
            case RECENT_CONTENTS:
                return () -> new RecentMessageRecord(options.recordMaxLength());
            case DISTINCT_RECENT_CONTENTS:
                return () -> new DistinctRecentMessageRecord(options.recordMaxLength());
            default:
                throw new IllegalArgumentException("Unexpected implementation error");
        }
    }
}
