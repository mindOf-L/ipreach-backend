package app.ipreach.backend.app.config;

import app.ipreach.backend.shared.validation.Endpoint;
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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //private final BasicAuthenticationEntryPoint authenticationEntryPoint;

    /*
    public SecurityConfig(BasicAuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

     */

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
            //.cors(Customizer.withDefaults())
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

}
