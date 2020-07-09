//package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.auth;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.stereotype.Component;
//import uk.ac.ebi.tsc.aap.client.security.AAPWebSecurityAutoConfiguration.AAPWebSecurityConfig;
//import uk.ac.ebi.tsc.aap.client.security.StatelessAuthenticationEntryPoint;
//import uk.ac.ebi.tsc.aap.client.security.StatelessAuthenticationFilter;
//import uk.ac.ebi.tsc.aap.client.security.TokenAuthenticationService;
//
//@Slf4j
//@Component
//public class BioSamplesAAPWebSecurityConfig extends AAPWebSecurityConfig {
//    private final StatelessAuthenticationEntryPoint unauthorizedHandler;
//
//    private TokenAuthenticationService tokenAuthenticationService;
//
//    public BioSamplesAAPWebSecurityConfig(StatelessAuthenticationEntryPoint unauthorizedHandler, TokenAuthenticationService tokenAuthenticationService) {
//        this.unauthorizedHandler = unauthorizedHandler;
//        this.tokenAuthenticationService = tokenAuthenticationService;
//    }
//
//    private StatelessAuthenticationFilter statelessAuthenticationFilterBean() throws Exception {
//        return new StatelessAuthenticationFilter(this.tokenAuthenticationService);
//    }
//
//    @Override
//    protected void configure(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity
//                // we don't need CSRF because our token is invulnerable
//                .csrf().disable()
//                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
//                // don't create session
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//
//        httpSecurity.addFilterBefore(statelessAuthenticationFilterBean(),
//                UsernamePasswordAuthenticationFilter.class);
//
//        //disable the no-cache header injectection, we'll manage this ourselves
//        httpSecurity.headers().cacheControl().disable();
//    }
//
//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService());
//    }
//}
