package org.iotwarehouse.extension.core.replay;

public interface RecordOptions {

    public long recordMaxLength();

    public RecordPolicy recordPolicy();

    static RecordOptionsImpl.Builder builder() {
        return new RecordOptionsImpl.Builder();
    }
}
