package com.nle.shared.service.admin;

import com.nle.exception.BadRequestException;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.repository.DepoOwnerAccountRepository;
import com.nle.security.AuthoritiesConstants;
import com.nle.ui.model.JWTToken;
import com.nle.ui.model.admin.AdminLoginDTO;
import com.nle.ui.model.admin.AdminProfileDTO;
import com.nle.io.entity.admin.Admin;
import com.nle.exception.CommonException;
import com.nle.exception.ResourceNotFoundException;
import com.nle.io.repository.AdminRepository;
import com.nle.security.SecurityUtils;
import com.nle.security.jwt.TokenProvider;
import com.nle.ui.model.request.ChangeAdminPasswordRequest;
import com.nle.ui.model.request.UpdateAdminRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
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
    private final DepoOwnerAccountRepository depoOwnerAccountRepository;

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
            return toDTO(optionalAdmin.get());
        }
        return null;
    }

    public JWTToken forcedImpersonate(String email) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (!currentUserLogin.isEmpty()) {
            Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountRepository.findByCompanyEmail(email);

            if (depoOwnerAccount.isEmpty())
                throw new BadRequestException("Cannot find depo with email "+ email +"!");

            String token = tokenProvider.generateManualToken(depoOwnerAccount.get().getCompanyEmail(), AuthoritiesConstants.IMPERSONATE_DEPO);
            return  new JWTToken(token);
        }

        return new JWTToken("null");
    }

    @Override
    public AdminProfileDTO updateAdminProfile(UpdateAdminRequest request) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty())
            throw new BadRequestException("Invalid token");
        Admin admin= adminRepository.findByEmail(currentUserLogin.get()).orElseThrow(()-> new BadRequestException("Invalid admin account"));
        if(request.getFullName()!=null)
            admin.setFullName(request.getFullName());
        if(request.getPhoneNumber()!=null)
            admin.setPhoneNumber(request.getPhoneNumber());
        if(request.getEmail()!=null)
            admin.setEmail(request.getEmail());
        return toDTO(adminRepository.save(admin));
    }

    @Override
    public void updateAdminPassword(ChangeAdminPasswordRequest request) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty())
            throw new BadRequestException("Invalid token");
        Admin admin= adminRepository.findByEmail(currentUserLogin.get()).orElseThrow(()-> new BadRequestException("Invalid admin account"));
        if(passwordEncoder.matches(request.getOldPassword(), admin.getPassword())){
            if(request.getNewPassword().equals(request.getConfirmNewPassword())){
                admin.setPassword(passwordEncoder.encode(request.getNewPassword()));
                adminRepository.save(admin);
            }else
                throw new BadRequestException("Invalid confirm_new_password");
        }else
            throw new BadRequestException("Invalid old_password");
    }

    public AdminProfileDTO toDTO(Admin admin){
        AdminProfileDTO adminProfileDTO= new AdminProfileDTO();
        BeanUtils.copyProperties(admin, adminProfileDTO);
        return adminProfileDTO;
    }
}
