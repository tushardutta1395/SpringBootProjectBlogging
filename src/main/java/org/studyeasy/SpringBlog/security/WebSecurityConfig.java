package org.studyeasy.SpringBlog.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    private static final String[] WHITELIST = new String[] {
            "/**",
            "/login",
            "/register",
            "/db-console/**",
            "/resources/**",
            "/posts/**"
    };

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .authorizeHttpRequests(requests -> requests
                .requestMatchers(WHITELIST).permitAll()
                .requestMatchers("/profile/**").authenticated()
                .requestMatchers("/update_photo/**").authenticated()
                .requestMatchers("/posts/add/**").authenticated()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/editor/**").hasAnyRole("ADMIN", "EDITOR"))
                .formLogin(login -> login
                .loginPage("/login").loginProcessingUrl("/login")
                .usernameParameter("email").passwordParameter("password")
                .defaultSuccessUrl("/", true).failureUrl("/login?error")
                .permitAll())
                .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/"))
                .rememberMe(rememberMe -> rememberMe
                .rememberMeParameter("remember-me"));

        // TODO: remove these after upgrading the DB from H2 infile DB
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.headers(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }
}
