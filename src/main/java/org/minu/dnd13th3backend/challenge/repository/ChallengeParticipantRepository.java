package org.minu.dnd13th3backend.challenge.repository;

import org.minu.dnd13th3backend.challenge.entity.ChallengeParticipant;
import org.minu.dnd13th3backend.challenge.type.ChallengeType;
import org.minu.dnd13th3backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChallengeParticipantRepository extends JpaRepository<ChallengeParticipant, Long> {

    List<ChallengeParticipant> findByUserAndChallenge_TypeAndChallenge_StartDateLessThanEqualAndChallenge_EndDateGreaterThanEqual(
            User user, ChallengeType type, LocalDate today1, LocalDate today2
    );

    List<ChallengeParticipant> findByUserAndChallenge_TypeAndChallenge_StartDateAndChallenge_EndDate(
            User user, ChallengeType type, LocalDate startDate, LocalDate endDate
    );

    List<ChallengeParticipant> findByChallenge_Id(Long challengeId);
}