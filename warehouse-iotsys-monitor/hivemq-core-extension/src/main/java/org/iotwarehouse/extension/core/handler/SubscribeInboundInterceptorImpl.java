package org.iotwarehouse.extension.core.handler;

import com.hivemq.extension.sdk.api.interceptor.subscribe.SubscribeInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.subscribe.parameter.SubscribeInboundInput;
import com.hivemq.extension.sdk.api.interceptor.subscribe.parameter.SubscribeInboundOutput;
import com.hivemq.extension.sdk.api.packets.subscribe.Subscription;
import com.hivemq.extension.sdk.api.services.Services;
import org.iotwarehouse.extension.core.replay.Replayable;
import org.iotwarehouse.extension.core.service.ExtensionServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SubscribeInboundInterceptorImpl implements SubscribeInboundInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(SubscribeInboundInterceptorImpl.class);

    @Override
    public void onInboundSubscribe(SubscribeInboundInput input, SubscribeInboundOutput output) {

        var clientId = input.getClientInformation().getClientId();

        var subPack = input.getSubscribePacket();
        var topics = subPack.getSubscriptions()
                .stream()
                .map(Subscription::getTopicFilter)
                .collect(Collectors.toList());

        var replayable = Replayable.builder()
                .subscribePacketId(subPack.getPacketId())
                .clientId(clientId)
                .topics(topics)
                .build();

        ExtensionServices.recordService().markReplayable(replayable);
    }
}
