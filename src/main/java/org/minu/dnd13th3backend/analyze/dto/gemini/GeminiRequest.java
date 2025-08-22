package org.minu.dnd13th3backend.analyze.dto.gemini;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class GeminiRequest {

    private List<Content> contents;

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