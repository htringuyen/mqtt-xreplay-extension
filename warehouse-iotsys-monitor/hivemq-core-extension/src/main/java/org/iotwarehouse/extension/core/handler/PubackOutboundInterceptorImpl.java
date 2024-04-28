package org.iotwarehouse.extension.core.handler;

import com.hivemq.extension.sdk.api.interceptor.puback.PubackOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.puback.parameter.PubackOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.puback.parameter.PubackOutboundOutput;
import org.iotwarehouse.extension.core.service.ExtensionServices;
import org.iotwarehouse.extension.core.util.MqttCodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PubackOutboundInterceptorImpl implements PubackOutboundInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(PubackOutboundInterceptorImpl.class);

    @Override
    public void onOutboundPuback(PubackOutboundInput input, PubackOutboundOutput output) {

        var reasonCode = input.getPubackPacket().getReasonCode();
        logger.debug("Handle in outbound PubAck with code {}", reasonCode);
        if (MqttCodeUtils.isFailAck(input.getPubackPacket().getReasonCode())) {
            return;
        }

        var packetId = input.getPubackPacket().getPacketIdentifier();
        var clientId = input.getClientInformation().getClientId();
        var recorded = ExtensionServices.recordService().recordIfAble(packetId, clientId);

        if (recorded) {
            logger.debug("Successfully record packetId {} for clientId {}", packetId, clientId);
        } else {
            logger.debug("Failed record packetId {} for clientId {}", packetId, clientId);
        }
    }
}
