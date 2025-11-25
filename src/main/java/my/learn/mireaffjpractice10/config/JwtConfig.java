package my.learn.mireaffjpractice10.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.time.Duration;

@Configuration

public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Getter
    @Value("${jwt.lifetime.access}")
    private Duration accessLifetime;

    @Getter
    @Value("${jwt.lifetime.refresh}")
    private Duration refreshLifetime;

    @Bean
    public SecretKey secretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @PostConstruct
    public void init() {
        System.out.println("ruruur");
    }

}
