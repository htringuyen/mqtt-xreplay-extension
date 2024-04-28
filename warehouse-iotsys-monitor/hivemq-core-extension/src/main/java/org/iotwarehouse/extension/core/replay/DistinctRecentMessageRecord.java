package org.iotwarehouse.extension.core.replay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

public class DistinctRecentMessageRecord extends AbstractMessageRecord {

    private final TreeSet<PayloadMessage> messages;

    private final ReentrantLock lock = new ReentrantLock();

    public DistinctRecentMessageRecord(long recordMaxLength) {
        super(recordMaxLength);
        messages = new TreeSet<PayloadMessage>((m1, m2) -> {
            var contentComp = Arrays.compare(m1.content(), m2.content());
            if (contentComp == 0) {
                return contentComp;
            }
            var longComp = Long.compare(m1.timestamp(), m2.timestamp());
            if (longComp != 0) {
                return longComp;
            }
            return contentComp;
        });
    }

    @Override
    public List<PayloadMessage> getMessages() {
        lock.lock();
        try {
            return new ArrayList<>(messages);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void addMessage(PayloadMessage message) {
        lock.lock();
        try {
            messages.remove(message);

            if (messages.size() >= recordMaxLength()) {
                messages.pollFirst();
            }

            messages.add(message);
        } finally {
            lock.unlock();
        }
    }


}
