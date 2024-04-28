package org.iotwarehouse.extension.core;

import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartInput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartOutput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStopInput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStopOutput;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import org.iotwarehouse.extension.core.handler.*;
import org.iotwarehouse.extension.core.service.ExtensionServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ExtensionMain implements com.hivemq.extension.sdk.api.ExtensionMain {

    private static final Logger logger = LoggerFactory.getLogger(ExtensionMain.class);

    @Override
    public void extensionStart(ExtensionStartInput input, ExtensionStartOutput output) {

        ExtensionServices.initialize();

        final var authenticator = new AuthenticatorImpl();
        Services.securityRegistry().setAuthenticatorProvider(in -> authenticator);

        final var publishInboundInterceptor = new PublishInboundInterceptorImpl();
        final var pubackOutboundInterceptor = new PubackOutboundInterceptorImpl();
        final var pubrecOutboundInterceptor = new PubrecOutboundInterceptorImpl();
        final var subscribeInboundInterceptor = new SubscribeInboundInterceptorImpl();
        final var subackOutboundInterceptor = new SubackOutboundInterceptorImpl();
        final var connackOutboundInterceptor = new ConnackOutboundInterceptorImpl();
        final var clientLifecycleEvenListener = new ClientLifecycleEventListenerImpl();

        Services.initializerRegistry().setClientInitializer((in, ctx) -> {

            ctx.addPublishInboundInterceptor(publishInboundInterceptor);

            ctx.addPubackOutboundInterceptor(pubackOutboundInterceptor);

            ctx.addPubrecOutboundInterceptor(pubrecOutboundInterceptor);

            ctx.addSubscribeInboundInterceptor(subscribeInboundInterceptor);

            ctx.addSubackOutboundInterceptor(subackOutboundInterceptor);

        });

        Services.interceptorRegistry().setConnackOutboundInterceptorProvider((in) -> connackOutboundInterceptor);

        Services.eventRegistry().setClientLifecycleEventListener((in) -> clientLifecycleEvenListener);

        // add publish inbound interceptor and outbound interceptor
        /*Services.initializerRegistry().setClientInitializer((in, ctx) -> {
            ctx.addPublishInboundInterceptor((i, o) -> {
                var clientId = i.getClientInformation().getClientId();
                var pubPack = i.getPublishPacket();
                republishMessage(pubPack);
                logger.debug("Inbound publish from {} at {} with package id {}: {}",
                        clientId, pubPack.getTimestamp(), pubPack.getPacketId(), byteBufferToString(pubPack.getPayload().get()));
            });

            ctx.addPublishOutboundInterceptor((i, o) -> {
                var clientId = i.getClientInformation().getClientId();
                var pubPack = i.getPublishPacket();
                logger.debug("Outbound publish from {} at {} with package id {}: {}",
                        clientId, pubPack.getTimestamp(), pubPack.getPacketId(), byteBufferToString(pubPack.getPayload().get()));
            });
        });*/
    }

    @Override
    public void extensionStop(ExtensionStopInput input, ExtensionStopOutput output) {

    }

    private String byteBufferToString(ByteBuffer byteBuffer) {
        var newBuffer = ByteBuffer.allocate(byteBuffer.capacity());
        newBuffer.put(byteBuffer);
        return new String(newBuffer.array(), StandardCharsets.UTF_8);
    }

    private void republishMessage(PublishPacket packet) {
        var content = byteBufferToString(packet.getPayload().get());
        var publish = Builders.publish()
                .topic(packet.getTopic())
                .payload(ByteBuffer.wrap(("Republished: " + content).getBytes(StandardCharsets.UTF_8)))
                .build();
        Services.publishService().publish(publish);
    }
}
