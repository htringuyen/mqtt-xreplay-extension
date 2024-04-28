package org.iotwarehouse.extension.core.handler;

import com.hivemq.extension.sdk.api.interceptor.pubrec.PubrecOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.pubrec.parameter.PubrecOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.pubrec.parameter.PubrecOutboundOutput;
import org.iotwarehouse.extension.core.service.ExtensionServices;
import org.iotwarehouse.extension.core.util.MqttCodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PubrecOutboundInterceptorImpl implements PubrecOutboundInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(PubrecOutboundInterceptorImpl.class);

    @Override
    public void onOutboundPubrec(PubrecOutboundInput input, PubrecOutboundOutput output) {

        logger.debug("Handle in outbound PubRec");
        if (MqttCodeUtils.isFailAck(input.getPubrecPacket().getReasonCode())) {
            return;
        }

        var packetId = input.getPubrecPacket().getPacketIdentifier();
        var clientId = input.getClientInformation().getClientId();
        var recorded = ExtensionServices.recordService().recordIfAble(packetId, clientId);

        if (recorded) {
            logger.debug("Successfully record packetId {} for clientId {}", packetId, clientId);
        } else {
            logger.debug("Failed record packetId {} for clientId {}", packetId, clientId);
        }
    }
}
