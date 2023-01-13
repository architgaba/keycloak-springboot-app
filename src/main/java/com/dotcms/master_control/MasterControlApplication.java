package com.dotcms.master_control;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.http.HttpServletRequest;

@EnableSwagger2
@SpringBootApplication
@EnableScheduling
public class MasterControlApplication {

	private static final Logger log = LogManager.getLogger(MasterControlApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(MasterControlApplication.class, args);
		log.info(" ------------------------------------------------------");
		log.info("|               STARTED DOTCMS MASTER SERVER           |");
		log.info(" ------------------------------------------------------");
	}


	@Bean
	@Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public AccessToken getAccessToken() {
		KeycloakAuthenticationToken kcAuthToken = (KeycloakAuthenticationToken) getRequest().getUserPrincipal();
		KeycloakPrincipal keycloakPrincipal = (KeycloakPrincipal) kcAuthToken.getPrincipal();
		KeycloakSecurityContext kcSecCtx = keycloakPrincipal.getKeycloakSecurityContext();
		return kcSecCtx.getToken();
	}

	@Bean
	@Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public KeycloakSecurityContext getKeycloakSecurityContext() {
		KeycloakAuthenticationToken kcAuthToken = (KeycloakAuthenticationToken) getRequest().getUserPrincipal();
		KeycloakPrincipal keycloakPrincipal = (KeycloakPrincipal) kcAuthToken.getPrincipal();
		KeycloakSecurityContext kcSecCtx = keycloakPrincipal.getKeycloakSecurityContext();
		return kcSecCtx;
	}

	private HttpServletRequest getRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
	}
}
