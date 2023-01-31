package com.nle.ui.controller.impersonate;

import com.nle.shared.service.admin.AdminService;
import com.nle.ui.model.JWTToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.nle.constant.AppConstant.DEPO_OWNER_IMPERSONATE_TOKEN;

@RestController
@RequestMapping("/api/switchUser")
@RequiredArgsConstructor
public class ImpersonateController {

    private final AdminService adminService;

    @Operation(description = "Get impersonate token from session then response to client", operationId = "findByCode", summary = "Get impersonate token from session then response to client")
    @SecurityRequirement(name = "nleapi")
    @GetMapping
    public ResponseEntity<JWTToken> switchUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String jwtToken = (String) session.getAttribute(DEPO_OWNER_IMPERSONATE_TOKEN);
        session.removeAttribute(DEPO_OWNER_IMPERSONATE_TOKEN);
        return ResponseEntity.ok(new JWTToken(jwtToken));
    }

    @Operation(description = "Get impersonate token depo owner", operationId = "giveToken", summary = "Get impersonate token depo owner")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/impersonate/{token}")
    public ResponseEntity<JWTToken> giveToken(@PathVariable("token") String jwtToken) {
        return ResponseEntity.ok(new JWTToken(jwtToken));
    }

    @Operation(description = "forced get impersonate token depo owner", operationId = "forcedIn", summary = "forced get impersonate token depo owner")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/impersonate/forced")
    public ResponseEntity<JWTToken> forcedIn(@RequestParam("username") String username) {
        return ResponseEntity.ok(adminService.forcedImpersonate(username));
    }

    @Operation(description = "check token", operationId = "findByCode", summary = "check token meaning", hidden = true)
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/checkToken")
    public String tokenPrincipal(Authentication authentication) {
        return authentication.getPrincipal().toString();
    }
}
