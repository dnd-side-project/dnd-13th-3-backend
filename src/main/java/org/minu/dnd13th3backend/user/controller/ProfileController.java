package org.minu.dnd13th3backend.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.minu.dnd13th3backend.auth.service.AuthService;
import org.minu.dnd13th3backend.user.dto.request.ProfileRequest;
import org.minu.dnd13th3backend.user.dto.request.ProfileUpdateRequest;
import org.minu.dnd13th3backend.user.dto.response.ProfileDetailResponse;
import org.minu.dnd13th3backend.user.dto.response.ProfileResponse;
import org.minu.dnd13th3backend.user.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "사용자 프로필 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {

    private final ProfileService profileService;
    private final AuthService authService;

    @Operation(
            summary = "프로필 조회",
            description = "JWT 토큰으로 사용자 프로필을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "프로필 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = ProfileDetailResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                                    {
                                      "id": "1",
                                      "nickname": "종훈",
                                      "goal": {
                                        "type": "FOCUS_IMPROVEMENT",
                                        "custom": null
                                      },
                                      "screenTimeGoal": {
                                        "type": "2HOURS",
                                        "custom": null
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "프로필을 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\"status\":404,\"message\":\"프로필을 찾을 수 없습니다.\"}"
                            )
                    )
            )
    })
    @GetMapping("/profile")
    public ResponseEntity<ProfileDetailResponse> getProfile(
            @Parameter(
                    description = "Bearer {accessToken} 형태의 Access Token", 
                    required = true,
                    example = "Bearer eyJhbGciOiJIUzUxMiJ9..."
            )
            @RequestHeader("Authorization") String authHeader) {
        
        String accessToken = authHeader.replace("Bearer ", "");
        String userId = authService.getUserIdFromToken(accessToken);
        
        ProfileDetailResponse response = profileService.getProfile(Long.valueOf(userId));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "프로필 등록",
            description = "새로운 사용자 프로필을 등록합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "프로필 등록 성공",
                    content = @Content(
                            schema = @Schema(implementation = ProfileResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                                    {
                                      "message": "프로필이 성공적으로 등록되었습니다."
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\"status\":400,\"message\":\"닉네임은 필수입니다.\"}"
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
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 프로필이 존재함",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\"status\":409,\"message\":\"이미 프로필이 존재합니다.\"}"
                            )
                    )
            )
    })
    @PostMapping("/profile")
    public ResponseEntity<ProfileResponse> createProfile(
            @Parameter(
                    description = "Bearer {accessToken} 형태의 Access Token", 
                    required = true,
                    example = "Bearer eyJhbGciOiJIUzUxMiJ9..."
            )
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ProfileRequest request) {
        
        String accessToken = authHeader.replace("Bearer ", "");
        String userId = authService.getUserIdFromToken(accessToken);
        
        profileService.createProfile(Long.valueOf(userId), request);
        return ResponseEntity.ok(ProfileResponse.success("프로필이 성공적으로 등록되었습니다."));
    }

    @Operation(
            summary = "프로필 수정",
            description = "사용자 프로필의 목표 정보를 수정합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "프로필 수정 성공",
                    content = @Content(
                            schema = @Schema(implementation = ProfileResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                                    {
                                      "message": "프로필이 성공적으로 수정되었습니다."
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\"status\":400,\"message\":\"목표는 필수입니다.\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "프로필을 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\"status\":404,\"message\":\"프로필을 찾을 수 없습니다.\"}"
                            )
                    )
            )
    })
    @PatchMapping("/profile")
    public ResponseEntity<ProfileResponse> updateProfile(
            @Parameter(
                    description = "Bearer {accessToken} 형태의 Access Token", 
                    required = true,
                    example = "Bearer eyJhbGciOiJIUzUxMiJ9..."
            )
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ProfileUpdateRequest request) {
        
        String accessToken = authHeader.replace("Bearer ", "");
        String userId = authService.getUserIdFromToken(accessToken);
        
        profileService.updateProfile(Long.valueOf(userId), request);
        return ResponseEntity.ok(ProfileResponse.success("프로필이 성공적으로 수정되었습니다."));
    }

}