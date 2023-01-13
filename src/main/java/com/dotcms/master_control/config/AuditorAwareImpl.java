package com.dotcms.master_control.config;

import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Autowired
    private AccessToken accessToken;

    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            String userId = accessToken.getName();
            return Optional.of(userId != null ? userId : "");
        } catch (BeanCreationException e) {
            return Optional.of("super-admin");
        }


    }

}