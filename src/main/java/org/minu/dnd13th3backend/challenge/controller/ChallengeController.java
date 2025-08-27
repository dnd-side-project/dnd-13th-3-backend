package org.minu.dnd13th3backend.challenge.controller;

import lombok.RequiredArgsConstructor;
import org.minu.dnd13th3backend.challenge.dto.request.ChallengeCreateRequest;
import org.minu.dnd13th3backend.challenge.dto.request.InviteJoinRequest;
import org.minu.dnd13th3backend.challenge.dto.response.*;
import org.minu.dnd13th3backend.challenge.entity.Challenge;
import org.minu.dnd13th3backend.challenge.service.ChallengeService;
import org.minu.dnd13th3backend.common.dto.ResponseDto;
import org.minu.dnd13th3backend.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/challenge")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    @PostMapping
    public ResponseEntity<ResponseDto<ChallengeCreateResponse>> createChallenge(
            @RequestBody ChallengeCreateRequest request,
            @AuthenticationPrincipal User user
    ) {
        Challenge challenge = challengeService.createChallenge(request, user);
        ChallengeCreateResponse responseData = new ChallengeCreateResponse(challenge.getId());
        return ResponseEntity.ok(ResponseDto.success("챌린지가 성공적으로 생성되었습니다.", responseData));
    }

    @GetMapping
    public ResponseEntity<ResponseDto<ChallengeListResponse>> getChallenges(
            @AuthenticationPrincipal User user
    ) {
        ChallengeListResponse responseData = challengeService.getChallenges(user);

        if (responseData.getChallenges().isEmpty()) {
            return ResponseEntity.ok(ResponseDto.success("참여 중인 챌린지가 없습니다.", responseData));
        }

        return ResponseEntity.ok(ResponseDto.success("챌린지 조회가 성공했습니다.", responseData));
    }

    @GetMapping("/history")
    public ResponseEntity<ResponseDto<ChallengeListResponse>> getChallengeHistory(
            @AuthenticationPrincipal User user
    ) {
        ChallengeListResponse responseData = challengeService.getChallengeHistory(user);
        if (responseData.getChallenges().isEmpty()) {
            return ResponseEntity.ok(ResponseDto.success("완료된 챌린지가 없습니다.", responseData));
        }
        return ResponseEntity.ok(ResponseDto.success("완료된 챌린지 조회가 성공했습니다.", responseData));
    }

    @PostMapping("/inviteUrl/{challengeId}")
    public ResponseEntity<ResponseDto<InviteUrlCreateResponse>> createInviteUrl(
            @PathVariable Long challengeId,
            @AuthenticationPrincipal User user
    ) {
        String url = challengeService.generateInviteLink(challengeId, user);
        InviteUrlCreateResponse response = new InviteUrlCreateResponse(url);
        return ResponseEntity.ok(ResponseDto.success("초대 링크가 성공적으로 생성되었습니다.", response));
    }

    @PostMapping("/join")
    public ResponseEntity<ResponseDto<InviteJoinResponse>> joinChallenge(
            @RequestBody InviteJoinRequest request,
            @AuthenticationPrincipal User user
    ) {
        Challenge challenge = challengeService.joinChallenge(request, user);
        InviteJoinResponse response = new InviteJoinResponse(challenge, "챌린지에 성공적으로 참여했습니다.");
        return ResponseEntity.ok(ResponseDto.success(response.getMessage(), response));
    }
}
