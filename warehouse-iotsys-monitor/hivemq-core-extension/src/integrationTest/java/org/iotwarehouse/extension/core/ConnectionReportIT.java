package org.iotwarehouse.extension.core;

import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.datatypes.Mqtt5UserProperties;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.N;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConnectionReportIT extends ExtensionBaseIT {

    @Test
    void testReportConnection() {
        final var OBS_ROLE_AUTH_NAME = "toplevelman";
        final var OBS_ROLE_AUTH_PASSWORD = "abcd1234";
        final var OBSERVER_NAME = "toplevelmanager01";
        final var OBSERVER_PASSWORD = "obs1234";
        final var OBSERVER_ID = OBSERVER_NAME + "xxx";

        final var SSR_ROLE_AUTH_NAME = "tempsys";
        final var SSR_ROLE_AUTH_PASSWORD = "abcd1234";
        final var warehouseList = new ArrayList<String>();
        final var sensorIdList = new ArrayList<String>();

        var N_SENSORS = 10;

        for (int i = 1; i <= N_SENSORS; i++) {
            warehouseList.add(String.format("VN-BD-%03d", i));
            sensorIdList.add(String.format("VN-BD-%03d|tempsys|001", i));
        }

        var obsClient = createClient(OBSERVER_ID, OBS_ROLE_AUTH_NAME, OBS_ROLE_AUTH_PASSWORD);
        var userProperties = Mqtt5UserProperties.builder()
                .add("OBSERVER_NAME", OBSERVER_NAME)
                .add("OBSERVER_PASSWORD", OBSERVER_PASSWORD)
                .build();
        connectClient(obsClient, userProperties);

        final var CONNECTED_COUNT = new AtomicInteger(0);
        final var DISCONNECTED_COUNT = new AtomicInteger(0);

        final var sensorClients = new ArrayList<Mqtt5BlockingClient>();

        for (int i = 0; i < N_SENSORS / 2; i++) {
            var sensorId = sensorIdList.get(i);
            var client = clientConnect(sensorId, SSR_ROLE_AUTH_NAME, SSR_ROLE_AUTH_PASSWORD);
            sensorClients.add(client);
        }

        sleepIn(200);

        obsClient.toAsync()
                .subscribeWith()
                .topicFilter("+/NOA/sensor_connected/report")
                .callback(publish -> {
                    logger.info("Received connected sensor id: {}", extractPayload(publish));
                    CONNECTED_COUNT.getAndIncrement();
                })
                .send();

        sleepIn(200);
        assertEquals(N_SENSORS / 2, CONNECTED_COUNT.get());

        for (int i = N_SENSORS / 2; i < N_SENSORS; i++) {
            var sensorId = sensorIdList.get(i);
            var client = clientConnect(sensorId, SSR_ROLE_AUTH_NAME, SSR_ROLE_AUTH_PASSWORD);
            sensorClients.add(client);
        }

        sleepIn(200);
        assertEquals(N_SENSORS, CONNECTED_COUNT.get());

        for (int i = 0; i < N_SENSORS / 2; i++) {
            var client = sensorClients.get(i);
            client.disconnect();
        }

        sleepIn(200);

        obsClient.toAsync()
                .subscribeWith()
                .topicFilter("+/+/sensor_disconnected/report")
                .callback(publish -> {
                    logger.info("Received disconnected sensor id: {}", extractPayload(publish));
                    DISCONNECTED_COUNT.getAndIncrement();
                })
                .send();

        sleepIn(200);
        assertEquals(N_SENSORS / 2, DISCONNECTED_COUNT.get());

        for (int i = N_SENSORS / 2; i < N_SENSORS; i++) {
            var client = sensorClients.get(i);
            client.disconnect();
        }

        sleepIn(200);
        assertEquals(N_SENSORS, DISCONNECTED_COUNT.get());
    }
}
