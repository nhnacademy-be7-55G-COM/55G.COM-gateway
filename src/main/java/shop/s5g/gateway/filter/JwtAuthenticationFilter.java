package shop.s5g.gateway.filter;

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
import shop.s5g.gateway.config.JwtAuthenticationConfig;

@Component
public class JwtAuthenticationFilter extends
    AbstractGatewayFilterFactory<JwtAuthenticationConfig> {

    private final JwtAuthenticationConfig jwtConfig;

    public JwtAuthenticationFilter(JwtAuthenticationConfig jwtConfig) {
        super(JwtAuthenticationConfig.class);
        this.jwtConfig = jwtConfig;
    }

    @Override
    public GatewayFilter apply(JwtAuthenticationConfig config) {
        return ((exchange, chain) -> {
            String token = extractToken(exchange);
            if (token == null){
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            if (token.equals("ANONYMOUS")){
                return chain.filter(exchange);
            }

            if (!validateToken(token)) {
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
            Jwts.parser().verifyWith(jwtConfig.getSecretKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    private boolean isExpiredToken(String token) {
        return Jwts.parser().verifyWith(jwtConfig.getSecretKey()).build().parseSignedClaims(token)
            .getPayload().getExpiration().before(new Date());
    }

}
