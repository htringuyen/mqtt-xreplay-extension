package org.iotwarehouse.extension.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class AuthenticationIT extends ExtensionBaseIT {

    @Test
    void testConnectDuplicatedSensors() {
        final var ROLE_NAME = "TEMPERATURE_SYSTEM_SENSOR";
        final var ROLE_AUTH_NAME = "tempsys";
        final var ROLE_AUTH_PASSWORD = "abcd1234";
        final var SENSOR_ID = "VN-BD-001|tempsys|01";

        var client = createClient(SENSOR_ID, ROLE_AUTH_NAME, ROLE_AUTH_PASSWORD);

        var connAck = connectClient(client);

        assertEquals(ROLE_NAME, getSinglePropertyFrom(connAck.getUserProperties(), "role"));
    }
}


