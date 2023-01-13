package com.dotcms.master_control.config;

import com.google.common.base.Predicates;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket leaveApi() {
        List<Parameter> headerList = new ArrayList<>();
        ParameterBuilder tokenParameterBuilder = new ParameterBuilder();
        tokenParameterBuilder.name("authorization").description("Authorization Token").modelRef(new ModelRef("string")).parameterType("header").required(true);
        headerList.add(tokenParameterBuilder.build());


        return new Docket(DocumentationType.SWAGGER_2)
                .globalOperationParameters(headerList)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build();
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>
    webServerFactoryCustomizer() {
        return factory -> factory.setContextPath("/dotcms");
    }
}