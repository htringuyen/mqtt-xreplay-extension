package org.iotwarehouse.extension.core.services;

import org.iotwarehouse.extension.core.replay.Publishable;
import org.iotwarehouse.extension.core.replay.RecordService;
import org.iotwarehouse.extension.core.replay.Recordable;
import org.iotwarehouse.extension.core.service.ExtensionServices;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecordServiceTest extends ServiceBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(RecordServiceTest.class);

    private static RecordService recordService;

    @BeforeAll
    static void setup() {
        ExtensionServices.initialize();
        recordService = ExtensionServices.recordService();
    }

    @Test
    void testSensorConnectedReportRecord() {

        final var warehouseList = new ArrayList<String>();
        final var containerList = new ArrayList<String>();
        final var topicList = new ArrayList<String>();

        final int N_WAREHOUSES = 10;
        final int N_CONTAINERS = 5;

        for (int whCount = 0; whCount < N_WAREHOUSES; whCount++) {
            var whCode = String.format("VN-WH-%03d", whCount);
            warehouseList.add(whCode);
            for (int cnCount = 0; cnCount < N_CONTAINERS; cnCount++) {
                var cnCode = String.format("CONT-%03d", cnCount);
                containerList.add(cnCode);
                topicList.add(whCode + "/" + cnCode + "/" + "sensor_connected/report");
            }
        }

        for (int i = 0; i < 2; i++) {
            for (var topic: topicList) {
                var message = topic + "/status";
                var recordable = Recordable.builder()
                        .topic(topic)
                        .timestamp(Instant.now().toEpochMilli())
                        .payload(message.getBytes())
                        .build();

                recordService.markRecordable(recordable);
                recordService.recordIfAble(recordable);
            }
        }

        assertEquals(1,
                countReplay(
                        getTopic(warehouseList.get(3), containerList.get(2))));

        assertEquals(N_CONTAINERS, countReplay(
                getTopic(warehouseList.get(4), "+")));

        assertEquals(N_WAREHOUSES, countReplay(
                getTopic("+", containerList.get(2))));

        assertEquals(N_WAREHOUSES * N_CONTAINERS, countReplay(getTopic("+", "+")));
    }

    private int countReplay(String topic) {
        var count = new AtomicInteger();
        recordService.replayForConsumer(topic, publishable -> {
            count.getAndIncrement();
            logPublishable(publishable);
        });
        return count.get();
    }

    private void logPublishable(Publishable publishable) {
        logger.info("Publish{ message={}, topic={} }", publishable.getMessage(), publishable.getTopic());
    }

    public String getTopic(String whCode, String cnCode) {
        return whCode + "/" + cnCode + "/" + "sensor_connected/report";
    }
}

































