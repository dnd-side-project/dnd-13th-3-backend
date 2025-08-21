package org.minu.dnd13th3backend.analyze.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.minu.dnd13th3backend.analyze.repository.AiFeedbackRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiFeedbackCleanupScheduler {

    private final AiFeedbackRepository aiFeedbackRepository;

    @Scheduled(cron = "0 0 6 * * *") // 매일 오전 6시 실행
    public void cleanupExpiredFeedback() {
        try {
            LocalDateTime now = LocalDateTime.now();
            aiFeedbackRepository.deleteByExpiresAtBefore(now);
            log.info("만료된 AI 피드백 정리 완료: {}", now);
        } catch (Exception e) {
            log.error("AI 피드백 정리 중 오류 발생", e);
        }
    }
}