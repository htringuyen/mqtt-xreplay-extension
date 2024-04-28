package org.iotwarehouse.extension.external;

import org.iotwarehouse.extension.core.external.ExternalServices;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

public class SensorStoreTest {

    private static final Logger logger = LoggerFactory.getLogger(SensorStoreTest.class);

    @Test
    void testFindParamsForId() {
        final var VALID_SENSOR_ID = "VN-BD-001|cont-01|status|01";
        final var INVALID_SENSOR_ID = "invalid_id";
        var sensorStore = ExternalServices.getSensorStore();

        var validParamsOpt = assertDoesNotThrow(() -> sensorStore.findParamsForId(VALID_SENSOR_ID));
        assertTrue(validParamsOpt.isPresent());
        logger.info(validParamsOpt.get().toString());

        var invalidParamOpt = assertDoesNotThrow(() -> sensorStore.findParamsForId(INVALID_SENSOR_ID));
        assertFalse(invalidParamOpt.isPresent());
    }
}
