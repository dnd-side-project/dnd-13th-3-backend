package org.minu.dnd13th3backend.screentime.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.minu.dnd13th3backend.screentime.entity.ScreenTime;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ScreenTimeGetDailyResponse {

    private List<ScreenTimeData> screenTimes;

    public static ScreenTimeGetDailyResponse from(ScreenTime screenTime) {
        if (screenTime == null) {
            return ScreenTimeGetDailyResponse.builder()
                    .screenTimes(List.of())
                    .build();
        }
        List<ScreenTimeData> data = List.of(new ScreenTimeData(screenTime));
        return ScreenTimeGetDailyResponse.builder()
                .screenTimes(data)
                .build();
    }

    @Getter
    @AllArgsConstructor
    public static class ScreenTimeData {
        private LocalDate date;
        private int screentimeMinutes;

        public ScreenTimeData(ScreenTime screenTime) {
            this.date = screenTime.getDate();
            this.screentimeMinutes = screenTime.getScreentimeMinutes();
        }
    }
}
