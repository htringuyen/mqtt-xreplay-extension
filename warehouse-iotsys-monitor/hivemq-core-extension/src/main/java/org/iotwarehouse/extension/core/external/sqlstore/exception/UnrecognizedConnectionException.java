package org.iotwarehouse.extension.core.external.sqlstore.exception;


public class UnrecognizedConnectionException extends RuntimeException {

    public UnrecognizedConnectionException() {
        super();
    }

    public UnrecognizedConnectionException(String msg) {
        super(msg);
    }

    public UnrecognizedConnectionException(Throwable cause) {
        super(cause);
    }

    public UnrecognizedConnectionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}