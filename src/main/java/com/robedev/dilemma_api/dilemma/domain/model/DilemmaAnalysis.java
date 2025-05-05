package com.robedev.dilemma_api.dilemma.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents the semantic analysis of a dilemma before reasoning.
 * This structure is built from the response returned by Sonar.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DilemmaAnalysis {

    private DilemmaCategory category;
    private String subcategory;
    private String depth;
    private String urgency;
    private List<String> conflicts;
    private String decisionType;
    private List<String> relevantFactors;
    private String recommendedTone;
}
