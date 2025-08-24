package org.minu.dnd13th3backend.challenge.repository;

import org.minu.dnd13th3backend.challenge.entity.Challenge;
import org.minu.dnd13th3backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    Optional<Challenge> findTopByCreatorOrderByCreatedAtDesc(User creator);
}

