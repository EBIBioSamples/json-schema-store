package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import uk.ac.ebi.tsc.aap.client.security.AAPWebSecurityAutoConfiguration.AAPWebSecurityConfig;
import uk.ac.ebi.tsc.aap.client.security.StatelessAuthenticationEntryPoint;
import uk.ac.ebi.tsc.aap.client.security.StatelessAuthenticationFilter;
import uk.ac.ebi.tsc.aap.client.security.TokenAuthenticationService;

@Slf4j
@Component
@Order(99)
public class BioSamplesAAPWebSecurityConfig extends AAPWebSecurityConfig {

  // private static final String ROLE_SELF_JSON_SCHEMA_STORE = "ROLE_self.json-schema-store";
  private final StatelessAuthenticationEntryPoint unauthorizedHandler;

  private final TokenAuthenticationService tokenAuthenticationService;

  @Value("${aap.schemaAuthority}")
  private String schemaAuthority;

  public BioSamplesAAPWebSecurityConfig(
      StatelessAuthenticationEntryPoint unauthorizedHandler,
      TokenAuthenticationService tokenAuthenticationService) {
    this.unauthorizedHandler = unauthorizedHandler;
    this.tokenAuthenticationService = tokenAuthenticationService;
  }

  private StatelessAuthenticationFilter statelessAuthenticationFilterBean() throws Exception {
    return new StatelessAuthenticationFilter(this.tokenAuthenticationService);
  }

  @Override
  protected void configure(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        // we don't need CSRF because our token is invulnerable
        .csrf()
        .disable()
        .exceptionHandling()
        .authenticationEntryPoint(unauthorizedHandler)
        .and()
        .authorizeRequests()
        .antMatchers("/api/v1/schemas", "/api/v1/schemas/**")
        // adding Authority to request for schema
        .hasAuthority(schemaAuthority)
        .and()
        // don't create session
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    httpSecurity.addFilterBefore(
        statelessAuthenticationFilterBean(), UsernamePasswordAuthenticationFilter.class);

    // disable the no-cache header injectection, we'll manage this ourselves
    httpSecurity.headers().cacheControl().disable();
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService());
  }
}
