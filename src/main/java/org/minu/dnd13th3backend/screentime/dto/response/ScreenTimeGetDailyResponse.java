package org.minu.dnd13th3backend.screentime.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.minu.dnd13th3backend.screentime.entity.ScreenTime;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class ScreenTimeGetDailyResponse {
    private List<DailyScreenTime> screenTimes;

    public static ScreenTimeGetDailyResponse from(ScreenTime screenTime) {
        if (screenTime == null) {
            return ScreenTimeGetDailyResponse.builder().screenTimes(List.of()).build();
        }
        return ScreenTimeGetDailyResponse.builder()
                .screenTimes(List.of(new DailyScreenTime(screenTime)))
                .build();
    }

    @Getter
    public static class DailyScreenTime {
        private LocalDate date;
        private int totalMinutes;
        private AppTimeDetails appTimes;

        public DailyScreenTime(ScreenTime screenTime) {
            this.date = screenTime.getDate();
            this.totalMinutes = screenTime.getInstagramMinutes() + screenTime.getYoutubeMinutes() +
                    screenTime.getKakaotalkMinutes() + screenTime.getChromeMinutes();
            this.appTimes = new AppTimeDetails(screenTime);
        }
    }

    @Getter
    public static class AppTimeDetails {
        private int instagram;
        private int youtube;
        private int kakaotalk;
        private int chrome;

        public AppTimeDetails(ScreenTime screenTime) {
            this.instagram = screenTime.getInstagramMinutes();
            this.youtube = screenTime.getYoutubeMinutes();
            this.kakaotalk = screenTime.getKakaotalkMinutes();
            this.chrome = screenTime.getChromeMinutes();
        }
    }
}