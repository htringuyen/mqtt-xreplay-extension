package org.iotwarehouse.extension.core.handler;

import com.hivemq.extension.sdk.api.interceptor.suback.SubackOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.suback.parameter.SubackOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.suback.parameter.SubackOutboundOutput;
import lombok.NonNull;
import org.iotwarehouse.extension.core.service.ExtensionServices;
import org.iotwarehouse.extension.core.util.MqttCodeUtils;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SubackOutboundInterceptorImpl implements SubackOutboundInterceptor {

    @Override
    public void onOutboundSuback(SubackOutboundInput input, SubackOutboundOutput output) {

        var packet = input.getSubackPacket();
        var clientId = input.getClientInformation().getClientId();

        var reasonCodes = packet.getReasonCodes();
        var validTopicIndexes = IntStream.range(0, reasonCodes.size())
                .filter(i -> MqttCodeUtils.isSuccessSuback(reasonCodes.get(i)))
                .boxed()
                .collect(Collectors.toList());

        ExtensionServices.recordService().replayIfAble(
                packet.getPacketIdentifier(), clientId, validTopicIndexes);
    }
}
