package org.minu.dnd13th3backend.timer.controller;

import lombok.RequiredArgsConstructor;
import org.minu.dnd13th3backend.common.dto.ResponseDto;
import org.minu.dnd13th3backend.timer.dto.request.TimerPostRequest;
import org.minu.dnd13th3backend.timer.dto.response.TimerGetResponse;
import org.minu.dnd13th3backend.timer.dto.response.TimerPostResponse;
import org.minu.dnd13th3backend.timer.entity.Timer;
import org.minu.dnd13th3backend.timer.service.TimerService;
import org.minu.dnd13th3backend.user.entity.User;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/timer")
@RequiredArgsConstructor
public class TimerController {

    private final TimerService timerService;

    @PostMapping
    public ResponseEntity<ResponseDto<TimerPostResponse>> createTimer(
            @RequestBody TimerPostRequest requestDto,
            @AuthenticationPrincipal User user
    ) {
        Timer timer = timerService.createTimer(requestDto, user);

        TimerPostResponse response = new TimerPostResponse(timer);

        return ResponseEntity.ok(ResponseDto.success("타이머 기록이 성공적으로 등록되었습니다.", response));
    }

    @GetMapping
    public ResponseEntity<ResponseDto<TimerGetResponse>> getTimer(
            @RequestParam(name = "period") String period,
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal User user
    ) {
        TimerGetResponse responseData = timerService.getTimer(period, date, user);
        return ResponseEntity.ok(ResponseDto.success("타이머 조회에 성공했습니다.", responseData));
    }
}
