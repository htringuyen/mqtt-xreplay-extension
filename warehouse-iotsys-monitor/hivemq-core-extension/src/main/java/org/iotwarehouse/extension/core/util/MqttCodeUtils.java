package org.iotwarehouse.extension.core.util;

import com.hivemq.extension.sdk.api.packets.connect.ConnackReasonCode;
import com.hivemq.extension.sdk.api.packets.publish.AckReasonCode;
import com.hivemq.extension.sdk.api.packets.subscribe.SubackReasonCode;
import lombok.NonNull;

public final class MqttCodeUtils {

    public static boolean isSuccessAck(@NonNull AckReasonCode code) {
        return code == AckReasonCode.SUCCESS
                || code == AckReasonCode.NO_MATCHING_SUBSCRIBERS;
    }

    public static boolean isFailAck(@NonNull AckReasonCode code) {
        return ! isSuccessAck(code);
    }

    public static boolean isSuccessSuback(@NonNull SubackReasonCode code) {
        return code == SubackReasonCode.GRANTED_QOS_0
                || code == SubackReasonCode.GRANTED_QOS_1
                || code == SubackReasonCode.GRANTED_QOS_2;
    }

    public static boolean isFailSuback(@NonNull SubackReasonCode code) {
        return ! isSuccessSuback(code);
    }

    public static boolean isSuccessConnack(@NonNull ConnackReasonCode code) {
        return code  == ConnackReasonCode.SUCCESS;
    }

    public static boolean isFailConnack(@NonNull ConnackReasonCode code) {
        return ! isSuccessConnack(code);
    }
}
