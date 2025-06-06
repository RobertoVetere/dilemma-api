package com.robedev.dilemma_api.dilemma.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Represents the reasoned response to a dilemma, generated by the AI.
 */
@Getter
@AllArgsConstructor
public class Advice {

    private final String summary;
    private final List<String> options;
    private final String recommendation;
    private final String tone;
}
