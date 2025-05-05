package com.robedev.dilemma_api.dilemma.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robedev.dilemma_api.dilemma.domain.model.Dilemma;
import com.robedev.dilemma_api.dilemma.domain.model.DilemmaAnalysis;

public class PromptBuilderService {

    private final ObjectMapper mapper;

    public PromptBuilderService() {
        this.mapper = new ObjectMapper();
    }

    public String buildAnalysisPrompt(String description) {
        return """
            Return ONLY JSON like:
            {"category":"","subcategory":"","depth":"","urgency":"",
            "conflicts":[],"decisionType":"","relevantFactors":[],"recommendedTone":""}
            Dilemma: "%s"
            """.formatted(description);
    }

    public String buildResolutionPrompt(Dilemma dilemma, DilemmaAnalysis analysis) {
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
            """.formatted(toJson(analysis), dilemma.getDescription());
    }

    private String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON serialization failed", e);
        }
    }
}
