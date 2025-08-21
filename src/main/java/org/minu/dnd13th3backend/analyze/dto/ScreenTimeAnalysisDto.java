package org.minu.dnd13th3backend.analyze.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.minu.dnd13th3backend.screentime.entity.ScreenTime;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ScreenTimeAnalysisDto {

    private int totalMinutes;
    private double avgMinutes;
    private int maxMinutes;
    private int recordDays;

    public static ScreenTimeAnalysisDto from(List<ScreenTime> screenTimes) {
        if (screenTimes.isEmpty()) {
            return ScreenTimeAnalysisDto.builder()
                    .totalMinutes(0)
                    .avgMinutes(0.0)
                    .maxMinutes(0)
                    .recordDays(0)
                    .build();
        }

        int totalMinutes = screenTimes.stream()
                .mapToInt(ScreenTime::getScreentimeMinutes)
                .sum();

        double avgMinutes = (double) totalMinutes / 7;
        
        int maxMinutes = screenTimes.stream()
                .mapToInt(ScreenTime::getScreentimeMinutes)
                .max()
                .orElse(0);

        return ScreenTimeAnalysisDto.builder()
                .totalMinutes(totalMinutes)
                .avgMinutes(avgMinutes)
                .maxMinutes(maxMinutes)
                .recordDays(screenTimes.size())
                .build();
    }

    public String toAnalysisString() {
        if (totalMinutes == 0) {
            return "이번 주 스크린타임 기록이 없습니다.";
        }

        StringBuilder analysis = new StringBuilder();
        analysis.append("스크린타임 주간 분석:\n");
        analysis.append("- 총 스크린타임: ").append(totalMinutes).append("분 (")
                .append(totalMinutes/60).append("시간 ").append(totalMinutes%60).append("분)\n");
        analysis.append("- 하루 평균: ").append(String.format("%.1f", avgMinutes)).append("분\n");
        analysis.append("- 최대 사용일: ").append(maxMinutes).append("분\n");
        analysis.append("- 기록 일수: ").append(recordDays).append("일\n");

        return analysis.toString();
    }
}