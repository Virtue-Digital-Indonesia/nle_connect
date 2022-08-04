package com.nle.controller.admin;

import com.nle.controller.dto.JWTToken;
import com.nle.controller.dto.admin.AdminLoginDTO;
import com.nle.service.admin.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
