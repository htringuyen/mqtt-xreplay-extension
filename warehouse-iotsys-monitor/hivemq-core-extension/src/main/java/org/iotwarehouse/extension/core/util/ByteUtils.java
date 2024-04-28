package org.iotwarehouse.extension.core.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class ByteUtils {

    public static byte[] copyOf(ByteBuffer inBuffer) {
        var tempBuffer = ByteBuffer.allocate(inBuffer.capacity());
        tempBuffer.put(inBuffer);
        return tempBuffer.array();
    }

    public static byte[] copyOf(Optional<ByteBuffer> inBufferOpt) {
        return inBufferOpt.map(ByteUtils::copyOf).orElseGet(() -> new byte[0]);
    }

    public static ByteBuffer byteBufferFrom(String value) {
        return ByteBuffer.wrap(value.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] toBytes(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }
}
