package org.iotwarehouse.extension.core.replay;

import org.iotwarehouse.extension.core.util.HasTimestamp;

public interface Recordable extends HasTimestamp {

    String getTopic();

    byte[] getPayload();

    long getTimestamp();

    String getClientId();

    int getPublishPacketId();

    static RecordableImpl.RecordableImplBuilder builder() {
        return new RecordableImpl.RecordableImplBuilder();
    }
}
