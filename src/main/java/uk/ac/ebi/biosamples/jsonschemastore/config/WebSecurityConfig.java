package uk.ac.ebi.biosamples.jsonschemastore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final SchemaStoreProperties schemaStoreProperties;

    public WebSecurityConfig(SchemaStoreProperties schemaStoreProperties) {
        super();
        this.schemaStoreProperties = schemaStoreProperties;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/v2/schemas")
//                .hasRole("USER")
                .authenticated()
                .and()
                .authorizeRequests()
                .antMatchers("/**").permitAll().antMatchers("/**/*").permitAll()
                .and()
                .cors().and()
                .csrf().disable()
        ;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(schemaStoreProperties.getAdminUsername())
                .password(passwordEncoder().encode(schemaStoreProperties.getAdminPassword()))
                .roles("USER");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
