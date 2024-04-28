package org.iotwarehouse.extension.core.handler;

import com.hivemq.extension.sdk.api.interceptor.publish.PublishInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundInput;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundOutput;
import org.iotwarehouse.extension.core.replay.Recordable;
import org.iotwarehouse.extension.core.service.ExtensionServices;
import org.iotwarehouse.extension.core.util.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishInboundInterceptorImpl implements PublishInboundInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(PublishInboundInterceptorImpl.class);

    @Override
    public void onInboundPublish(PublishInboundInput input, PublishInboundOutput output) {
        var pubPack = input.getPublishPacket();
        var payloadOpt = pubPack.getPayload();
        if (payloadOpt.isEmpty()) {
            return;
        }


        var recordable = Recordable.builder()
                .topic(pubPack.getTopic())
                .timestamp(pubPack.getTimestamp())
                .payload(ByteUtils.copyOf(payloadOpt.get()))
                .packetId(pubPack.getPacketId())
                .clientId(input.getClientInformation().getClientId())
                .build();

        logger.debug("Mark Recordable for clientId {} - packetId {}: {}",
                input.getClientInformation().getClientId(), pubPack.getPacketId(), recordable);

        ExtensionServices.recordService().markRecordable(recordable);
        //ExtensionServices.recordService().recordIfAble(recordable);

        /*Services.extensionExecutorService()
                .schedule(() -> {
                    var clientId = input.getClientInformation().getClientId();
                    Services.clientService()
                            .isClientConnected(clientId)
                            .whenComplete((isConnected, throwable) -> {
                                if (throwable == null && isConnected) {
                                    var isRecorded = ExtensionServices.recordService().recordIfAble(recordable);
                                    if (isRecorded) {
                                        logger.debug("Record successfully {}", recordable);
                                    } else {
                                        logger.debug("Record failed {}", recordable);
                                    }
                                }
                            });
                }, 5, TimeUnit.MILLISECONDS);*/
    }
}

















