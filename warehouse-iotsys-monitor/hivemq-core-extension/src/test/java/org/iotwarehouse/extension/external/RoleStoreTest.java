package org.iotwarehouse.extension.external;

import org.iotwarehouse.extension.core.external.ExternalServices;
import org.iotwarehouse.extension.core.external.exception.ExternalServiceException;
import org.iotwarehouse.extension.core.messaging.RoleType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

public class RoleStoreTest {

    private static final Logger logger = LoggerFactory.getLogger(RoleStoreTest.class);

    @Test
    void testAuthenticateRole() {
        final var AUTH_NAME = "toplevelman";
        final var AUTH_PASSWORD = "abcd1234";
        var roleStore = ExternalServices.getRoleStore();

        var roleOpt = assertDoesNotThrow(() -> roleStore.authenticateRole(AUTH_NAME, AUTH_PASSWORD));
        assertTrue(roleOpt.isPresent());
        var role = roleOpt.get();
        logger.info("Authenticated role: {}", role);
        assertEquals("TOP_LEVEL_MANAGER", role.getName());
        assertEquals(RoleType.OBSERVER, role.getType());

        var invalidRoleOpt = assertDoesNotThrow(
                () -> roleStore.authenticateRole(AUTH_NAME, "invalid_password"));
        assertFalse(invalidRoleOpt.isPresent());
    }
}
