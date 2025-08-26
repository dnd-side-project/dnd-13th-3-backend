package org.minu.dnd13th3backend.challenge.service;

import lombok.RequiredArgsConstructor;
import org.minu.dnd13th3backend.challenge.dto.request.ChallengeCreateRequest;
import org.minu.dnd13th3backend.challenge.dto.request.InviteJoinRequest;
import org.minu.dnd13th3backend.challenge.dto.response.ChallengeGetResponse;
import org.minu.dnd13th3backend.challenge.dto.response.ChallengeListResponse;
import org.minu.dnd13th3backend.challenge.entity.Challenge;
import org.minu.dnd13th3backend.challenge.entity.ChallengeParticipant;
import org.minu.dnd13th3backend.challenge.entity.InviteCode;
import org.minu.dnd13th3backend.challenge.repository.ChallengeParticipantRepository;
import org.minu.dnd13th3backend.challenge.repository.ChallengeRepository;
import org.minu.dnd13th3backend.challenge.repository.InviteCodeRepository;
import org.minu.dnd13th3backend.challenge.type.ChallengeStatus;
import org.minu.dnd13th3backend.common.exception.BusinessException;
import org.minu.dnd13th3backend.common.exception.ErrorCode;
import org.minu.dnd13th3backend.screentime.entity.ScreenTime;
import org.minu.dnd13th3backend.screentime.repository.ScreenTimeRepository;
import org.minu.dnd13th3backend.user.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipantRepository participantRepository;
    private final InviteCodeRepository inviteCodeRepository;
    private final ScreenTimeRepository screenTimeRepository;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    @Transactional
    public Challenge createChallenge(ChallengeCreateRequest request, User user) {
        Challenge challenge = Challenge.builder()
                .creator(user)
                .title(request.getTitle())
                .goalTimeMinutes(request.getGoalTimeMinutes())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(ChallengeStatus.IN_PROGRESS)
                .build();

        Challenge savedChallenge = challengeRepository.save(challenge);

        ChallengeParticipant participant = ChallengeParticipant.builder()
                .challenge(savedChallenge)
                .user(user)
                .build();

        participantRepository.save(participant);

        return savedChallenge;
    }

    @Transactional(readOnly = true)
    public ChallengeListResponse getChallenges(User user) {
        LocalDate today = LocalDate.now();
        List<ChallengeParticipant> userParticipations = participantRepository.findByUserAndChallenge_StartDateLessThanEqualAndChallenge_EndDateGreaterThanEqualOrderByChallenge_CreatedAtDesc(user, today, today);

        if (userParticipations.isEmpty()) {
            return ChallengeListResponse.builder().challenges(List.of()).build();
        }

        List<ChallengeGetResponse> challengeResponses = userParticipations.stream()
                .map(this::mapParticipationToChallengeGetResponse)
                .collect(Collectors.toList());

        return ChallengeListResponse.builder().challenges(challengeResponses).build();
    }

    @Transactional(readOnly = true)
    public ChallengeListResponse getChallengeHistory(User user) {
        LocalDate today = LocalDate.now();
        List<ChallengeParticipant> userParticipations = participantRepository.findByUserAndChallenge_EndDateBeforeOrderByChallenge_StartDateDesc(user, today);

        if (userParticipations.isEmpty()) {
            return ChallengeListResponse.builder().challenges(List.of()).build();
        }

        List<ChallengeGetResponse> challengeResponses = userParticipations.stream()
                .map(this::mapParticipationToChallengeGetResponse)
                .collect(Collectors.toList());

        return ChallengeListResponse.builder().challenges(challengeResponses).build();
    }

    private ChallengeGetResponse mapParticipationToChallengeGetResponse(ChallengeParticipant participation) {
        Challenge challenge = participation.getChallenge();
        List<ChallengeParticipant> allParticipants = participantRepository.findByChallenge_Id(challenge.getId());

        List<ChallengeGetResponse.ParticipantRecord> participantRecords = allParticipants.stream()
                .map(participant -> {
                    User participantUser = participant.getUser();

                    if (participantUser.getProfile() == null) {
                        throw new BusinessException(ErrorCode.PARTICIPANT_PROFILE_NOT_FOUND);
                    }

                    List<ScreenTime> screenTimes = screenTimeRepository.findByUser_IdAndDateBetween(
                            participantUser.getId(), challenge.getStartDate(), challenge.getEndDate()
                    );

                    long totalInsta = screenTimes.stream().mapToLong(ScreenTime::getInstagramMinutes).sum();
                    long totalYoutube = screenTimes.stream().mapToLong(ScreenTime::getYoutubeMinutes).sum();
                    long totalKakaotalk = screenTimes.stream().mapToLong(ScreenTime::getKakaotalkMinutes).sum();
                    long totalChrome = screenTimes.stream().mapToLong(ScreenTime::getChromeMinutes).sum();
                    long currentTimeMinutes = totalInsta + totalYoutube + totalKakaotalk + totalChrome;

                    double achievementRate;
                    if (challenge.getGoalTimeMinutes() > 0) {
                        double usageRate = ((double) currentTimeMinutes / challenge.getGoalTimeMinutes()) * 100.0;
                        achievementRate = Math.max(0, 100.0 - usageRate);
                    } else {
                        achievementRate = (currentTimeMinutes == 0) ? 100.0 : 0.0;
                    }

                    String status;
                    if (LocalDate.now().isAfter(challenge.getEndDate())) {
                        status = (currentTimeMinutes <= challenge.getGoalTimeMinutes()) ? "달성" : "실패";
                    } else {
                        status = "진행 중";
                    }

                    return ChallengeGetResponse.ParticipantRecord.builder()
                            .userId(participantUser.getId())
                            .nickname(participantUser.getProfile().getNickname())
                            .characterIndex(participantUser.getProfile().getCharacterIndex())
                            .currentTimeMinutes(currentTimeMinutes)
                            .instagramMinutes(totalInsta)
                            .youtubeMinutes(totalYoutube)
                            .kakaotalkMinutes(totalKakaotalk)
                            .chromeMinutes(totalChrome)
                            .achievementRate(achievementRate)
                            .status(status)
                            .build();
                })
                .collect(Collectors.toList());

        return ChallengeGetResponse.builder()
                .challengeId(challenge.getId())
                .startDate(challenge.getStartDate())
                .endDate(challenge.getEndDate())
                .title(challenge.getTitle())
                .goalTimeMinutes(challenge.getGoalTimeMinutes())
                .participants(participantRecords)
                .build();
    }

    @Transactional
    public String generateInviteLink(Long challengeId, User user) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHALLENGE_NOT_FOUND));

        if (!challenge.getCreator().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        InviteCode inviteCode = inviteCodeRepository.findByChallenge(challenge)
                .orElseGet(() -> {
                    InviteCode newCode = InviteCode.builder()
                            .challenge(challenge)
                            .code(UUID.randomUUID().toString().substring(0, 8))
                            .expiresAt(challenge.getEndDate())
                            .build();
                    return inviteCodeRepository.save(newCode);
                });

        return frontendBaseUrl + "/join?code=" + inviteCode.getCode();
    }

    @Transactional
    public Challenge joinChallenge(InviteJoinRequest request, User user) {
        InviteCode inviteCode = inviteCodeRepository.findByCode(request.getInviteCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVITE_CODE_NOT_FOUND));

        if (LocalDate.now().isAfter(inviteCode.getExpiresAt())) {
            throw new BusinessException(ErrorCode.INVITE_CODE_EXPIRED);
        }

        if (user.getProfile() == null) {
            throw new BusinessException(ErrorCode.PROFILE_NOT_FOUND);
        }

        Challenge challenge = inviteCode.getChallenge();

        if (challenge.getCreator().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.CANNOT_JOIN_OWN_CHALLENGE);
        }

        if (participantRepository.existsByChallengeAndUser(challenge, user)) {
            throw new BusinessException(ErrorCode.CHALLENGE_ALREADY_JOINED);
        }

        long currentParticipants = participantRepository.countByChallenge(challenge);
        if (currentParticipants >= 6) {
            throw new BusinessException(ErrorCode.CHALLENGE_FULL);
        }

        ChallengeParticipant participant = ChallengeParticipant.builder()
                .challenge(challenge)
                .user(user)
                .build();
        participantRepository.save(participant);

        return challenge;
    }
}
