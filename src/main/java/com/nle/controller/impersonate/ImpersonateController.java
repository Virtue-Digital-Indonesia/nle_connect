package com.nle.controller.impersonate;

import com.nle.controller.dto.JWTToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.nle.constant.AppConstant.DEPO_OWNER_IMPERSONATE_TOKEN;

@RestController
@RequestMapping("/api/switchUser")
@RequiredArgsConstructor
public class ImpersonateController {
    @Operation(description = "Get impersonate token from session then response to client", operationId = "findByCode", summary = "Get impersonate token from session then response to client")
    @SecurityRequirement(name = "nleapi")
    @GetMapping
    public ResponseEntity<JWTToken> switchUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String jwtToken = (String) session.getAttribute(DEPO_OWNER_IMPERSONATE_TOKEN);
        session.removeAttribute(DEPO_OWNER_IMPERSONATE_TOKEN);
        return ResponseEntity.ok(new JWTToken(jwtToken));
    }
}
