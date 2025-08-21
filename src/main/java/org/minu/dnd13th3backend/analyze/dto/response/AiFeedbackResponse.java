package org.minu.dnd13th3backend.analyze.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class AiFeedbackResponse {

    private String period;
    private LocalDate startDate;
    private LocalDate endDate;
    private String type;
    private List<String> feedback;

    public static AiFeedbackResponse from(String period, LocalDate startDate, LocalDate endDate, 
                                         String type, List<String> feedback) {
        return AiFeedbackResponse.builder()
                .period(period)
                .startDate(startDate)
                .endDate(endDate)
                .type(type)
                .feedback(feedback)
                .build();
    }
}