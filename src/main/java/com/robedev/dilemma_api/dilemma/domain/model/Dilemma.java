package com.robedev.dilemma_api.dilemma.domain.model;

import com.robedev.dilemma_api.dilemma.domain.exception.InvalidDilemmaException;
import lombok.Getter;

/**
 * Domain model / Represents the dilemma sent by the user.
 */
@Getter
public class Dilemma {

    private final String description;

    public Dilemma(String description) {
        if (description == null || description.isBlank()) {
            throw new InvalidDilemmaException("The dilemma description cannot be empty.");
        }
        this.description = description.trim();
    }
}
