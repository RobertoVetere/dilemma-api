package com.robedev.dilemma_api.dilemma.infraestructure.controller.dto;

import com.robedev.dilemma_api.dilemma.domain.model.Advice;
import lombok.Getter;

import java.util.List;

/**
 * DTO representing the structured response sent back to the user after reasoning.
 */
@Getter
public class AdviceResponse {

    private final String summary;
    private final List<String> options;
    private final String recommendation;
    private final String tone;

    public AdviceResponse(Advice advice) {
        this.summary = advice.getSummary();
        this.options = advice.getOptions();
        this.recommendation = advice.getRecommendation();
        this.tone = advice.getTone();
    }
}
