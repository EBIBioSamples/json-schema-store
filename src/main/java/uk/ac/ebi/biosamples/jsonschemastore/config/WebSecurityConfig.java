//package uk.ac.ebi.biosamples.jsonschemastore.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class WebSecurityConfig {
//  private final SchemaStoreProperties schemaStoreProperties;
//
//  public WebSecurityConfig(SchemaStoreProperties schemaStoreProperties) {
//    this.schemaStoreProperties = schemaStoreProperties;
//  }
//
//  @Bean
//  protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//    http.httpBasic(Customizer.withDefaults());
//    http.authorizeHttpRequests(authorize -> authorize
//        .requestMatchers(HttpMethod.POST, "/api/v2/schemas").authenticated()
//        .anyRequest().permitAll());
//    http.cors(httpSecurityCorsConfigurer -> {
//    });
//
//    return http.build();
//  }
//
////  @Override
////  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
////    auth.inMemoryAuthentication()
////        .withUser(schemaStoreProperties.getAdminUsername())
////        .password(passwordEncoder().encode(schemaStoreProperties.getAdminPassword()))
////        .roles("USER");
////  }
//
//  @Bean
//  public PasswordEncoder passwordEncoder() {
//    return new BCryptPasswordEncoder();
//  }
//
//}
