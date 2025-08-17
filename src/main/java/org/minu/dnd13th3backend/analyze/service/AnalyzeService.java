package org.minu.dnd13th3backend.analyze.service;

import lombok.RequiredArgsConstructor;
import org.minu.dnd13th3backend.analyze.dto.ScreenTimeAnalysisDto;
import org.minu.dnd13th3backend.analyze.dto.TimerAnalysisDto;
import org.minu.dnd13th3backend.analyze.dto.response.AiFeedbackResponse;
import org.minu.dnd13th3backend.analyze.entity.AiFeedback;
import org.minu.dnd13th3backend.analyze.repository.AiFeedbackRepository;
import org.minu.dnd13th3backend.analyze.type.FeedbackType;
import org.minu.dnd13th3backend.common.exception.BusinessException;
import org.minu.dnd13th3backend.common.exception.ErrorCode;
import org.minu.dnd13th3backend.screentime.entity.ScreenTime;
import org.minu.dnd13th3backend.screentime.repository.ScreenTimeRepository;
import org.minu.dnd13th3backend.timer.entity.Timer;
import org.minu.dnd13th3backend.timer.repository.TimerRepository;
import org.minu.dnd13th3backend.user.entity.User;
import org.minu.dnd13th3backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AnalyzeService {

    private final UserRepository userRepository;
    private final ScreenTimeRepository screenTimeRepository;
    private final TimerRepository timerRepository;
    private final AiFeedbackRepository aiFeedbackRepository;
    private final GeminiService geminiService;

    public AiFeedbackResponse generateAiFeedback(Long userId, String period, String type) {
        validateParameters(period, type);
        validateUser(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        LocalDate today = LocalDate.now();
        FeedbackType feedbackType = "day".equals(period) ? FeedbackType.DAILY : FeedbackType.WEEKLY;
        LocalDate referenceDate = calculateReferenceDate(today, feedbackType);

        // 기존 유효한 피드백이 있는지 확인
        AiFeedback existingFeedback = aiFeedbackRepository.findValidFeedback(
                userId, feedbackType, referenceDate, LocalDateTime.now())
                .orElse(null);

        if (existingFeedback != null) {
            // 기존 피드백 반환
            List<String> feedbackList = parseFeedbackContent(existingFeedback.getContent());
            LocalDate[] dateRange = calculateDateRange(referenceDate, feedbackType);
            return AiFeedbackResponse.from(period, dateRange[0], dateRange[1], type, feedbackList);
        }

        // 새로운 피드백 생성
        LocalDate[] dateRange = calculateDateRange(referenceDate, feedbackType);
        List<String> feedback = generateNewAiFeedback(user, dateRange[0], dateRange[1], type);
        
        // 피드백 저장
        saveFeedback(user, feedbackType, referenceDate, feedback);

        return AiFeedbackResponse.from(period, dateRange[0], dateRange[1], type, feedback);
    }

    private void validateParameters(String period, String type) {
        if (!List.of("day", "week").contains(period)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (!List.of("screentime", "timer").contains(type)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private void validateUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
    }

    private LocalDate calculateReferenceDate(LocalDate today, FeedbackType feedbackType) {
        if (feedbackType == FeedbackType.DAILY) {
            return today;
        } else {
            // 주간의 경우 해당 주의 월요일을 기준일로 사용
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            return today.with(weekFields.dayOfWeek(), 1);
        }
    }

    private LocalDate[] calculateDateRange(LocalDate referenceDate, FeedbackType feedbackType) {
        if (feedbackType == FeedbackType.DAILY) {
            return new LocalDate[]{referenceDate, referenceDate};
        } else {
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            LocalDate startOfWeek = referenceDate.with(weekFields.dayOfWeek(), 1);
            LocalDate endOfWeek = referenceDate.with(weekFields.dayOfWeek(), 7);
            return new LocalDate[]{startOfWeek, endOfWeek};
        }
    }

    private void saveFeedback(User user, FeedbackType feedbackType, LocalDate referenceDate, List<String> feedback) {
        String content = String.join("\n", feedback);
        LocalDateTime expiresAt = calculateExpiresAt(feedbackType);

        AiFeedback aiFeedback = AiFeedback.builder()
                .user(user)
                .type(feedbackType)
                .content(content)
                .referenceDate(referenceDate)
                .expiresAt(expiresAt)
                .build();

        aiFeedbackRepository.save(aiFeedback);
    }

    private LocalDateTime calculateExpiresAt(FeedbackType feedbackType) {
        LocalDateTime now = LocalDateTime.now();
        if (feedbackType == FeedbackType.DAILY) {
            // 일간 피드백은 다음날 오전 6시까지 유효
            return now.plusDays(1).withHour(6).withMinute(0).withSecond(0).withNano(0);
        } else {
            // 주간 피드백은 다음주 월요일 오전 6시까지 유효
            return now.plusWeeks(1).withHour(6).withMinute(0).withSecond(0).withNano(0);
        }
    }

    private List<String> parseFeedbackContent(String content) {
        return List.of(content.split("\n"));
    }

    private List<String> generateNewAiFeedback(User user, LocalDate startDate, LocalDate endDate, String type) {
        String analysisData = "";
        
        switch (type) {
            case "screentime" -> {
                List<ScreenTime> screenTimes = screenTimeRepository.findByUser_IdAndDateBetween(
                        user.getId(), startDate, endDate);
                ScreenTimeAnalysisDto analysis = ScreenTimeAnalysisDto.from(screenTimes);
                analysisData = analysis.toAnalysisString();
            }
            case "timer" -> {
                LocalDateTime startDateTime = startDate.atStartOfDay();
                LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
                List<Timer> timers = timerRepository.findByUser_IdAndStartedAtBetween(
                        user.getId(), startDateTime, endDateTime);
                TimerAnalysisDto analysis = TimerAnalysisDto.from(timers);
                analysisData = analysis.toAnalysisString();
            }
        }
        
        String prompt = createFeedbackPrompt(type, analysisData, user.getName());
        String aiResponse = geminiService.generateFeedback(prompt);
        
        return parseAiFeedback(aiResponse);
    }
    
    
    private String createFeedbackPrompt(String type, String analysisData, String userName) {
        String basePrompt = "다음은 " + userName + "님의 " + 
                (type.equals("screentime") ? "스크린타임" : "타이머 활동") + " 데이터입니다.\n\n" +
                analysisData + "\n\n" +
                "위 데이터를 바탕으로 다음 조건에 맞는 피드백을 생성해주세요:\n" +
                "1. 친근하고 격려하는 톤으로 작성\n" +
                "2. 구체적인 수치를 언급하며 분석\n" +
                "3. 개선점이나 칭찬할 점을 포함\n" +
                "4. 3-5개의 짧은 문장으로 구성\n" +
                "5. 각 문장은 줄바꿈(\\n)으로 구분\n\n" +
                "예시 형식:\n" +
                "이번 주 총 240분 사용하셨네요!\n" +
                "하루 평균 34분으로 적절한 수준이에요.\n" +
                "주말에 조금 늘어났지만 괜찮아요!";
        
        return basePrompt;
    }
    
    private List<String> parseAiFeedback(String aiResponse) {
        if (aiResponse == null || aiResponse.trim().isEmpty()) {
            return List.of("AI 피드백을 생성할 수 없습니다.");
        }
        
        String[] lines = aiResponse.split("\\n");
        List<String> feedback = new ArrayList<>();
        
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty() && !trimmed.startsWith("예시") && !trimmed.startsWith("-")) {
                feedback.add(trimmed);
            }
        }
        
        return feedback.isEmpty() ? List.of("AI 피드백을 생성할 수 없습니다.") : feedback;
    }
}