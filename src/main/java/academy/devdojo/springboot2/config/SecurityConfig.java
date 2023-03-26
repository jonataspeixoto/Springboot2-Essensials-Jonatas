package academy.devdojo.springboot2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import lombok.extern.log4j.Log4j2;

@Configuration
@Log4j2
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
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
            .httpBasic();
        return http.build();
    }

    @Bean
    public UserDetailsService users() throws Exception{
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        log.info("Password encoded {}", passwordEncoder.encode("test"));

        UserDetails admin = User.builder()
            .username("jonatas")
            .password(passwordEncoder.encode("academy"))
            .roles("USER", "ADMIN")
            .build();

        UserDetails user = User.builder()
            .username("devDojo")
            .password(passwordEncoder.encode("academy"))
            .roles("USER")
            .build();

        return new InMemoryUserDetailsManager(admin, user);
    }
}
