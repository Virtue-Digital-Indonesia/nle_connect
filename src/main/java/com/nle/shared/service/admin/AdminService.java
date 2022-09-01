package com.nle.shared.service.admin;

import com.nle.ui.model.JWTToken;
import com.nle.ui.model.admin.AdminLoginDTO;
import com.nle.ui.model.admin.AdminProfileDTO;

public interface AdminService {
    JWTToken loginAdmin(AdminLoginDTO adminLoginDTO);

    AdminProfileDTO getAdminProfile();
}