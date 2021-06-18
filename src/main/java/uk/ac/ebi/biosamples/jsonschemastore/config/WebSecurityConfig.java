package uk.ac.ebi.biosamples.jsonschemastore.config;

//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//
//@Configuration
//@EnableWebSecurity
//public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .antMatchers(HttpMethod.POST, "/api/v2/schemas").authenticated()
//                .and()
//                .authorizeRequests()
////                .antMatchers(HttpMethod.POST, "/about").authenticated()
//                .antMatchers("/**").permitAll().antMatchers("/**/*").permitAll();
////                .antMatchers("/", "/api").permitAll()
////                .anyRequest().authenticated()
////                .and()
////                .formLogin()
////                .loginPage("/login")
////                .permitAll()
////                .and()
////                .logout()
////                .permitAll();
//    }
//}

public class WebSecurityConfig {

}
