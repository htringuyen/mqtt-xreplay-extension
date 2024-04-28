package org.iotwarehouse.extension.core.external;

import org.iotwarehouse.extension.core.external.exception.ExternalServiceException;
import org.iotwarehouse.extension.core.param.ExplicitParams;

import java.util.Optional;

public interface ObserverStore {

    public boolean authenticate(String observerName, String password) throws ExternalServiceException;

    public Optional<ExplicitParams> findParamsForName(String name) throws ExternalServiceException;
}
