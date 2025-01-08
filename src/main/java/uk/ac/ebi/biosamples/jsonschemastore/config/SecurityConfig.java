package uk.ac.ebi.biosamples.jsonschemastore.config;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import uk.ac.ebi.biosamples.jsonschemastore.service.UserService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final RepositoryRestProperties repositoryRestProperties;
    private final UserService userService;
    private final Log logger = LogFactory.getLog(getClass());
    @Bean
    protected SecurityFilterChain configure(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        String basePath = repositoryRestProperties.getBasePath();
        http
                .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(HttpMethod.GET).permitAll()
                        // checklists
                        .requestMatchers(HttpMethod.POST, basePath + "/mongoJsonSchemas/**").hasAuthority("editor")
                        .requestMatchers(HttpMethod.PUT, basePath + "/mongoJsonSchemas/**").hasAuthority("editor")
                        .requestMatchers(HttpMethod.DELETE, basePath + "/mongoJsonSchemas/**").hasAuthority("editor")



                        // fields
                        .requestMatchers(HttpMethod.POST, basePath + "/fields/**").hasAuthority("editor")
                        .requestMatchers(HttpMethod.PUT, basePath + "/fields/**").hasAuthority("editor")
                        .requestMatchers(HttpMethod.DELETE, basePath + "/fields/**").hasAuthority("editor")


                        // export chceklists
                        .requestMatchers(HttpMethod.GET, "/exporter/**").permitAll()

                        // checklist registry
                        .requestMatchers(HttpMethod.GET, "/registry/**").permitAll()

                        // admin
                        .requestMatchers(HttpMethod.GET, "/checklist/converter/**").permitAll()
                        .requestMatchers(HttpMethod.GET, basePath + "/users/search/me").authenticated()
                        .requestMatchers(HttpMethod.GET, basePath + "/users/search").authenticated()
                        .requestMatchers(HttpMethod.GET, basePath + "/users/**").hasAuthority("admin")
                        .requestMatchers(HttpMethod.POST, basePath + "/users/**").hasAuthority("admin")
                        .requestMatchers(HttpMethod.PUT, basePath + "/users/**").hasAuthority("admin")

                        // TODO: does read only this need auth?
                        .requestMatchers(HttpMethod.GET, basePath + "/mongoJsonSchemas/**").permitAll()
                        .requestMatchers(HttpMethod.GET, basePath + "/fields/**").permitAll()
                        .requestMatchers(HttpMethod.GET, basePath + "/fieldGroups/**").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/registry/**").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/api/v2/schemas/list").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.GET,  basePath).permitAll()

                        .anyRequest().authenticated()
                )
                .addFilter(requestHeaderAuthenticationFilter(authenticationManager))
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
