package org.minu.dnd13th3backend.timer.repository;

import org.minu.dnd13th3backend.timer.entity.Timer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TimerRepository extends JpaRepository<Timer, Long> {
    List<Timer> findByUser_IdAndStartedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
}
