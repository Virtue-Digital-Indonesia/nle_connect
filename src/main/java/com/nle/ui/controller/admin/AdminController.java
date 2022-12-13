package com.nle.ui.controller.admin;

import com.nle.ui.model.JWTToken;
import com.nle.ui.model.admin.AdminLoginDTO;
import com.nle.ui.model.admin.AdminProfileDTO;
import com.nle.io.repository.dto.ShippingLineStatistic;
import com.nle.shared.service.admin.AdminService;
import com.nle.ui.model.request.UpdateAdminRequest;
import com.nle.ui.model.request.ChangeAdminPasswordRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(description = "Authenticate admin by email and password", operationId = "authenticate", summary = "Authenticate admin by email and password")
    @PostMapping("/authenticate")
    public ResponseEntity<JWTToken> authenticate(@Valid @RequestBody AdminLoginDTO adminLoginDTO) {
        return ResponseEntity.ok(adminService.loginAdmin(adminLoginDTO));
    }

    @Operation(description = "Update admin profile", operationId = "authenticate", summary = "Update admin profile")
    @SecurityRequirement(name = "nleapi")
    @PutMapping("/update")
    public ResponseEntity<AdminProfileDTO> updateAdminProfile(@Valid @RequestBody UpdateAdminRequest request) {
        return ResponseEntity.ok(adminService.updateAdminProfile(request));
    }

    @Operation(description = "Authenticate admin by email and password", operationId = "authenticate", summary = "Authenticate admin by email and password")
    @SecurityRequirement(name = "nleapi")
    @PutMapping("/change-password")
    public ResponseEntity<String> changeAdminPassword(@Valid @RequestBody ChangeAdminPasswordRequest request) {
        adminService.updateAdminPassword(request);
        return ResponseEntity.ok("Password has been updated successfully");
    }

    @Operation(description = "Get Admin Profile", operationId = "getAdminProfile", summary = "Get Admin Profile")
    @GetMapping(value = "/profile")
    @SecurityRequirement(name = "nleapi")
    public ResponseEntity<AdminProfileDTO> getAdminProfile() {
        return ResponseEntity.ok(adminService.getAdminProfile());
    }

}
