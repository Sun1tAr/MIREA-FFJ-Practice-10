package my.learn.mireaffjpractice10.config;

import lombok.RequiredArgsConstructor;
import my.learn.mireaffjpractice10.model.UserRole;
import my.learn.mireaffjpractice10.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .cors(cors -> cors.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**", "/h2-console/*", "/h2-console", "/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/user/**").hasAnyAuthority(UserRole.USER.getAuthority(), UserRole.ADMIN.getAuthority())
                        .requestMatchers("/api/v1/admin/**").hasAuthority(UserRole.ADMIN.getAuthority())
                )

                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Если проблемы с аутентификацией, то жестко бросаем 401
//                .exceptionHandling(eh -> eh
//                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
//                )

                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }


}
