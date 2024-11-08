package com.S5G.gateway.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class JwtAuthenticationFilter extends
    AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            String token = extractToken(exchange);

            // 검증할 필요가 없는 public 엔드포인트라면 그대로 진행
            //TODO 현재 모든 경로 public으로 설정, 추후에 종합후에 설정 예정

            if (isPublicEndpoint(exchange.getRequest().getURI().getPath())) {
                return chain.filter(exchange);
            }

            if (token == null || !validateToken(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            if (isExpiredToken(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            return chain.filter(exchange);
        });
    }

    private String extractToken(ServerWebExchange exchange) {
        String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    private boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(newConfig().getSecretKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    private boolean isExpiredToken(String token) {
        return Jwts.parser().verifyWith(newConfig().getSecretKey()).build().parseSignedClaims(token)
            .getPayload().getExpiration().before(new Date());
    }
    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/api/shop/") || path.startsWith("/api/auth/");
    }

    public static class Config {
        @Value("${spring.jwt.secret}")
        private String secretKey;

        public SecretKey getSecretKey() {
            return new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8),
                SIG.HS256.key().build().getAlgorithm());
        }
    }

}
