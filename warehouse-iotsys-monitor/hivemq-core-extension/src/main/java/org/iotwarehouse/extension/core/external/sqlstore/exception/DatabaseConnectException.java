package org.iotwarehouse.extension.core.external.sqlstore.exception;


public class DatabaseConnectException extends RuntimeException {

    public DatabaseConnectException() {
        super();
    }

    public DatabaseConnectException(String msg) {
        super(msg);
    }

    public DatabaseConnectException(Throwable cause) {
        super(cause);
    }

    public DatabaseConnectException(String msg, Throwable cause) {
        super(msg, cause);
    }
}