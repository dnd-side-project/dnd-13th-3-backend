package org.minu.dnd13th3backend.challenge.repository;

import org.minu.dnd13th3backend.challenge.entity.Challenge;
import org.minu.dnd13th3backend.challenge.entity.ChallengeParticipant;
import org.minu.dnd13th3backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChallengeParticipantRepository extends JpaRepository<ChallengeParticipant, Long> {

    @Query("SELECT cp FROM ChallengeParticipant cp JOIN FETCH cp.user u JOIN FETCH u.profile " +
            "WHERE cp.user = :user " +
            "AND cp.challenge.startDate <= :today1 " +
            "AND cp.challenge.endDate >= :today2 " +
            "ORDER BY cp.challenge.createdAt DESC")
    List<ChallengeParticipant> findByUserAndChallenge_StartDateLessThanEqualAndChallenge_EndDateGreaterThanEqualOrderByChallenge_CreatedAtDesc(
            @Param("user") User user, @Param("today1") LocalDate today1, @Param("today2") LocalDate today2
    );

    @Query("SELECT cp FROM ChallengeParticipant cp JOIN FETCH cp.user u JOIN FETCH u.profile " +
            "WHERE cp.user = :user " +
            "AND cp.challenge.endDate < :today " +
            "ORDER BY cp.challenge.startDate DESC")
    List<ChallengeParticipant> findByUserAndChallenge_EndDateBeforeOrderByChallenge_StartDateDesc(@Param("user") User user, @Param("today") LocalDate today);

    @Query("SELECT cp FROM ChallengeParticipant cp JOIN FETCH cp.user u JOIN FETCH u.profile WHERE cp.challenge.id = :challengeId")
    List<ChallengeParticipant> findByChallenge_Id(@Param("challengeId") Long challengeId);

    boolean existsByChallengeAndUser(Challenge challenge, User user);

    long countByChallenge(Challenge challenge);
}
