package org.iotwarehouse.extension.external;

import org.iotwarehouse.extension.core.external.ExternalServices;
import org.iotwarehouse.extension.core.external.ObserverStore;
import org.iotwarehouse.extension.core.external.exception.ExternalServiceException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ObserverStoreTest {

    private static final Logger logger = LoggerFactory.getLogger(ObserverStoreTest.class);

    private final ObserverStore obsStore = ExternalServices.getObserverStore();

    @Test
    void testAuthenticateObserver() throws ExternalServiceException {
        final var OBS_NAME = "warehousescopeobserver01";
        final var OBS_PASSWORD = "obs1234";

        assertTrue(obsStore.authenticate(OBS_NAME, OBS_PASSWORD));

        assertFalse(obsStore.authenticate(OBS_NAME, "invalid_password"));
    }

    @Test
    void testFindObserverParams() throws ExternalServiceException {
        final var OBS_NAME = "warehousescopeobserver01";

        var paramOpt = obsStore.findParamsForName(OBS_NAME);

        assertTrue(paramOpt.isPresent());
        logger.info(paramOpt.get().toString());

        assertFalse(obsStore.findParamsForName("invalid_name").isPresent());
    }


}




































