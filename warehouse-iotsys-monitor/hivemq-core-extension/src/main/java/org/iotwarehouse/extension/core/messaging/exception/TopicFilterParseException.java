package org.iotwarehouse.extension.core.messaging.exception;


public class TopicFilterParseException extends RuntimeException {

    public TopicFilterParseException() {
        super();
    }

    public TopicFilterParseException(String msg) {
        super(msg);
    }

    public TopicFilterParseException(Throwable cause) {
        super(cause);
    }

    public TopicFilterParseException(String msg, Throwable cause) {
        super(msg, cause);
    }
}