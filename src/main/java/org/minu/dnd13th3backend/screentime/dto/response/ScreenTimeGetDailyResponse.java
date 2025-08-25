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

    public static ScreenTimeGetDailyResponse from(ScreenTime screenTime, String status) {
        if (screenTime == null) {
            return ScreenTimeGetDailyResponse.builder()
                    .screenTimes(List.of(DailyScreenTime.empty(LocalDate.now(), "NO_DATA")))
                    .build();
        }
        return ScreenTimeGetDailyResponse.builder()
                .screenTimes(List.of(new DailyScreenTime(screenTime, status)))
                .build();
    }

    @Getter
    public static class DailyScreenTime {
        private LocalDate date;
        private String dayOfWeek;
        private int totalMinutes;
        private String status;
        private AppTimeDetails appTimes;

        public DailyScreenTime(ScreenTime screenTime, String status) {
            this.date = screenTime.getDate();
            this.dayOfWeek = screenTime.getDate().getDayOfWeek().name(); // MONDAY, TUESDAY ...
            this.totalMinutes = screenTime.getInstagramMinutes() + screenTime.getYoutubeMinutes() +
                    screenTime.getKakaotalkMinutes() + screenTime.getChromeMinutes();
            this.status = status;
            this.appTimes = new AppTimeDetails(screenTime);
        }

        public static DailyScreenTime empty(LocalDate date, String status) {
            return new DailyScreenTime(date, status);
        }

        private DailyScreenTime(LocalDate date, String status) {
            this.date = date;
            this.dayOfWeek = date.getDayOfWeek().name();
            this.totalMinutes = 0;
            this.status = status;
            this.appTimes = new AppTimeDetails(null);
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