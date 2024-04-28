package org.iotwarehouse.extension.core.external;

import org.iotwarehouse.extension.core.external.exception.ExternalServiceException;
import org.iotwarehouse.extension.core.messaging.Role;

import java.util.Optional;

public interface RoleStore {

    public Optional<Role> authenticateRole(String authName, String authPassword) throws ExternalServiceException;
}
