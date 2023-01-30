package com.nle.security.impersonate;

import com.nle.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.nle.constant.AppConstant.DEPO_OWNER_IMPERSONATE_TOKEN;

@Component
@RequiredArgsConstructor
public class SwitchUserAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String jwt = tokenProvider.createToken(authentication);
//        HttpSession session = request.getSession(true);
//        session.setAttribute(DEPO_OWNER_IMPERSONATE_TOKEN, jwt);
//        getRedirectStrategy().sendRedirect(request, response, "/api/switchUser");
//        response.sendRedirect("admin/home.html");
        getRedirectStrategy().sendRedirect(request, response, "/api/switchUser/impersonate/"+jwt);
    }
}
