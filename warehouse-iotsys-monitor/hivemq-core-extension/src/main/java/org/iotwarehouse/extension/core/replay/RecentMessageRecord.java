package org.iotwarehouse.extension.core.replay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class RecentMessageRecord extends AbstractMessageRecord {

    private static final Logger logger = LoggerFactory.getLogger(RecentMessageRecord.class);

    private final TreeSet<PayloadMessage> messages;

    private final ReentrantLock lock = new ReentrantLock();

    public RecentMessageRecord(long recordMaxLength) {
        super(recordMaxLength);
        messages = new TreeSet<>((m1, m2) -> {
            var longComp = Long.compare(m1.timestamp(), m2.timestamp());
            if (longComp != 0) return longComp;
            return Arrays.compare(m1.content(), m2.content());
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
            if (messages.size() >= recordMaxLength()) {
                messages.pollFirst();
                messages.add(message);
            } else {
                messages.add(message);
            }
            logger.debug("Record size = {} after added {}", messages.size(), message);
        } finally {
            lock.unlock();
        }
    }
}
