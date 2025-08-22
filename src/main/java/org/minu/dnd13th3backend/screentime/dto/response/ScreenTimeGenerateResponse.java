package org.minu.dnd13th3backend.screentime.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.minu.dnd13th3backend.screentime.entity.ScreenTime;
import java.time.LocalDate;

@Getter
@Builder
public class ScreenTimeGenerateResponse {
    private Long id;
    private LocalDate date;
    private int totalMinutes;
    private AppTimeDetails appTimes;

    public static ScreenTimeGenerateResponse from(ScreenTime screenTime) {
        int total = screenTime.getInstagramMinutes() + screenTime.getYoutubeMinutes() +
                screenTime.getKakaotalkMinutes() + screenTime.getChromeMinutes();
        return ScreenTimeGenerateResponse.builder()
                .id(screenTime.getId())
                .date(screenTime.getDate())
                .totalMinutes(total)
                .appTimes(new AppTimeDetails(screenTime))
                .build();
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