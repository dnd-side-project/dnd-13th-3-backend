package org.minu.dnd13th3backend.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.minu.dnd13th3backend.auth.dto.TokenResponse;
import org.minu.dnd13th3backend.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        TokenResponse tokenResponse = authService.processOAuth2Login(oauth2User);

        HttpSession session = request.getSession(false);
        boolean isNativeApp = session != null && "true".equals(session.getAttribute("is_native_app"));

        String targetUrl;

        if (isNativeApp) {
            targetUrl = UriComponentsBuilder.fromUriString("minu://login/success")
                    .queryParam("accessToken", tokenResponse.getAccessToken())
                    .queryParam("refreshToken", tokenResponse.getRefreshToken())
                    .queryParam("characterIndex", tokenResponse.getCharacterIndex())
                    .queryParam("isNewUser", tokenResponse.getIsNewUser())
                    .build().toUriString();
            log.info("Redirecting to native app: {}", targetUrl);
        } else {
            targetUrl = UriComponentsBuilder.fromUriString(frontendBaseUrl + "/login/success")
                    .queryParam("accessToken", tokenResponse.getAccessToken())
                    .queryParam("refreshToken", tokenResponse.getRefreshToken())
                    .queryParam("characterIndex", tokenResponse.getCharacterIndex())
                    .queryParam("isNewUser", tokenResponse.getIsNewUser())
                    .build().toUriString();
            log.info("Redirecting to web: {}", targetUrl);
        }

        if (session != null) {
            session.removeAttribute("is_native_app");
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}

