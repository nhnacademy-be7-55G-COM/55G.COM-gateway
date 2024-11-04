package com.S5G.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("shop-service",
                p -> p.path("/api/shop/**")
                    .uri("lb://shop-service")
            )
            .route("auth-service",
                p -> p.path("/api/auth/**")
                    .uri("lb://auth-service")
            )
            .build();
    }

}
