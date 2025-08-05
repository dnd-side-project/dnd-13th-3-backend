package org.minu.dnd13th3backend.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.minu.dnd13th3backend.auth.dto.TokenResponse;
import org.minu.dnd13th3backend.auth.service.AuthService;
import org.minu.dnd13th3backend.auth.service.RefreshTokenService;
import org.minu.dnd13th3backend.auth.service.TokenBlacklistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "OAuth2 인증 및 JWT 토큰 관리 API")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;

    @Operation(hidden = true)
    @GetMapping("/oauth2/success")  
    public RedirectView oauth2Success(@AuthenticationPrincipal OAuth2User oauth2User) {
        TokenResponse tokenResponse = authService.processOAuth2Login(oauth2User);
        
        String frontendUrl = "http://localhost:3000/login/success" +
                "?accessToken=" + tokenResponse.getAccessToken() +
                "&refreshToken=" + tokenResponse.getRefreshToken();
        
        return new RedirectView(frontendUrl);
    }

    @Operation(hidden = true)
    @GetMapping("/oauth2/failure")
    public ResponseEntity<String> oauth2Failure() {
        return ResponseEntity.badRequest().body("OAuth2 로그인에 실패했습니다.");
    }

    @Operation(
            summary = "JWT 토큰 갱신", 
            description = "Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급받습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "토큰 갱신 성공",
                    content = @Content(
                            schema = @Schema(implementation = TokenResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                                    {
                                      "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzU0Mjg3NTQ1LCJleHAiOjE3NTQyODkzNDV9...",
                                      "refreshToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzU0Mjg3NTQ1LCJleHAiOjE3NTQ4OTIzNDV9..."
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401", 
                    description = "유효하지 않은 Refresh Token",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\"status\":401,\"message\":\"유효하지 않은 리프레시 토큰입니다.\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404", 
                    description = "사용자를 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\"status\":404,\"message\":\"사용자를 찾을 수 없습니다.\"}"
                            )
                    )
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @Parameter(
                    description = "Bearer {refreshToken} 형태의 Refresh Token", 
                    required = true,
                    example = "Bearer eyJhbGciOiJIUzUxMiJ9..."
            )
            @RequestHeader("Authorization") String refreshToken) {
        String token = refreshToken.replace("Bearer ", "");
        TokenResponse tokenResponse = authService.refreshToken(token);
        return ResponseEntity.ok(tokenResponse);
    }

    @Operation(
            summary = "로그아웃", 
            description = "Access Token을 무효화하여 로그아웃합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204", 
                    description = "로그아웃 성공"
            ),
            @ApiResponse(
                    responseCode = "401", 
                    description = "유효하지 않은 토큰",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\"status\":401,\"message\":\"유효하지 않은 토큰입니다.\"}"
                            )
                    )
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Parameter(
                    description = "Bearer {accessToken} 형태의 Access Token", 
                    required = true,
                    example = "Bearer eyJhbGciOiJIUzUxMiJ9..."
            )
            @RequestHeader("Authorization") String authHeader) {
        
        String accessToken = authHeader.replace("Bearer ", "");
        String userId = authService.getUserIdFromToken(accessToken);
        
        tokenBlacklistService.blacklistToken(accessToken);
        refreshTokenService.deleteRefreshToken(userId);
        
        return ResponseEntity.noContent().build();
    }
}