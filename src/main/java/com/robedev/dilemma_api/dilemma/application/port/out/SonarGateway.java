package com.robedev.dilemma_api.dilemma.application.port.out;

import com.robedev.dilemma_api.dilemma.domain.model.Advice;
import com.robedev.dilemma_api.dilemma.domain.model.Dilemma;
import com.robedev.dilemma_api.dilemma.domain.model.DilemmaAnalysis;

/**
 * Output port for interacting with the Perplexity Sonar AI service.
 * Provides methods for semantic analysis and reasoning.
 */
public interface SonarGateway {

    /**
     * Performs semantic analysis on the given dilemma.
     * @param dilemma The user-submitted dilemma.
     * @return A structured analysis of the dilemma.
     */
    DilemmaAnalysis analyze(Dilemma dilemma);

    /**
     * Resolves the dilemma based on its analysis.
     * @param dilemma The original dilemma.
     * @param analysis The analysis result to guide reasoning.
     * @return Structured advice to return to the user.
     */
    Advice resolve(Dilemma dilemma, DilemmaAnalysis analysis);
}
