package org.iotwarehouse.extension.core.handler;

import com.hivemq.extension.sdk.api.interceptor.connack.ConnackOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.connack.parameter.ConnackOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.connack.parameter.ConnackOutboundOutput;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import org.iotwarehouse.extension.core.replay.Recordable;
import org.iotwarehouse.extension.core.service.ExtensionServices;
import org.iotwarehouse.extension.core.util.ByteUtils;
import org.iotwarehouse.extension.core.util.MqttCodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class ConnackOutboundInterceptorImpl implements ConnackOutboundInterceptor {

    private static final String CONNECTED_INFO_TOPIC = "SENSOR_CONNECTED_REPORT";

    private static final Qos PUBLISH_QOS = Qos.AT_LEAST_ONCE;
    private static final Logger logger = LoggerFactory.getLogger(ConnackOutboundInterceptorImpl.class);

    public void onOutboundConnack(ConnackOutboundInput input, ConnackOutboundOutput output) {

        var packet = input.getConnackPacket();
        if (MqttCodeUtils.isSuccessConnack(packet.getReasonCode())) {

            var clientId = input.getClientInformation().getClientId();

            logger.debug("Process outbound connack for clientId {}", clientId);

            var connectTrackingOpt = ExtensionServices.trackingService().endConnectTrackingForClient(clientId);
            if (connectTrackingOpt.isEmpty()) {
                return;
            }
            var connectTracking = connectTrackingOpt.get();

            var topicGroupOpt = ExtensionServices.topicGroupService().findTopicGroupByName(CONNECTED_INFO_TOPIC);
            if (topicGroupOpt.isEmpty()) {
                throw new IllegalStateException("Unexpected implementation error");
            }
            var topicGroup = topicGroupOpt.get();
            var topic = topicGroup.getTopicWithParams(connectTracking.getExplicitParams());

            var publish = Builders.publish()
                    .topic(topic)
                    .qos(PUBLISH_QOS)
                    .payload(ByteUtils.byteBufferFrom(clientId))
                    .build();

            Services.publishService().publish(publish)
                    .whenComplete((result, throwable) -> {
                        if (throwable == null) {
                            logger.debug("Successfully publish {} to clientId {}", topic, clientId);
                        } else {
                            logger.debug("Failed to publish {} to clientId {}", topic, clientId);
                        }
                    });

            var recordable = Recordable.builder()
                    .timestamp(Instant.now().toEpochMilli())
                    .topic(topic)
                    .payload(ByteUtils.toBytes(clientId))
                    .clientId(clientId)
                    .packetId(0)
                    .build();

            ExtensionServices.recordService().markRecordable(recordable);
            ExtensionServices.recordService().recordIfAble(0, clientId);
        }
    }
}
























