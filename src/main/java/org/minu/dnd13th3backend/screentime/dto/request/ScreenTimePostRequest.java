package org.minu.dnd13th3backend.screentime.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScreenTimePostRequest {
    private LocalDate date;
    private Integer screentimeMinutes;
}
