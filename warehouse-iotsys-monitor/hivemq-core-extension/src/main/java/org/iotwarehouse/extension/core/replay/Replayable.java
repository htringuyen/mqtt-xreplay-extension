package org.iotwarehouse.extension.core.replay;

import org.iotwarehouse.extension.core.util.HasTimestamp;

import java.util.List;

public interface Replayable extends HasTimestamp {

    String getClientId();

    long getTimestamp();

    int getSubscribePacketId();

    List<String> getTopics();

    static ReplayableImpl.ReplayableImplBuilder builder() {
        return new ReplayableImpl.ReplayableImplBuilder();
    }
}
