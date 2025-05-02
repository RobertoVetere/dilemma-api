package com.robedev.dilemma_api.dilemma.infraestructure.sonar;

import com.robedev.dilemma_api.dilemma.application.port.out.SonarGateway;
import com.robedev.dilemma_api.dilemma.domain.model.Advice;
import com.robedev.dilemma_api.dilemma.domain.model.Dilemma;
import com.robedev.dilemma_api.dilemma.infraestructure.sonar.DilemmaAnalysis;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Infrastructure adapter that connects with Perplexity Sonar API
 * to perform semantic analysis and dilemma reasoning.
 */
@Component
public class PerplexitySonarClient implements SonarGateway {

    private final WebClient webClient;

    public PerplexitySonarClient(@Value("${PERPLEXITY_API_KEY}") String apiKey,
                                 @Value("${PERPLEXITY_URL}") String base_url) {
        this.webClient = WebClient.builder()
                .baseUrl(base_url)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public DilemmaAnalysis analyze(Dilemma dilemma) {
        String prompt = buildAnalysisPrompt(dilemma.getDescription());

        // Construir la solicitud
        Mono<String> responseMono = webClient.post()
                .uri("/chat/completions")
                .bodyValue(buildRequestBody(prompt))
                .retrieve()
                .bodyToMono(String.class);

        // Procesar la respuesta
        String response = responseMono.block();
        // TODO: Parsear la respuesta JSON y mapear a DilemmaAnalysis
        return parseAnalysisResponse(response);
    }


    @Override
    public Advice resolve(Dilemma dilemma, DilemmaAnalysis analysis) {
        String prompt = buildResolutionPrompt(dilemma, analysis);

        // Construir la solicitud
        Mono<String> responseMono = webClient.post()
                .uri("/chat/completions")
                .bodyValue(buildRequestBody(prompt))
                .retrieve()
                .bodyToMono(String.class);

        // Procesar la respuesta
        String response = responseMono.block();
        // TODO: Parsear la respuesta JSON y mapear a Advice
        return parseAdviceResponse(response);
    }

    private String buildAnalysisPrompt(String description) {
        // TODO: Implementar la lógica para construir el prompt de análisis
        return "Analyze the following dilemma: " + description;
    }

    private String buildResolutionPrompt(Dilemma dilemma, DilemmaAnalysis analysis) {
        // TODO: Implementar la lógica para construir el prompt de resolución
        return "Based on the analysis, provide advice for the dilemma: " + dilemma.getDescription();
    }

    private String buildRequestBody(String prompt) {
        // TODO: Construir el cuerpo de la solicitud según la estructura esperada por la API
        return "{ \"model\": \"sonar\", \"messages\": [ { \"role\": \"user\", \"content\": \"" + prompt + "\" } ] }";
    }

    private DilemmaAnalysis parseAnalysisResponse(String response) {
        // TODO: Implementar la lógica para parsear la respuesta y mapear a DilemmaAnalysis
        return null;
    }

    private Advice parseAdviceResponse(String response) {
        // TODO: Implementar la lógica para parsear la respuesta y mapear a Advice
        return null;
    }
}
