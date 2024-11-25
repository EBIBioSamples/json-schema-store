package uk.ac.ebi.biosamples.jsonschemastore.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import uk.ac.ebi.biosamples.jsonschemastore.auth.BearerTokenAuthenticationProvider;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    protected final Log logger = LogFactory.getLog(getClass());
    protected final BearerTokenAuthenticationProvider bearerTokenAuthenticationProvider;
    @Bean
    protected SecurityFilterChain configure(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/v2/mongoJsonSchemas").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v2/mongoJsonSchemas").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v2/mongoJsonSchemas").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v2/mongoJsonSchemas").authenticated()
                        .anyRequest().permitAll()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            logger.warn("Unauthorized access attempt: " + authException.getMessage());
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        })
                )
                .addFilter(new BearerTokenAuthenticationFilter(authenticationManager))
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
        ;
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(bearerTokenAuthenticationProvider);
        return auth.getOrBuild();
    }
}
