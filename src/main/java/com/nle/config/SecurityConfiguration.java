package com.nle.config;

import com.nle.security.DepoOwnerUserDetailsService;
import com.nle.security.impersonate.SwitchUserAuthenticationSuccessHandler;
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
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.util.UrlPathHelper;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final TokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final DepoOwnerUserDetailsService depoOwnerUserDetailsService;
    private final SwitchUserAuthenticationSuccessHandler switchUserAuthenticationSuccessHandler;

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
                        , "/api/admins/authenticate"
                        , "/api/depo-worker-accounts/join"
                        , "/api/depo-worker-accounts/complete"
                        , "/api/depo-worker-accounts/status/**"
                        , "/api/ftp/**"
                        , "/api/depo-worker-accounts/authenticate"
                        , "/api/reset-password"
                        , "/api/forgot-password"
                        , "/api/fleets"
                        , "/api/contact-us"
                        , "/api/booking/depo/**"
                        , "/api/booking/otp/**"
                        , "/api/booking/payment/callback"
                        , "/api/booking/payment/callback/invoice"
                        , "/api/payment/callback/disbursement"
                        , "/api/item-type"
                        , "/api/item-type/iso"
                        , "/api/insw-shipping"
                        , "/api/applicants/portal/getdepo-byloc-shippingline"
                ).permitAll()
            .antMatchers("/impersonate"
                    , "/api/admins/profile"
                    , "/api/admins/update"
                    , "/api/admins/change-password"
                    , "/api/applicants/**"
                    , "/api/switchUser/impersonate/**"
                    , "/api/fleets/addFleet"
                    , "/api/item-type/addItemType"
                    , "/api/item-type/iso/addIso"
                    , "/api/insw-shipping/add-Insw-Shipping"
                    ).access("hasRole('ADMIN')")
            .anyRequest()
                .authenticated()
            .and()
            .apply(securityConfigurerAdapter());
        // @formatter:on
        return http.build();
    }

    @Bean
    public SwitchUserFilter switchUserFilter() {
        SwitchUserFilter filter = new SwitchUserFilter();
        filter.setUserDetailsService(depoOwnerUserDetailsService);
        filter.setSwitchUserMatcher(new AntPathRequestMatcher("/impersonate", "GET", true, new UrlPathHelper()));
        filter.setSwitchFailureUrl("/impersonate-fail");
        filter.setSuccessHandler(switchUserAuthenticationSuccessHandler);
        return filter;
    }

    private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider);
    }
}
