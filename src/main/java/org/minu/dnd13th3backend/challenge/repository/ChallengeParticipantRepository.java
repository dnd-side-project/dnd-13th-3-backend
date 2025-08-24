package org.minu.dnd13th3backend.challenge.repository;

import org.minu.dnd13th3backend.challenge.entity.Challenge;
import org.minu.dnd13th3backend.challenge.entity.ChallengeParticipant;
import org.minu.dnd13th3backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeParticipantRepository extends JpaRepository<ChallengeParticipant, Long> {

    List<ChallengeParticipant> findByUserAndChallenge_StartDateLessThanEqualAndChallenge_EndDateGreaterThanEqualOrderByChallenge_CreatedAtDesc(
            User user, LocalDate today1, LocalDate today2
    );

    List<ChallengeParticipant> findByUserAndChallenge_StartDateAndChallenge_EndDateOrderByChallenge_CreatedAtDesc(
            User user, LocalDate startDate, LocalDate endDate
    );

    List<ChallengeParticipant> findByChallenge_Id(Long challengeId);

    boolean existsByChallengeAndUser(Challenge challenge, User user);

    long countByChallenge(Challenge challenge);
}