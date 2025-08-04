package org.minu.dnd13th3backend.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/oauth2")
@Tag(name = "OAuth2 Login", description = "OAuth2 Google 로그인 시작점")
public class OAuth2Controller {

    @Operation(
            summary = "Google OAuth2 로그인 시작",
            description = """
                    Google OAuth2 로그인을 시작합니다.
                    
                    ### 사용 방법:
                    1. 프론트엔드에서 이 URL로 브라우저 리다이렉트
                    2. Google 로그인 페이지로 이동
                    3. 로그인 완료 후 자동으로 백엔드에서 JWT 토큰 생성
                    4. 프론트엔드 URL로 토큰과 함께 리다이렉트
                    
                    ### 전체 플로우:
                    ```
                    1. GET /oauth2/authorization/google
                    2. → Google 로그인 페이지
                    3. → Google 인증 완료
                    4. → 백엔드 콜백 처리
                    5. → http://localhost:3000/login/success?accessToken=...&refreshToken=...
                    ```
                    
                    **주의: 이 API는 브라우저에서만 동작합니다. Swagger에서 테스트하지 마세요.**
                    """
    )
    @ApiResponse(
            responseCode = "302",
            description = "Google OAuth2 로그인 페이지로 리다이렉트",
            content = @Content(
                    examples = @ExampleObject(
                            name = "redirect",
                            value = "https://accounts.google.com/oauth/authorize?client_id=..."
                    )
            )
    )
    @GetMapping("/authorization/google")
    public RedirectView startGoogleLogin() {
        return new RedirectView("/oauth2/authorization/google");
    }
}