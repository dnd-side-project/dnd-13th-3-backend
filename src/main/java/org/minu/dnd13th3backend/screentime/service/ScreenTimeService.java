package org.minu.dnd13th3backend.screentime.service;

import lombok.RequiredArgsConstructor;
import org.minu.dnd13th3backend.screentime.dto.request.ScreenTimePostRequest; // 경로 수정
import org.minu.dnd13th3backend.screentime.entity.ScreenTime;
import org.minu.dnd13th3backend.screentime.repository.ScreenTimeRepository;
import org.minu.dnd13th3backend.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScreenTimeService {

    private final ScreenTimeRepository screenTimeRepository;

    @Transactional
    public ScreenTime registerOrUpdateScreenTime(ScreenTimePostRequest requestDto, User user) {
        Optional<ScreenTime> optionalScreenTime = screenTimeRepository.findByUserAndDate(user, requestDto.getDate());

        if (optionalScreenTime.isPresent()) {
            ScreenTime screenTime = optionalScreenTime.get();
            screenTime.updateScreenTime(requestDto.getScreentimeMinutes());
            return screenTime;
        } else {
            ScreenTime newScreenTime = ScreenTime.builder()
                    .user(user)
                    .date(requestDto.getDate())
                    .screentimeMinutes(requestDto.getScreentimeMinutes())
                    .build();
            return screenTimeRepository.save(newScreenTime);
        }
    }
}
