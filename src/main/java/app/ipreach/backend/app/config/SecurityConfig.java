package app.ipreach.backend.app.config;

import app.ipreach.backend.shared.validation.Endpoint;
import app.ipreach.backend.shared.validation.Security;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${CORS_ORIGIN:*}")
    private String corsOrigins;

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.builder()
            .username("myuser")
            .password(passwordEncoder().encode("mypassword"))
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            //.headers(headersConfig -> headersConfig.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(Endpoint.getMatchEndpoints()).permitAll()
                .requestMatchers(Endpoint.getMatchErrors()).permitAll()
                .requestMatchers(Endpoint.getMatchSwagger()).permitAll()
                .requestMatchers(Endpoint.getMatchTest()).permitAll()
                .anyRequest().authenticated())
            //.securityMatcher("/**")
            .httpBasic(Customizer.withDefaults())
            .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(login -> login
                .loginPage("/api/v1/auth/login").permitAll())
                //.loginProcessingUrl("/api/v1/auth/login").permitAll())
            .logout(logout -> logout
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true)
                .logoutUrl("/api/v1/auth/logout")
                .logoutSuccessUrl("/api/v1/auth/login")
                .permitAll());
            //.exceptionHandling(config -> config.authenticationEntryPoint(authenticationEntryPoint));

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(corsOrigins.split(",")));

        log.info("Allowed CORS: {}", Arrays.toString(corsOrigins.split(",")));

        config.setAllowedHeaders(Security.getAllowedHeaders());
        config.setAllowedMethods(Security.getAllowedMethods());
        config.setExposedHeaders(Security.getExposedHeaders());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
