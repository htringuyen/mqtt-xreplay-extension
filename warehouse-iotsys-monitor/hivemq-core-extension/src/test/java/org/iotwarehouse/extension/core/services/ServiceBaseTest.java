package org.iotwarehouse.extension.core.services;

public class ServiceBaseTest {

    protected void sleepIn(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
