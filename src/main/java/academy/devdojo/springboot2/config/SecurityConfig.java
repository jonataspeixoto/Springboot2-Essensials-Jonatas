package academy.devdojo.springboot2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import academy.devdojo.springboot2.service.DevDojoUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Configuration
@Log4j2
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final DevDojoUserDetailsService devDojoUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager userDetailsServiceAuth) throws Exception {
//        CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
//        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
//        requestHandler.setCsrfRequestAttributeName("_csrf");

        http
            .csrf()
            .disable()
            .authorizeHttpRequests((authz) -> authz
                .anyRequest()
                .authenticated()
            )
//            .csrf((csrf) -> csrf
//                .csrfTokenRepository(tokenRepository)
//                .csrfTokenRequestHandler(requestHandler)
//            )
            .formLogin()
            .and()
            .httpBasic()
            .and()
            .authenticationManager(userDetailsServiceAuth);
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider jpaDaoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(devDojoUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    public UserDetailsService inMemoryUserDetailsService() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        log.info("Password encoded {}", passwordEncoder.encode("academy"));

        UserDetails admin = User.builder()
            .username("jonatas2")
            .password(passwordEncoder.encode("academy"))
            .roles("USER", "ADMIN")
            .build();

        UserDetails user = User.builder()
            .username("devDojo2")
            .password(passwordEncoder.encode("academy"))
            .roles("USER")
            .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public DaoAuthenticationProvider inMemoryDaoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(inMemoryUserDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(inMemoryDaoAuthenticationProvider());
        authenticationManagerBuilder.authenticationProvider(jpaDaoAuthenticationProvider());

        return authenticationManagerBuilder.build();
    }
}
