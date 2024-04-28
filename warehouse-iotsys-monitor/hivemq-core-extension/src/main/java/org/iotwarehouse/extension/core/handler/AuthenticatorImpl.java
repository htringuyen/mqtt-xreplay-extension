package org.iotwarehouse.extension.core.handler;

import com.hivemq.extension.sdk.api.auth.SimpleAuthenticator;
import com.hivemq.extension.sdk.api.auth.parameter.SimpleAuthInput;
import com.hivemq.extension.sdk.api.auth.parameter.SimpleAuthOutput;
import com.hivemq.extension.sdk.api.auth.parameter.TopicPermission;
import com.hivemq.extension.sdk.api.packets.auth.DefaultAuthorizationBehaviour;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import org.iotwarehouse.extension.core.external.ExternalServices;
import org.iotwarehouse.extension.core.external.exception.ExternalServiceException;
import org.iotwarehouse.extension.core.messaging.Role;
import org.iotwarehouse.extension.core.messaging.RoleType;
import org.iotwarehouse.extension.core.param.ExplicitParams;
import org.iotwarehouse.extension.core.service.ExtensionServices;
import org.iotwarehouse.extension.core.tracking.ConnectTracking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.cert.Extension;
import java.util.List;
import java.util.Optional;

public class AuthenticatorImpl implements SimpleAuthenticator {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticatorImpl.class);

    @Override
    public void onConnect(SimpleAuthInput input, SimpleAuthOutput output) {
        var clientId = input.getClientInformation().getClientId();
        var conPack = input.getConnectPacket();

        // authenticate role
        var roleOpt = authenticateRole(conPack);
        if (roleOpt.isEmpty()) {
            output.failAuthentication();
            return;
        }

        // do next authentication base on the role
        var role = roleOpt.get();

        ExplicitParams params;

        if (role.getType() == RoleType.SENSOR) {
            var paramsOpt = findSensorParams(clientId);
            if (paramsOpt.isEmpty()) {
                output.failAuthentication();
                return;
            }
            params = paramsOpt.get();

            ExtensionServices.trackingService()
                    .start(ConnectTracking.builder()
                            .clientId(clientId)
                            .explicitParams(params)
                            .build());
        } else if (role.getType() == RoleType.OBSERVER) {
            var paramsOpt = findObserverParams(conPack);
            if (paramsOpt.isEmpty()) {
                output.failAuthentication();
                return;
            }
            params = paramsOpt.get();
        } else {
            output.failAuthentication();
            throw new IllegalStateException("Unexpected implementation error");
        }

        // set topic permission
        logger.debug("Start get all permissions for role");
        var allowedPermissions = ExtensionServices.topicGroupService().getTopicPermissionsFor(role, params);
        logger.debug("Successfully get all permissions for role");
        logTopicPermissions(allowedPermissions);

        var defaultPermission = output.getDefaultPermissions();

        defaultPermission.clear();
        defaultPermission.addAll(allowedPermissions);
        defaultPermission.setDefaultBehaviour(DefaultAuthorizationBehaviour.DENY);

        output.getOutboundUserProperties().addUserProperty("role", role.getName());
        output.authenticateSuccessfully();
    }

    private Optional<Role> authenticateRole(ConnectPacket conPack) {
        var usernameOpt = conPack.getUserName();
        var passwordOpt = conPack.getPassword();
        if (usernameOpt.isEmpty() || passwordOpt.isEmpty()) {
            return Optional.empty();
        }

        var username = usernameOpt.get();
        var password = StandardCharsets.UTF_8.decode(passwordOpt.get()).toString();

        try {
            logger.debug("Start authenticate role with auth name={}, password={}", username, password);
            return ExternalServices.getRoleStore().authenticateRole(username, password);
        } catch (ExternalServiceException e) {
            logger.debug("Error when use external service", e);
        }
        return Optional.empty();
    }

    private Optional<ExplicitParams> findSensorParams(String id) {
        try {
            logger.debug("On authenticator find param for sensor id {}", id);
            return ExternalServices.getSensorStore().findParamsForId(id);
        } catch (ExternalServiceException e) {
            logger.debug("Error when use external service", e);
        }
        return Optional.empty();
    }

    private Optional<ExplicitParams> findObserverParams(ConnectPacket conPack) {
        final var OBSERVER_NAME_KEY = "OBSERVER_NAME";
        final var OBSERVER_PASSWORD_KEY = "OBSERVER_PASSWORD";

        var observerNameOpt = conPack.getUserProperties().getFirst(OBSERVER_NAME_KEY);
        var observerPasswordOpt = conPack.getUserProperties().getFirst(OBSERVER_PASSWORD_KEY);

        if (observerNameOpt.isEmpty() || observerPasswordOpt.isEmpty()) {
            return Optional.empty();
        }

        var observerName = observerNameOpt.get();
        var observerPassword = observerPasswordOpt.get();

        final var OBS_STORE = ExternalServices.getObserverStore();

        try {
            logger.debug("On authenticator authenticate observer name={}, password={}", observerName, observerPassword);
            if (!OBS_STORE.authenticate(observerName, observerPassword)) {
                return Optional.empty();
            }
            logger.debug("On authenticator find params for observer name {}", observerName);
            return OBS_STORE.findParamsForName(observerNameOpt.get());
        } catch (ExternalServiceException e) {
            logger.debug("Error when use external service", e);
        }
        return Optional.empty();
    }

    private void logTopicPermissions(List<TopicPermission> permissions) {
        logger.debug("Allowed {} topics", permissions.size());
        permissions.forEach(p ->
                logger.debug("AllowedTopic{ activity={}, filter={} }", p.getActivity(), p.getTopicFilter()));
    }
}
