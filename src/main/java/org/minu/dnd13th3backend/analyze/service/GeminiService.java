package org.minu.dnd13th3backend.analyze.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.minu.dnd13th3backend.analyze.config.GeminiConfig;
import org.minu.dnd13th3backend.analyze.dto.gemini.GeminiRequest;
import org.minu.dnd13th3backend.analyze.dto.gemini.GeminiResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    private final GeminiConfig geminiConfig;
    private final RestTemplate restTemplate;

    public String generateFeedback(String prompt) {
        try {
            GeminiRequest request = createGeminiRequest(prompt);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            
            String url = geminiConfig.getGeminiApiUrl() + "?key=" + geminiConfig.getGeminiApiKey();
            HttpEntity<GeminiRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<GeminiResponse> response = restTemplate.exchange(
                url, 
                HttpMethod.POST, 
                entity, 
                GeminiResponse.class
            );
            
            return extractTextFromResponse(response.getBody());
            
        } catch (Exception e) {
            log.error("Gemini API 호출 중 오류 발생", e);
            return "AI 피드백 생성 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        }
    }

    private GeminiRequest createGeminiRequest(String prompt) {
        return GeminiRequest.builder()
                .contents(List.of(
                    GeminiRequest.Content.builder()
                            .parts(List.of(
                                GeminiRequest.Part.builder()
                                        .text(prompt)
                                        .build()
                            ))
                            .build()
                ))
                .build();
    }

    private String extractTextFromResponse(GeminiResponse response) {
        if (response == null || 
            response.getCandidates() == null || 
            response.getCandidates().isEmpty() ||
            response.getCandidates().get(0).getContent() == null ||
            response.getCandidates().get(0).getContent().getParts() == null ||
            response.getCandidates().get(0).getContent().getParts().isEmpty()) {
            return "AI 피드백을 생성할 수 없습니다.";
        }
        
        return response.getCandidates().get(0).getContent().getParts().get(0).getText();
    }
}