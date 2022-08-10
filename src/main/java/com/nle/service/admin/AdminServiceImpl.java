package com.nle.service.admin;

import com.nle.controller.dto.JWTToken;
import com.nle.controller.dto.admin.AdminLoginDTO;
import com.nle.controller.dto.admin.AdminProfileDTO;
import com.nle.entity.admin.Admin;
import com.nle.exception.CommonException;
import com.nle.exception.ResourceNotFoundException;
import com.nle.repository.AdminRepository;
import com.nle.security.SecurityUtils;
import com.nle.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Override
    public JWTToken loginAdmin(AdminLoginDTO adminLoginDTO) {
        Optional<Admin> optionalAdmin = adminRepository.findByEmail(adminLoginDTO.getEmail());
        if (optionalAdmin.isEmpty()) {
            throw new ResourceNotFoundException("Admin with email: '" + adminLoginDTO.getEmail() + "' doesn't exist");
        }
        Admin admin = optionalAdmin.get();
        if (!admin.isActivated()) {
            throw new CommonException("Admin with email " + adminLoginDTO.getEmail() + " is not activated");
        }
        // compare password
        boolean matches = passwordEncoder.matches(adminLoginDTO.getPassword(), admin.getPassword());
        if (!matches) {
            throw new CommonException("Invalid Admin Credentials");
        }
        // generate token
        List<GrantedAuthority> grantedAuthorities = Collections.singletonList(new SimpleGrantedAuthority(admin.getRoles()));
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(admin.getEmail(), admin.getPassword(), grantedAuthorities);
        String jwt = tokenProvider.createToken(authentication);
        return new JWTToken(jwt);
    }

    @Override
    public AdminProfileDTO getAdminProfile() {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty()) {
            throw new ResourceNotFoundException("No user login information");
        }
        Optional<Admin> optionalAdmin = adminRepository.findByEmail(currentUserLogin.get());
        if (optionalAdmin.isPresent()) {
            AdminProfileDTO adminProfileDTO = new AdminProfileDTO();
            BeanUtils.copyProperties(optionalAdmin.get(), adminProfileDTO);
            return adminProfileDTO;
        }
        return null;
    }
}
