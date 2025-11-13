package co.edu.uniquindio.stayNow.config;

import co.edu.uniquindio.stayNow.security.JWTFilter;
import co.edu.uniquindio.stayNow.security.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> req
                        // ==== RUTAS PÚBLICAS ====
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/ws-chat/**", "/chat-websocket/**",
                                "/chat.html", "/chatMultiusuario.html",
                                "/app/**", "/topic/**", "/queue/**").permitAll()

                        // ==== ALOJAMIENTOS ====
                        .requestMatchers(HttpMethod.GET, "/api/accommodations/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/accommodations/**").hasAuthority("ROLE_HOST")
                        .requestMatchers(HttpMethod.PUT, "/api/accommodations/**").hasAuthority("ROLE_HOST")
                        .requestMatchers(HttpMethod.DELETE, "/api/accommodations/**").hasAnyAuthority("ROLE_HOST", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/accommodations/*/reservations").hasAuthority("ROLE_HOST")
                        .requestMatchers(HttpMethod.POST, "/api/accommodations/*/review").hasAuthority("ROLE_GUEST")

                        // ==== IMÁGENES ====
                        .requestMatchers("/api/images/**").authenticated()
                        // metricas
                        .requestMatchers(HttpMethod.GET, "accommodation/**").hasAuthority("ROLE_HOST")
                        // ==== RESTO ====
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new JwtAuthenticationEntryPoint()))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of("http://localhost:60723"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*")); // ✅ permite todos los headers
        config.setExposedHeaders(List.of("Authorization", "Content-Type", "Set-Cookie")); // ✅ deja ver cookies
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }
}