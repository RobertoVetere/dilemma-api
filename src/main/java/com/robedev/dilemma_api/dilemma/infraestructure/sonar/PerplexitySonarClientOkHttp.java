package com.robedev.dilemma_api.dilemma.infraestructure.sonar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robedev.dilemma_api.dilemma.application.port.out.SonarGateway;
import com.robedev.dilemma_api.dilemma.domain.model.Advice;
import com.robedev.dilemma_api.dilemma.domain.model.Dilemma;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component("sonarOkHttp")
public class PerplexitySonarClientOkHttp implements SonarGateway {

    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final String apiKey;
    private final String apiUrl;
    private static final MediaType JSON = MediaType.parse("application/json");
    private static final String MODEL = "llama-3.1-sonar-small-128k-online";

    public PerplexitySonarClientOkHttp(
            @Value("${PERPLEXITY_API_KEY}") String apiKey,
            @Value("${PERPLEXITY_BASE_URL}") String baseUrl
    ) {
        this.apiKey = apiKey;
        this.apiUrl = baseUrl + "/chat/completions";
        this.client = new OkHttpClient();
        this.mapper = new ObjectMapper()
                .configure(com.fasterxml.jackson.databind.DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true);
        ;
    }

    @Override
    public DilemmaAnalysis analyze(Dilemma dilemma) {
        String prompt = analysisPrompt(dilemma.getDescription());
        log.debug("Analysis prompt → {}", prompt);
        String raw = callPerplexity(prompt);
        return parseAnalysis(raw);
    }

    @Override
    public Advice resolve(Dilemma dilemma, DilemmaAnalysis analysis) {
        String prompt = resolutionPrompt(dilemma, analysis);
        log.debug("Resolution prompt → {}", prompt);
        String raw = callPerplexity(prompt);
        return parseAdvice(raw);
    }

    private String callPerplexity(String prompt) {
        try {
            // Cuerpo JSON construido con DTO (más seguro que .formatted)
            String bodyJson = mapper.writeValueAsString(new RequestBodyDTO(prompt));

            log.info("🚀 JSON enviado a Perplexity:\n{}", bodyJson);

            RequestBody body = RequestBody.create(bodyJson, JSON);

            Request request = new Request.Builder()
                    .url(apiUrl)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "No body";
                    log.error("❌ Perplexity returned {} → {}", response.code(), errorBody);
                    throw new IllegalStateException("Perplexity API error: " + response.code());
                }

                String responseBody = response.body().string();
                log.debug("✅ Raw response ← {}", responseBody);

                JsonNode root = mapper.readTree(responseBody);
                String content = root.path("choices").get(0).path("message").path("content").asText();

                // Sanear contenido no JSON (ticks, bloques markdown, etc.)
                content = content
                        .replaceAll("(?m)^```(?:json)?\\s*", "")
                        .replaceAll("(?m)^```\\s*", "")
                        .replaceAll("(?m)^`+\\s*", "")
                        .replaceAll("(?m)`+$", "")
                        .trim();

                return content;
            }
        } catch (IOException e) {
            log.error("❌ Error calling Perplexity", e);
            throw new RuntimeException(e);
        }
    }


    private DilemmaAnalysis parseAnalysis(String json) {
        try {
            return mapper.readValue(json, DilemmaAnalysis.class);
        } catch (Exception e) {
            throw new IllegalStateException("Bad analysis JSON", e);
        }
    }

    private Advice parseAdvice(String json) {
        try {
            AdviceDTO dto = mapper.readValue(json, AdviceDTO.class);
            return new Advice(dto.summary, dto.options, dto.recommendation, dto.tone );
        } catch (Exception e) {
            throw new IllegalStateException("Bad advice JSON", e);
        }
    }




    private String analysisPrompt(String description) {
        return """
               Return ONLY JSON like:
               {"category":"","subcategory":"","depth":"","urgency":"",
               "conflicts":[],"decisionType":"","relevantFactors":[],"recommendedTone":""}
               Dilemma: "%s"
               """.formatted(description);
    }

    private String resolutionPrompt(Dilemma dilemma, DilemmaAnalysis a) {
        return """
    You are a deeply thoughtful advisor with expertise in emotional intelligence, psychology, decision-making, and human dilemmas. 

    Based on the following structured analysis of the situation:
    %s

    Provide nuanced, human-like advice in the following minified JSON format:
    {"summary":"","options":[],"recommendation":"","tone":""}

    Instructions:
    - In 'summary', synthesize the emotional, practical and existential aspects of the dilemma.
    - In 'options', include 3–4 realistic, contrasting actions, each phrased with empathy and clarity.
    - In 'recommendation', propose a course of action with a justification rooted in long-term well-being and self-awareness.
    - In 'tone', choose a tone like 'reflective', 'cautiously optimistic', 'supportive', or similar.
    - In "roadmap": Provide a short, strategic roadmap with actionable next steps. Consider the historical context (e.g., economy, housing, job market) and the user's situation to propose a reasoned path forward over the next few months.

    Be specific, insightful, and go beyond surface-level logic.

    Dilemma: "%s"
    """.formatted(toJson(a), dilemma.getDescription());
    }


    private String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON serialization failed", e);
        }
    }

    // Clase auxiliar para componer el cuerpo del request
    @Data
    private static class RequestBodyDTO {
        private final String model = MODEL;
        private final List<Message> messages;
        private final boolean stream = false;
        private final double temperature = 0.3;

        public RequestBodyDTO(String prompt) {
            this.messages = List.of(
                    new Message("system", "Be precise and concise. Reply ONLY minified JSON."),
                    new Message("user", prompt)
            );
        }

        @Data
        private static class Message {
            private final String role;
            private final String content;
        }
    }

    @Data
    private static class AdviceDTO {
        String summary;
        List<String> options;
        String recommendation;
        String tone;
    }





}
