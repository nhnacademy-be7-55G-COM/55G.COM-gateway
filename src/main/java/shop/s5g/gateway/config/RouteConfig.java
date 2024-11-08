package shop.s5g.gateway.config;

import com.S5G.gateway.filter.JwtAuthenticationFilter;
import com.S5G.gateway.filter.JwtAuthenticationFilter.Config;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RouteConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("shop-service",
                p -> p.path("/api/shop/**")
                    .filters(f -> f.filter(jwtAuthenticationFilter.apply(new Config())))
                    .uri("lb://shop-service")
            )
            .route("auth-service",
                p -> p.path("/api/auth/**")
                    .filters(f -> f.filter(jwtAuthenticationFilter.apply(new Config())))
                    .uri("lb://auth-service")
            )
            .build();
    }

}
