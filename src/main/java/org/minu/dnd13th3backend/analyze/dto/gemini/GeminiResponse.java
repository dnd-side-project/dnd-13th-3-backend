package org.minu.dnd13th3backend.analyze.dto.gemini;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class GeminiResponse {

    private List<Candidate> candidates;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Candidate {
        private Content content;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Content {
        private List<Part> parts;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Part {
        private String text;
    }
}