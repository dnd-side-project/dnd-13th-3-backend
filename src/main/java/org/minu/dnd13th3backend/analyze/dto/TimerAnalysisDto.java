package org.minu.dnd13th3backend.analyze.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.minu.dnd13th3backend.timer.entity.Timer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class TimerAnalysisDto {

    private int totalMinutes;
    private int sessionCount;
    private Map<String, Integer> categoryTotals;

    public static TimerAnalysisDto from(List<Timer> timers) {
        if (timers.isEmpty()) {
            return TimerAnalysisDto.builder()
                    .totalMinutes(0)
                    .sessionCount(0)
                    .categoryTotals(new HashMap<>())
                    .build();
        }

        Map<String, Integer> categoryTotals = new HashMap<>();
        int totalMinutes = 0;

        for (Timer timer : timers) {
            int timerMinutes = timer.getDurationHours() * 60 + timer.getDurationMinutes();
            totalMinutes += timerMinutes;
            
            categoryTotals.merge(timer.getCategory(), timerMinutes, Integer::sum);
        }

        return TimerAnalysisDto.builder()
                .totalMinutes(totalMinutes)
                .sessionCount(timers.size())
                .categoryTotals(categoryTotals)
                .build();
    }

    public String toAnalysisString() {
        if (totalMinutes == 0) {
            return "이번 주 타이머 사용 기록이 없습니다.";
        }

        StringBuilder analysis = new StringBuilder();
        analysis.append("타이머 활동 주간 분석:\n");
        analysis.append("- 총 활동시간: ").append(totalMinutes).append("분 (")
                .append(totalMinutes/60).append("시간 ").append(totalMinutes%60).append("분)\n");
        analysis.append("- 총 세션 수: ").append(sessionCount).append("회\n");
        analysis.append("- 카테고리별 시간:\n");

        categoryTotals.forEach((category, minutes) -> {
            analysis.append("  * ").append(category).append(": ").append(minutes).append("분\n");
        });

        return analysis.toString();
    }
}