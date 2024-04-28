package org.iotwarehouse.extension.core.handler;

import com.hivemq.extension.sdk.api.interceptor.publish.PublishOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishOutboundOutput;
import org.iotwarehouse.extension.core.replay.Recordable;
import org.iotwarehouse.extension.core.service.ExtensionServices;
import org.iotwarehouse.extension.core.util.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishOutboundInterceptorImpl implements PublishOutboundInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(PublishOutboundInterceptorImpl.class);

    @Override
    public void onOutboundPublish(PublishOutboundInput input, PublishOutboundOutput output) {
        var pubPack = input.getPublishPacket();
        var payloadOpt = pubPack.getPayload();
        if (payloadOpt.isEmpty()) {
            return;
        }
        var recordable = Recordable.builder()
                .topic(pubPack.getTopic())
                .timestamp(pubPack.getTimestamp())
                .payload(ByteUtils.copyOf(payloadOpt.get()))
                .build();

        logger.debug("Record If Able: {}", recordable);
        //ExtensionServices.recordService().recordIfAble(recordable);
    }
}
