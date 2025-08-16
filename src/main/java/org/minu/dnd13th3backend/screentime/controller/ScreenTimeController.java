package org.minu.dnd13th3backend.screentime.controller;

import lombok.RequiredArgsConstructor;
import org.minu.dnd13th3backend.common.dto.ResponseDto;
import org.minu.dnd13th3backend.screentime.dto.response.ScreenTimeGenerateResponse;
import org.minu.dnd13th3backend.screentime.entity.ScreenTime;
import org.minu.dnd13th3backend.screentime.service.ScreenTimeService;
import org.minu.dnd13th3backend.user.entity.User;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/screentime")
@RequiredArgsConstructor
public class ScreenTimeController {

    private final ScreenTimeService screenTimeService;

    @PostMapping
    public ResponseEntity<ResponseDto<ScreenTimeGenerateResponse>> generateScreenTime(
            @AuthenticationPrincipal User user
    ) {
        ScreenTime screenTime = screenTimeService.generateAndSaveScreenTime(user);
        ScreenTimeGenerateResponse response = new ScreenTimeGenerateResponse(screenTime);
        return ResponseEntity.ok(ResponseDto.success("스크린타임이 성공적으로 생성/갱신되었습니다.", response));
    }

    @GetMapping
    public ResponseEntity<ResponseDto<?>> getScreenTime(
            @RequestParam(name = "period") String period,
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal User user
    ) {
        Object responseData = screenTimeService.getScreenTime(period, date, user);
        return ResponseEntity.ok(ResponseDto.success("스크린타임 조회에 성공했습니다.", responseData));
    }
}
