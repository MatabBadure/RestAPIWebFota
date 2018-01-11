package com.hillrom.vest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.common.base.Predicate;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()                 
                //.apis(RequestHandlerSelectors.basePackage("com.hillrom.vest.rest"))
                //.paths(PathSelectors.regex("/api.*"))
                .paths(paths())
                .build()
                .pathMapping("/");
             
    }
    
    private Predicate<String> paths() {
        return PathSelectors.any();
    }
    
    /**
     * A method that returns the API Info
     * @return ApiInfo The Information including description
     */
    public ApiInfo getApiInfo() {
        return  new ApiInfo(
                "My REST API",
                "This REST API allows the user to manage domain objects",
                "1e",
                "",
                "support@mysite.com",
                null,
                null
        );
    }
}
