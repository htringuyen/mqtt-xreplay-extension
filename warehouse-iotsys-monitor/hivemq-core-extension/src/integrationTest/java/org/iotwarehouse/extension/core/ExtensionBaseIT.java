package org.iotwarehouse.extension.core;

import com.hivemq.client.internal.mqtt.message.connect.connack.MqttConnAck;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.datatypes.Mqtt5UserProperties;
import com.hivemq.client.mqtt.mqtt5.message.auth.Mqtt5SimpleAuth;
import com.hivemq.client.mqtt.mqtt5.message.connect.Mqtt5Connect;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishResult;
import com.hivemq.client.mqtt.mqtt5.message.publish.puback.Mqtt5PubAck;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.testcontainers.hivemq.HiveMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Testcontainers
public class ExtensionBaseIT {
    protected final Logger logger = LoggerFactory.getLogger(ExtensionBaseIT.class);

    @Container
    protected static HiveMQContainer hivemq = new HiveMQContainer(DockerImageName.parse("hivemq/hivemq-ce").withTag("latest"))
            .withExtension(MountableFile.forClasspathResource("hivemq-core-extension"))
            .withoutPrepackagedExtensions("hivemq-allow-all-extension")
            //.withExposedPorts(1433)
            .withLogConsumer(outputFrame -> System.out.print("HiveMQ: " + outputFrame.getUtf8String()))
            .withLogLevel(Level.DEBUG);

    @BeforeEach
    void init() throws TimeoutException  {
        //hivemq.disableExtension("Allow All Extension", "/opt/hivemq/extensions/hivemq-allow-all-extension");
    }

    protected void sleepIn(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    protected Mqtt5SimpleAuth simpleAuthOf(String username, String password) {
        return Mqtt5SimpleAuth.builder()
                .username(username)
                .password(password.getBytes(StandardCharsets.UTF_8))
                .build();
    }

    protected String getSinglePropertyFrom(Mqtt5UserProperties properties, String name) {
        var propertyList = properties.asList();
        for (var property: propertyList) {
            if (property.getName().toString().equals(name)) {
                return property.getValue().toString();
            }
        }
        return null;
    }

    protected Mqtt5Connect createConnect() {
        return Mqtt5Connect.builder().build();
    }

    protected Mqtt5Connect createConnect(Mqtt5UserProperties properties) {
        return Mqtt5Connect.builder().userProperties(properties).build();
    }

    protected Mqtt5BlockingClient clientConnect(String id, String username, String password) {
        return clientConnect(id, username, password, null);
    }

    protected Mqtt5BlockingClient createClient(String id) {
        return Mqtt5Client.builder()
                .serverPort(hivemq.getMqttPort())
                .identifier(id)
                .buildBlocking();
    }

    protected Mqtt5BlockingClient createClient(String id, String username, String password) {
        return Mqtt5Client.builder()
                .serverPort(hivemq.getMqttPort())
                .identifier(id)
                .simpleAuth()
                .username(username)
                .password(password.getBytes())
                .applySimpleAuth()
                .buildBlocking();
    }

    protected Mqtt5ConnAck connectClient(Mqtt5BlockingClient client, Mqtt5UserProperties properties) {
        var connect = Mqtt5Connect.builder()
                .userProperties(properties)
                .build();
        return client.connect(connect);
    }

    protected Mqtt5ConnAck connectClient(Mqtt5BlockingClient client) {
        var connect = Mqtt5Connect.builder()
                .build();
        return client.connect(connect);
    }

    protected Mqtt5ConnAck connectClient(Mqtt5BlockingClient client, String username,
                                         String password, Mqtt5UserProperties properties) {
        if (properties == null) {
            properties = Mqtt5UserProperties.builder().build();
        }

        var connect = Mqtt5Connect.builder()
                .simpleAuth(simpleAuthOf(username, password))
                .userProperties(properties)
                .build();
        return client.connect(connect);
    }



    protected Mqtt5BlockingClient clientConnect(String id, String username, String password,
                                                Mqtt5UserProperties properties) {
        var client = Mqtt5Client.builder()
                .serverPort(hivemq.getMqttPort())
                .identifier(id)
                .buildBlocking();
        if (properties == null) {
            properties = Mqtt5UserProperties.builder().build();
        }

        var connect = Mqtt5Connect.builder()
                .simpleAuth(simpleAuthOf(username, password))
                .userProperties(properties)
                .build();
        client.connect(connect);
        return client;
    }

    protected Mqtt5PublishResult publishMessage(Mqtt5BlockingClient client, String topic, byte[] payload, MqttQos qos) {
        return client.publishWith().topic(topic).qos(qos).payload(payload).send();
    }

    protected Mqtt5PublishResult publishMessage(Mqtt5BlockingClient client, String topic, byte[] payload) {
        return publishMessage(client, topic, payload, MqttQos.EXACTLY_ONCE);
    }

    protected Mqtt5PublishResult publishMessage(Mqtt5BlockingClient client, String topic, String payload, MqttQos qos) {
        return publishMessage(client, topic, payload.getBytes(), qos);
    }

    // overload with string msg
    protected Mqtt5PublishResult publishMessage(Mqtt5BlockingClient client, String topic, String payload) {
        return publishMessage(client, topic, payload, MqttQos.EXACTLY_ONCE);
    }

    // helper method for subscribing to a topic
    protected void subscribeToTopic(Mqtt5BlockingClient client, String topic) {
        client.subscribeWith().topicFilter(topic).send();
    }

    protected String extractPayload(Mqtt5Publish publish) {
        var payload = publish.getPayloadAsBytes();
        return new String(payload, StandardCharsets.UTF_8);
    }
}




























