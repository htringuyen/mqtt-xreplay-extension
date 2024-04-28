package org.iotwarehouse.extension.core.param;


public class InfiniteValueSetException extends RuntimeException {

    public InfiniteValueSetException() {
        super();
    }

    public InfiniteValueSetException(String msg) {
        super(msg);
    }

    public InfiniteValueSetException(Throwable cause) {
        super(cause);
    }

    public InfiniteValueSetException(String msg, Throwable cause) {
        super(msg, cause);
    }
}