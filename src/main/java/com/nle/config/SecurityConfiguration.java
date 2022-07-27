package com.nle.config;

import com.nle.config.prop.AppProperties;
import com.nle.security.jwt.JWTConfigurer;
import com.nle.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final TokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final AppProperties appProperties;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
            .antMatchers(HttpMethod.OPTIONS, "/**")
            .antMatchers("/resources/**")
            .antMatchers("/facebook.png")
            .antMatchers("/mail.png")
            .antMatchers("/phone.png")
            .antMatchers("/product-nle-connect.svg")
            .antMatchers("/product-nle-connect-uppercase.png")
            .antMatchers("/template-instagram.png")
            .antMatchers("/template-linkedin.png")
            .antMatchers("/Vector.png")
            .antMatchers("/web.png")
            .antMatchers("/tx_deponame_timestamp.csv")
            .antMatchers("/v3/api-docs/**")
            .antMatchers("/swagger-ui/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .csrf().disable()
            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling()
            .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .authorizeRequests()
                .antMatchers("/api/register/**"
                    , "/api/activate/**"
                    , "/api/authenticate"
                    , "/api/depo-worker-accounts/join"
                    , "/api/depo-worker-accounts/complete"
                    , "/api/depo-worker-accounts/status/**"
                    , "/api/ftp/**"
                    , "/api/depo-worker-accounts/authenticate"
                ).permitAll()
            .anyRequest()
                .authenticated()
            .and()
            .apply(securityConfigurerAdapter());
        // @formatter:on
        return http.build();
    }

    private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider);
    }
}
