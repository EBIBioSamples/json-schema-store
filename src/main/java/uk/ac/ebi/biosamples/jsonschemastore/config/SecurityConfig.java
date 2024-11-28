package uk.ac.ebi.biosamples.jsonschemastore.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import uk.ac.ebi.biosamples.jsonschemastore.service.UserService;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    protected final Log logger = LogFactory.getLog(getClass());
    private final UserService userService;
    @Bean
    protected SecurityFilterChain configure(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        RequestHeaderAuthenticationFilter requestHeaderAuthenticationFilter = requestHeaderAuthenticationFilter(authenticationManager);
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/v2/mongoJsonSchemas/**").hasAuthority("editor")
                        .requestMatchers(HttpMethod.PUT, "/api/v2/mongoJsonSchemas/**").hasAuthority("editor")
                        .requestMatchers(HttpMethod.DELETE, "/api/v2/mongoJsonSchemas/**").hasAuthority("editor")
                        .requestMatchers(HttpMethod.GET, "/api/v2/mongoJsonSchemas/**").hasAuthority("reader")
                        .requestMatchers(HttpMethod.POST, "/api/v2/fields/**").hasAuthority("editor")
                        .requestMatchers(HttpMethod.PUT, "/api/v2/fields/**").hasAuthority("editor")
                        .requestMatchers(HttpMethod.DELETE, "/api/v2/fields/**").hasAuthority("editor")
                        .requestMatchers(HttpMethod.GET, "/api/v2/users/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v2/mongoJsonSchemas/**").hasAuthority("reader")
                        .requestMatchers(HttpMethod.GET, "/exporter/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/registry/**").permitAll()
                        .anyRequest().permitAll()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authenticationException) -> {
                            logger.warn("Unauthorized access attempt: " + authenticationException.getMessage());
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        })
                )
                .addFilter(requestHeaderAuthenticationFilter)
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
        ;
        return http.build();
    }

    private RequestHeaderAuthenticationFilter requestHeaderAuthenticationFilter(AuthenticationManager authenticationManager) {
        RequestHeaderAuthenticationFilter filter = new RequestHeaderAuthenticationFilter();
        filter.setPrincipalRequestHeader("Authorization");
        filter.setAuthenticationManager(authenticationManager);
        filter.setCheckForPrincipalChanges(true);
        filter.setExceptionIfHeaderMissing(false);
        filter.setAuthenticationSuccessHandler(
                (request, response, authentication) -> userService.updateUser((UserDetails) authentication.getPrincipal()));
        filter.setAuthenticationFailureHandler(
                (request, response, authenticationException) -> {
                    logger.warn("Unauthorized access attempt: " + authenticationException.getMessage());
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: "+authenticationException.getMessage());
                });

        return filter;
    }



    @Bean
    public AuthenticationManager authenticationManager(AuthenticationManagerBuilder auth,
                                                       AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> uds) {
        PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider = new PreAuthenticatedAuthenticationProvider();
        preAuthenticatedAuthenticationProvider.setPreAuthenticatedUserDetailsService(uds);
        auth.authenticationProvider(preAuthenticatedAuthenticationProvider);
        return auth.getOrBuild();
    }
}
