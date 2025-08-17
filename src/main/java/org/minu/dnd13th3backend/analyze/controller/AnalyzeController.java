package org.minu.dnd13th3backend.analyze.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.minu.dnd13th3backend.analyze.dto.response.AiFeedbackResponse;
import org.minu.dnd13th3backend.analyze.service.AnalyzeService;
import org.springframework.http.ResponseEntity;
import org.minu.dnd13th3backend.user.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analyze")
@RequiredArgsConstructor
@Tag(name = "Analyze", description = "AI 피드백 및 분석 API")
public class AnalyzeController {

    private final AnalyzeService analyzeService;

    @Operation(
            summary = "AI 피드백 조회",
            description = "기간과 유형을 지정하여 AI 피드백을 조회합니다. period=day|week, type은 screentime, timer 중 선택 가능합니다. 기존 피드백이 있으면 캐시된 결과를 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "AI 피드백 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = AiFeedbackResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                                    {
                                      "period": "week",
                                      "startDate": "2025-01-06",
                                      "endDate": "2025-01-12",
                                      "type": "screentime",
                                      "feedback": [
                                        "이번 주 총 2,100분 사용하셨네요!",
                                        "하루 평균 300분으로 조금 높은 편이에요.",
                                        "주중에는 잘 관리하셨지만 주말에 늘어났어요.",
                                        "다음 주엔 목표 시간에 맞춰 도전해봐요!"
                                      ]
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 파라미터",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\"status\":400,\"message\":\"유효하지 않은 period 또는 type 파라미터입니다.\"}"
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
    @GetMapping("/ai-feedback")
    public ResponseEntity<AiFeedbackResponse> getAiFeedback(
            @Parameter(
                    description = "분석 기간 (day: 일간, week: 주간)",
                    required = true,
                    example = "day"
            )
            @RequestParam("period") String period,
            
            @Parameter(
                    description = "분석 유형 (screentime, timer)",
                    required = true,
                    example = "screentime"
            )
            @RequestParam("type") String type,
            
            @AuthenticationPrincipal User user) {
        
        AiFeedbackResponse response = analyzeService.generateAiFeedback(user.getId(), period, type);
        
        return ResponseEntity.ok(response);
    }
}