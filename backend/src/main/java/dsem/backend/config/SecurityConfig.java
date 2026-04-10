package dsem.backend.config;

import dsem.backend.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> {})
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                .requestMatchers("/api/inventory/receive").hasAnyRole("ADMIN", "STORE_MANAGER")
                .requestMatchers("/api/inventory/**").authenticated()
                .requestMatchers("/api/explosives").authenticated()
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/explosives").hasRole("ADMIN")
                .requestMatchers("/api/requests/pending").hasAnyRole("ADMIN", "STORE_MANAGER")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/requests").hasAnyRole("ADMIN", "OPERATIONS")
                .requestMatchers("/api/requests/**").authenticated()
                .requestMatchers("/api/approvals/**").hasAnyRole("ADMIN", "STORE_MANAGER")
                .requestMatchers("/api/dispatch/**").hasAnyRole("ADMIN", "STORE_MANAGER")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/blast").hasAnyRole("ADMIN", "OPERATIONS")
                .requestMatchers("/api/blast/**").authenticated()
                .requestMatchers("/api/returns/**").authenticated()
                .requestMatchers("/api/reports/**").authenticated()
                .requestMatchers("/api/audit-logs/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
