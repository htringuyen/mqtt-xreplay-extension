package org.iotwarehouse.extension.core.replay;

import java.util.List;

public interface MessageRecord {

    void addMessage(PayloadMessage message);

    List<PayloadMessage> getMessages();
}
