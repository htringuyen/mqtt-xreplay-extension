package org.iotwarehouse.extension.core;

import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.datatypes.Mqtt5UserProperties;
import com.hivemq.client.mqtt.mqtt5.exceptions.Mqtt5SubAckException;
import com.hivemq.client.mqtt.mqtt5.message.Mqtt5ReasonCode;
import com.hivemq.client.mqtt.mqtt5.message.connect.Mqtt5Connect;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.Mqtt5Subscribe;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TopicPubSubIT extends ExtensionBaseIT {

    @Test
    void testTemperatureSystemSensor() {
        final var WAREHOUSE_CODE = "VN-BD-001";
        final var ROLE_AUTH_NAME = "tempsys";
        final var ROLE_AUTH_PASSWORD = "abcd1234";
        final var SENSOR_ID = "VN-BD-001|tempsys|001";

        var client = createClient(SENSOR_ID, ROLE_AUTH_NAME, ROLE_AUTH_PASSWORD);
        connectClient(client);

        helpTestSubscribeTopicsSuccess(client, WAREHOUSE_CODE + "/temperature_system/control");

        helpTestSubscribeTopicsFail(client,
                WAREHOUSE_CODE + "/temperature_system/report",
                WAREHOUSE_CODE + "/humidity_system/control",
                "invalid/topic");

        helpTestPublishTopicsSuccess(client, WAREHOUSE_CODE + "/temperature_system/report");

        helpTestPublishTopicsFail(client, createConnect(),
                WAREHOUSE_CODE + "/temperature_system/control",
                WAREHOUSE_CODE + "/humidity_system/report",
                "invalid/topic");
    }

    @Test
    void testWarehouseScopeObserver() {
        final var WAREHOUSE_CODE = "VN-BD-001";
        final var ROLE_AUTH_NAME = "whscopeobs";
        final var ROLE_AUTH_PASSWORD = "abcd1234";
        final var OBSERVER_NAME = "warehousescopeobserver01";
        final var OBSERVER_PASSWORD = "obs1234";
        final var OBSERVER_ID = OBSERVER_NAME + "xxx";

        var userProperties = Mqtt5UserProperties.builder()
                .add("OBSERVER_NAME", OBSERVER_NAME)
                .add("OBSERVER_PASSWORD", OBSERVER_PASSWORD)
                .build();

        var client = createClient(OBSERVER_ID, ROLE_AUTH_NAME, ROLE_AUTH_PASSWORD);
        connectClient(client, userProperties);

        helpTestSubscribeTopicsSuccess(client,
                WAREHOUSE_CODE + "/temperature_system/report",
                WAREHOUSE_CODE + "/humidity_system/report",
                WAREHOUSE_CODE + "/side_temperature/report/+",
                WAREHOUSE_CODE + "/side_humidity/report/+",
                WAREHOUSE_CODE + "/+/status/report",
                WAREHOUSE_CODE + "/+/sensor_connected/report",
                WAREHOUSE_CODE + "/+/sensor_disconnected/report");

        helpTestSubscribeTopicsFail(client,
                WAREHOUSE_CODE + "/temperature_system/control",
                "invalid/topic");

        /*helpTestPublishTopicsSuccess(client,
                WAREHOUSE_CODE + "/temperature_system/control",
                WAREHOUSE_CODE + "/humidity_system/control");*/

        var connect = createConnect(userProperties);
        helpTestPublishTopicsFail(client, connect,
                WAREHOUSE_CODE + "/temperature_system/report",
                "invalid/topic");
    }



    private void helpTestPublishTopicsSuccess(Mqtt5BlockingClient client, String... topics) {
        Arrays.stream(topics)
                .forEach(topic -> {
                    var pubAck = client.publish(Mqtt5Publish.builder().topic(topic).build());
                    sleepIn(100);
                    assertTrue(client.getState().isConnected());
                });
    }

    private void helpTestPublishTopicsFail(Mqtt5BlockingClient client, Mqtt5Connect forReConnect, String... topics) {
        Arrays.stream(topics)
                .forEach(topic -> {
                    var pubAck = client.publish(Mqtt5Publish.builder().topic(topic).build());
                    sleepIn(100);
                    assertFalse(client.getState().isConnected());
                    client.connect(forReConnect);
                });
    }

    private void helpTestSubscribeTopicsSuccess(Mqtt5BlockingClient client, String... topicFilters) {
        Arrays.stream(topicFilters)
                .forEach(tf -> {
                    var subAck = client.subscribe(Mqtt5Subscribe.builder()
                            .topicFilter(tf).build());
                    logReasonCodes("SuccessSubAck", subAck.getReasonCodes());
                });
    }

    private void helpTestSubscribeTopicsFail(Mqtt5BlockingClient client, String... topicFilters) {
        Arrays.stream(topicFilters)
                .forEach(tf -> {
                    var subError = assertThrows(Mqtt5SubAckException.class, () -> client.subscribe(Mqtt5Subscribe.builder()
                            .topicFilter("tf").build()));
                    logReasonCodes("FailSubAck", subError.getMqttMessage().getReasonCodes());
                });
    }

    private void logReasonCodes(String type, List<? extends Mqtt5ReasonCode> reasonCodes) {
        reasonCodes.stream()
                .map(code -> type + " reasonCode: " + code)
                .forEach(logger::info);
    }
}
