package org.iotwarehouse.extension.core.handler;

import com.hivemq.extension.sdk.api.events.client.ClientLifecycleEventListener;
import com.hivemq.extension.sdk.api.events.client.parameters.AuthenticationSuccessfulInput;
import com.hivemq.extension.sdk.api.events.client.parameters.ConnectionStartInput;
import com.hivemq.extension.sdk.api.events.client.parameters.DisconnectEventInput;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import org.iotwarehouse.extension.core.external.ExternalServices;
import org.iotwarehouse.extension.core.external.exception.ExternalServiceException;
import org.iotwarehouse.extension.core.replay.Recordable;
import org.iotwarehouse.extension.core.service.ExtensionServices;
import org.iotwarehouse.extension.core.util.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class ClientLifecycleEventListenerImpl implements ClientLifecycleEventListener {

    private static final String DISCONNECTED_INFO_TOPIC = "SENSOR_DISCONNECTED_REPORT";

    private static final Qos PUBLISH_QOS = Qos.AT_LEAST_ONCE;
    private static final Logger logger = LoggerFactory.getLogger(ClientLifecycleEventListenerImpl.class);

    @Override
    public void onMqttConnectionStart(ConnectionStartInput input) {

    }

    @Override
    public void onAuthenticationSuccessful(AuthenticationSuccessfulInput input) {

    }

    @Override
    public void onDisconnect(DisconnectEventInput input) {
        try {
            var clientId = input.getClientInformation().getClientId();
            var paramsOpt = ExternalServices.getSensorStore().findParamsForId(clientId);

            if (paramsOpt.isEmpty()) {
                return;
            }

            var params = paramsOpt.get();
            var topicGroup = ExtensionServices.topicGroupService().findTopicGroupByName(DISCONNECTED_INFO_TOPIC);
            if (topicGroup.isEmpty()) {
                return;
            }

            var topic = topicGroup.get().getTopicWithParams(params);
            var publish = Builders.publish()
                    .topic(topic)
                    .payload(ByteUtils.byteBufferFrom(clientId))
                    .qos(PUBLISH_QOS)
                    .build();
            Services.publishService().publish(publish);

            var recordable = Recordable.builder()
                    .timestamp(Instant.now().toEpochMilli())
                    .topic(topic)
                    .payload(ByteUtils.toBytes(clientId))
                    .clientId(clientId)
                    .packetId(0)
                    .build();

            ExtensionServices.recordService().markRecordable(recordable);
            ExtensionServices.recordService().recordIfAble(0, clientId);

        } catch (ExternalServiceException e) {
            logger.debug("External service error", e);
        }

    }
}
