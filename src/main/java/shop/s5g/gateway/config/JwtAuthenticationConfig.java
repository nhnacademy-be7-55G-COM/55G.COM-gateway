package shop.s5g.gateway.config;

import io.jsonwebtoken.Jwts.SIG;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtAuthenticationConfig {

    private final String secretKey;

    public JwtAuthenticationConfig(@Value("${spring.jwt.secret}") String secretKey) {
        this.secretKey = secretKey;
    }

    public SecretKey getSecretKey() {
        return new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8),
            SIG.HS256.key().build().getAlgorithm());
    }
}
