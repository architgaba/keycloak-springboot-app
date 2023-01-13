package com.dotcms.master_control.config;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.management.HttpSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackageClasses = KeycloakSecurityComponents.class)
class WebSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    @Autowired
    private SimpleCorsFilter simpleCorsFilter;

    private static final String[] AUTH_WHITELIST = {
            // -- Swagger UI v2
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            // -- Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/swagger-ui/**"
            // other public endpoints of your API may be appended to this array
    };


    @Autowired
    public void configureGlobal(
            AuthenticationManagerBuilder auth) throws Exception {

        KeycloakAuthenticationProvider keycloakAuthenticationProvider
                = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(
                new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    @Bean
    public KeycloakSpringBootConfigResolver KeycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(
                new SessionRegistryImpl());
    }

    @Bean
    @Override
    @ConditionalOnMissingBean(HttpSessionManager.class)
    protected HttpSessionManager httpSessionManager() {
        return new HttpSessionManager();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.cors().and().addFilterBefore(simpleCorsFilter, ChannelProcessingFilter.class)
                .sessionManagement()
                .sessionAuthenticationStrategy(sessionAuthenticationStrategy())



                .and().csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.GET, "/v1/master/getAccessModules/{tenantId}").hasRole("admin")
                .antMatchers(HttpMethod.PUT, "/v1/master/addUpdate").hasRole("admin")
                .antMatchers(HttpMethod.PUT, "/v1/master/delete/{tenantId}").hasRole("admin")

                .antMatchers(HttpMethod.GET, "/v1/tenant/list").hasAnyRole("admin")
                .antMatchers(HttpMethod.POST, "/v1/tenant/add").hasAnyRole("admin")
                .antMatchers(HttpMethod.PUT, "/v1/tenant/update/{tenantid}").hasAnyRole("admin")
                .antMatchers(HttpMethod.DELETE, "/v1/tenant/delete/{tenantId}").hasAnyRole("admin")
                .antMatchers(HttpMethod.GET, "/v1/tenant/realm/{realmName}").hasAnyRole("admin")
                .antMatchers(HttpMethod.GET, "/v1/tenant/basicInfo/{tenantId}").hasAnyRole("admin")


                .antMatchers(HttpMethod.POST, "/v1/module/add").hasAnyRole("admin")
                .antMatchers(HttpMethod.PUT, "/v1/module/update").hasAnyRole("admin")
                .antMatchers(HttpMethod.GET, "/v1/module/getAll").hasAnyRole("admin")
                .antMatchers(HttpMethod.GET, "/v1/module/get/{moduleId}").hasAnyRole("admin")
                .antMatchers(HttpMethod.DELETE, "/v1/module/delete/{moduleId}").hasAnyRole("admin")

                .antMatchers(HttpMethod.PUT, "/v1/configuration/billing-template").hasAnyRole("admin")
                .antMatchers(HttpMethod.DELETE, "/v1/configuration/delete/billing-template/{billingTemplateId}").hasAnyRole("admin")


                .antMatchers(HttpMethod.GET, "/v1/configuration/{tenantId}").hasAnyRole("admin")
                .antMatchers(HttpMethod.GET, "/v1/configuration/price-types").hasAnyRole("admin")
                .antMatchers(HttpMethod.POST, "/v1/configuration/email-template").hasAnyRole("admin")
                .antMatchers(HttpMethod.PUT, "/v1/configuration/email-template").hasAnyRole("admin")
                .antMatchers(HttpMethod.DELETE, "/v1/configuration/delete/email-template/{emailTemplateId}").hasAnyRole("admin")
                .antMatchers(HttpMethod.GET, "/v1/configuration/get-all/email-templates/{tenantId}").hasAnyRole("admin")
                .antMatchers(HttpMethod.POST, "/v1/configuration/approval-template").hasAnyRole("admin")
                .antMatchers(HttpMethod.PUT, "/v1/configuration/approval-template").hasAnyRole("admin")
                .antMatchers(HttpMethod.DELETE, "/v1/configuration/delete/{approvalTemplateId}").hasAnyRole("admin")
                .antMatchers(HttpMethod.GET, "/v1/configuration/get-all/approval-templates/{tenantId}").hasAnyRole("admin")
                .antMatchers(HttpMethod.POST, "/v1/configuration/generate-invoice").hasAnyRole("admin")
                .antMatchers(HttpMethod.GET, "/v1/configuration/download/invoice/{invoiceId}").hasAnyRole("admin")
                .antMatchers(HttpMethod.PUT, "/v1/configuration/update-general-settings").hasAnyRole("admin")
                .antMatchers(HttpMethod.GET, "/v1/configuration/general-settings/{tenantId}").hasAnyRole("admin")
                .antMatchers(HttpMethod.POST, "/v1/configuration/add/invoice").hasAnyRole("admin")
                .antMatchers(HttpMethod.PUT, "/v1/configuration/upload/invoice").hasAnyRole("admin")
                .antMatchers(HttpMethod.GET, "/v1/configuration/get-all/invoices/{tenantId}").hasAnyRole("admin")
                .antMatchers(HttpMethod.PUT, "/v1/access/reset/password").hasAnyRole("admin")
                .antMatchers(HttpMethod.PUT, "/v1/access/reset/password").hasAnyRole("admin")
                .antMatchers(HttpMethod.PUT, "/v1/access/mark/inactive").hasAnyRole("admin")

                .antMatchers(HttpMethod.PUT, "/v1/configuration/invoice/startInvoiceApproval/{invoiceId}").hasAnyRole("admin")
                .antMatchers(HttpMethod.GET, "/v1/configuration/invoice/getAll/pendingInvoices").hasAnyRole("admin","user")
                .antMatchers(HttpMethod.PUT, "/v1/configuration/invoice/invoiceApprovalAction/{invoiceId}").hasAnyRole("admin","user")
                .antMatchers(HttpMethod.PUT, "/v1/configuration/invoice/invoiceBulkApprovalAction").hasAnyRole("admin","user")
                .antMatchers(HttpMethod.GET, "/v1/configuration/invoice/getAllInvoiceAdmin").hasAnyRole("admin")
                .antMatchers(HttpMethod.PUT, "/v1/configuration/invoice/dispatchInvoice/{invoiceId}").hasAnyRole("admin")
                .antMatchers(HttpMethod.POST, "/v1/configuration/invoice/dispatchInvoice/resend/{invoiceId}").hasAnyRole("admin")
                .antMatchers(HttpMethod.PUT, "/v1/configuration/invoice/markPaid/{invoiceId}").hasAnyRole("admin")


                .antMatchers(HttpMethod.GET, "/v1/access/").hasAnyRole("admin")
                .antMatchers(HttpMethod.GET, "/v1/access/download/bulk-upload/format").hasAnyRole("admin")
                .antMatchers(HttpMethod.POST, "/v1/access/").hasAnyRole("admin")
                .antMatchers(HttpMethod.PUT, "/v1/access/").hasAnyRole("admin")
                .antMatchers(HttpMethod.DELETE, "/v1/access/delete/{userAccessId}").hasAnyRole("admin")
                .antMatchers(HttpMethod.POST, "/v1/access/upload/bulk/users").hasAnyRole("admin")
                .antMatchers(HttpMethod.POST, "/v1/access/invite/user").hasAnyRole("admin")

                .antMatchers(AUTH_WHITELIST).permitAll()
                .antMatchers("/**").denyAll()
                .anyRequest().permitAll();
    }
}