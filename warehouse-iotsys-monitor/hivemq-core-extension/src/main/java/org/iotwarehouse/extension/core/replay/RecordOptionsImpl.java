package org.iotwarehouse.extension.core.replay;

import lombok.Builder;

@Builder(builderClassName = "Builder")
public class RecordOptionsImpl implements RecordOptions {

    private final long recordMaxLength;

    private final RecordPolicy recordPolicy;


    public long recordMaxLength() {
        return recordMaxLength;
    }

    public RecordPolicy recordPolicy() {
        return recordPolicy;
    }

}
