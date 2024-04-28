package org.iotwarehouse.extension.core.replay;

import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.publish.PublishService;

import java.util.Iterator;
import java.util.List;

public abstract class AbstractMessageRecord implements MessageRecord {

    private final long recordMaxLength;

    protected AbstractMessageRecord(long recordMaxLength) {
        this.recordMaxLength = recordMaxLength;
    }

    protected long recordMaxLength() {
        return recordMaxLength;
    }
}
