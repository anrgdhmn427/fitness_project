package com.fitness.aiservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@Slf4j
public class GeminiService {


    private final WebClient webClient;
    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Autowired
    public GeminiService(WebClient webClient) {
        this.webClient = webClient;
    }


    public String getAnswer(String question) {
        Map<String, Object> requestBdy = Map.of("contents", new Object[]{
                Map.of("parts", new Object[]{
                        Map.of("text", question)
                })
        });

        String response = webClient.post()
                .uri(geminiApiUrl +"?key="+ geminiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBdy)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return response;


    }


}
