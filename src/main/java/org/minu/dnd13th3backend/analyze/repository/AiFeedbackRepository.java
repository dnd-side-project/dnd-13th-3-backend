package org.minu.dnd13th3backend.analyze.repository;

import org.minu.dnd13th3backend.analyze.entity.AiFeedback;
import org.minu.dnd13th3backend.analyze.type.FeedbackType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AiFeedbackRepository extends JpaRepository<AiFeedback, Long> {

    @Query("SELECT af FROM AiFeedback af WHERE af.user.id = :userId AND af.type = :type AND af.referenceDate = :referenceDate AND af.expiresAt > :now")
    Optional<AiFeedback> findValidFeedback(@Param("userId") Long userId, 
                                           @Param("type") FeedbackType type, 
                                           @Param("referenceDate") LocalDate referenceDate,
                                           @Param("now") LocalDateTime now);

    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}