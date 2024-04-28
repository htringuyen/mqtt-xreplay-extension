package org.iotwarehouse.extension.core.external.exception;


public class AuthenticationException extends Exception {

    public AuthenticationException() {
        super();
    }

    public AuthenticationException(String msg) {
        super(msg);
    }

    public AuthenticationException(Throwable cause) {
        super(cause);
    }

    public AuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
