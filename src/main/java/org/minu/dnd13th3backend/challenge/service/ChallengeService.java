package org.minu.dnd13th3backend.challenge.service;

import lombok.RequiredArgsConstructor;
import org.minu.dnd13th3backend.challenge.dto.request.ChallengeCreateRequest;
import org.minu.dnd13th3backend.challenge.dto.response.ChallengeGetResponse;
import org.minu.dnd13th3backend.challenge.entity.Challenge;
import org.minu.dnd13th3backend.challenge.entity.ChallengeParticipant;
import org.minu.dnd13th3backend.challenge.repository.ChallengeParticipantRepository;
import org.minu.dnd13th3backend.challenge.repository.ChallengeRepository;
import org.minu.dnd13th3backend.challenge.type.ChallengeStatus;
import org.minu.dnd13th3backend.challenge.type.ChallengeType;
import org.minu.dnd13th3backend.common.exception.BusinessException;
import org.minu.dnd13th3backend.common.exception.ErrorCode;
import org.minu.dnd13th3backend.screentime.entity.ScreenTime;
import org.minu.dnd13th3backend.screentime.repository.ScreenTimeRepository;
import org.minu.dnd13th3backend.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipantRepository participantRepository;
    private final ScreenTimeRepository screenTimeRepository;

    @Transactional
    public Challenge createChallenge(ChallengeCreateRequest request, User user) {
        Challenge challenge = Challenge.builder()
                .creator(user)
                .title(request.getTitle())
                .type(request.getType())
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
    public ChallengeGetResponse getChallenge(ChallengeType type, LocalDate startDate, LocalDate endDate, User user) {

        List<ChallengeParticipant> userParticipation;
        boolean isCompletedChallenge = (startDate != null && endDate != null);

        if (isCompletedChallenge) {
            userParticipation = participantRepository.findByUserAndChallenge_TypeAndChallenge_StartDateAndChallenge_EndDate(user, type, startDate, endDate);
        } else {
            LocalDate today = LocalDate.now();
            userParticipation = participantRepository.findByUserAndChallenge_TypeAndChallenge_StartDateLessThanEqualAndChallenge_EndDateGreaterThanEqual(user, type, today, today);
        }

        if (userParticipation.isEmpty()) {
            throw new BusinessException(ErrorCode.CHALLENGE_NOT_FOUND);
        }
        Challenge challenge = userParticipation.get(0).getChallenge();

        List<ChallengeParticipant> allParticipants = participantRepository.findByChallenge_Id(challenge.getId());

        List<ChallengeGetResponse.ParticipantRecord> participantRecords = allParticipants.stream()
                .map(participant -> {
                    User participantUser = participant.getUser();

                    List<ScreenTime> screenTimes = screenTimeRepository.findByUser_IdAndDateBetween(
                            participantUser.getId(), challenge.getStartDate(), challenge.getEndDate()
                    );

                    long currentTimeMinutes = screenTimes.stream()
                            .mapToLong(ScreenTime::getScreentimeMinutes)
                            .sum();

                    double achievementRate;
                    if (challenge.getGoalTimeMinutes() > 0) {
                        double usageRate = ((double) currentTimeMinutes / challenge.getGoalTimeMinutes()) * 100.0;

                        achievementRate = Math.max(0, 100.0 - usageRate);
                    } else {
                        achievementRate = (currentTimeMinutes == 0) ? 100.0 : 0.0;
                    }

                    String status;
                    if (isCompletedChallenge) {
                        status = (currentTimeMinutes <= challenge.getGoalTimeMinutes()) ? "달성" : "실패";
                    } else {
                        status = "진행 중";
                    }

                    return ChallengeGetResponse.ParticipantRecord.builder()
                            .userId(participantUser.getId())
                            .nickname(participantUser.getProfile().getNickname())
                            .currentTimeMinutes(currentTimeMinutes)
                            .achievementRate(achievementRate)
                            .status(status)
                            .build();
                })
                .collect(Collectors.toList());

        return ChallengeGetResponse.builder()
                .challengeId(challenge.getId())
                .type(challenge.getType())
                .startDate(challenge.getStartDate())
                .endDate(challenge.getEndDate())
                .title(challenge.getTitle())
                .goalTimeMinutes(challenge.getGoalTimeMinutes())
                .participants(participantRecords)
                .build();
    }
}
