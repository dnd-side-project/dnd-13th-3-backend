package org.minu.dnd13th3backend.screentime.dto.response;

import lombok.Getter;
import org.minu.dnd13th3backend.screentime.entity.ScreenTime;

import java.time.LocalDate;

@Getter
public class ScreenTimeGenerateResponse {

    private final Long id;
    private final LocalDate date;
    private final Integer screentimeMinutes;

    public ScreenTimeGenerateResponse(ScreenTime screenTime) {
        this.id = screenTime.getId();
        this.date = screenTime.getDate();
        this.screentimeMinutes = screenTime.getScreentimeMinutes();
    }
}
