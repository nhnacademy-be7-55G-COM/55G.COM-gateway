package config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteLocatorConfig {
    @Bean
    public RouteLocator myRoute(RouteLocatorBuilder builder) {

        return builder.routes()
                .route("shop-service",
                        p -> p.path("/api/shop/**")
                                .uri("lb://shop-service")
                )
                .build();
    }
}
