package com.nle.security;

import com.nle.entity.DepoOwnerAccount;
import com.nle.exception.CommonException;
import com.nle.repository.DepoOwnerAccountRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component("userDetailsService")
@RequiredArgsConstructor
public class DepoOwnerUserDetailsService implements UserDetailsService {
    private final Logger log = LoggerFactory.getLogger(DepoOwnerUserDetailsService.class);

    private final DepoOwnerAccountRepository depoOwnerAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String companyEmail) throws UsernameNotFoundException {
        log.debug("Authenticating {}", companyEmail);
        if (new EmailValidator().isValid(companyEmail, null)) {
            return depoOwnerAccountRepository
                .findByCompanyEmail(companyEmail)
                .map(user -> createSpringSecurityUser(companyEmail, user))
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + companyEmail + " was not found in the database"));
        }
        return null;
    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(String companyEmail, DepoOwnerAccount user) {
        if (!user.isActivated()) {
            throw new CommonException("User " + companyEmail + " was not activated");
        }
        List<GrantedAuthority> grantedAuthorities = Collections.singletonList(new SimpleGrantedAuthority("USER"));
        return new org.springframework.security.core.userdetails.User(companyEmail, user.getPassword(), grantedAuthorities);
    }
}
