package com.nle.security.jwt;

import com.nle.config.prop.AppProperties;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JWTConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final TokenProvider tokenProvider;
    private final AppProperties appProperties;

    public JWTConfigurer(TokenProvider tokenProvider, AppProperties appProperties) {
        this.tokenProvider = tokenProvider;
        this.appProperties = appProperties;
    }

    @Override
    public void configure(HttpSecurity http) {
        JWTFilter customFilter = new JWTFilter(tokenProvider, appProperties);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
