package com.nle.shared.service.admin;

import com.nle.ui.model.JWTToken;
import com.nle.ui.model.admin.AdminLoginDTO;
import com.nle.ui.model.admin.AdminProfileDTO;
import com.nle.ui.model.request.UpdateAdminRequest;

public interface AdminService {
    JWTToken loginAdmin(AdminLoginDTO adminLoginDTO);

    AdminProfileDTO getAdminProfile();

    JWTToken forcedImpersonate(String email);

    AdminProfileDTO updateAdminProfile(UpdateAdminRequest request);
    void updateAdminPassword(UpdateAdminRequest request);
}
