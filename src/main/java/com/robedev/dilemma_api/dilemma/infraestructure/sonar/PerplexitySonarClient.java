package com.robedev.dilemma_api.dilemma.infraestructure.sonar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robedev.dilemma_api.dilemma.application.port.out.SonarGateway;
import com.robedev.dilemma_api.dilemma.domain.model.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("sonarWebClient")
public class PerplexitySonarClient implements SonarGateway {

    private static final String MODEL = "sonar-small-online"; // Modelo público gratuito

    private final WebClient webClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public PerplexitySonarClient(
            @Value("${PERPLEXITY_API_KEY}") String apiKey,
            @Value("${PERPLEXITY_BASE_URL}") String baseUrl
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public DilemmaAnalysis analyze(Dilemma dilemma) {
        String prompt = analysisPrompt(dilemma.getDescription());
        log.debug("Analysis prompt → {}", prompt);
        return toAnalysis(callSonar(prompt));
    }

    @Override
    public Advice resolve(Dilemma dilemma, DilemmaAnalysis analysis) {
        String prompt = resolutionPrompt(dilemma, analysis);
        log.debug("Resolution prompt → {}", prompt);
        return toAdvice(callSonar(prompt));
    }

    private String callSonar(String prompt) {
        Map<String, Object> body = buildRequestBody(prompt);
        try {
            log.debug("JSON sent to Sonar:\n{}",
                    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(body));
        } catch (Exception ignore) {}

        try {
            return webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            log.error("Sonar returned {} → {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    private Map<String, Object> buildRequestBody(String prompt) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", MODEL);
        body.put("temperature", 0.3);
        body.put("messages", List.of(
                Map.of("role", "system",
                        "content", "Be precise and concise. Reply ONLY minified JSON when asked."),
                Map.of("role", "user", "content", prompt)
        ));
        return body;
    }

    private String analysisPrompt(String description) {
        return """
                Return ONLY JSON like:
                {"category":"","subcategory":"","depth":"","urgency":"",
                 "conflicts":[],"decisionType":"","relevantFactors":[],"recommendedTone":""}
                Dilemma: "%s"
                """.formatted(description.replace("\"", "\\\""));
    }

    private String resolutionPrompt(Dilemma dilemma, DilemmaAnalysis a) {
        return """
                Using this analysis JSON: %s
                craft advice ONLY as:
                {"summary":"","options":[],"recommendation":"","tone":""}
                Dilemma: "%s"
                """.formatted(toJson(a), dilemma.getDescription().replace("\"", "\\\""));
    }

    private DilemmaAnalysis toAnalysis(String raw) {
        String json = extractContent(raw);
        log.debug("Analysis JSON ← {}", json);
        try {
            return mapper.readValue(json, DilemmaAnalysis.class);
        } catch (Exception e) {
            throw new IllegalStateException("Bad analysis JSON", e);
        }
    }

    private Advice toAdvice(String raw) {
        String json = extractContent(raw);
        log.debug("Advice JSON ← {}", json);
        try {
            AdviceDTO dto = mapper.readValue(json, AdviceDTO.class);
            return new Advice(dto.summary, dto.options, dto.recommendation, dto.tone);
        } catch (Exception e) {
            throw new IllegalStateException("Bad advice JSON", e);
        }
    }

    private String extractContent(String apiResponse) {
        try {
            JsonNode root = mapper.readTree(apiResponse);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot extract content", e);
        }
    }

    private String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Data
    private static class AdviceDTO {
        String summary;
        List<String> options;
        String recommendation;
        String tone;
        String roadmap;
    }
}
