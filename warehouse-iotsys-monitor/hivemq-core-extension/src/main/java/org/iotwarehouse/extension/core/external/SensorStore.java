package org.iotwarehouse.extension.core.external;

import org.iotwarehouse.extension.core.external.exception.ExternalServiceException;
import org.iotwarehouse.extension.core.param.ExplicitParams;

import java.util.Optional;

public interface SensorStore {

    public Optional<ExplicitParams> findParamsForId(String sensorId) throws ExternalServiceException;

}
