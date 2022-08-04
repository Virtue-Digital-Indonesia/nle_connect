package com.nle.service.admin;

import com.nle.controller.dto.JWTToken;
import com.nle.controller.dto.admin.AdminLoginDTO;

public interface AdminService {
    JWTToken loginAdmin(AdminLoginDTO adminLoginDTO);
}
