package org.minu.dnd13th3backend.screentime.repository;

import org.minu.dnd13th3backend.screentime.entity.ScreenTime;
import org.minu.dnd13th3backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ScreenTimeRepository extends JpaRepository<ScreenTime, Long> {

    Optional<ScreenTime> findByUserAndDate(User user, LocalDate date);
}
