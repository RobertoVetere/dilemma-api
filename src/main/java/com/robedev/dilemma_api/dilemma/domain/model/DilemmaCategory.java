package com.robedev.dilemma_api.dilemma.domain.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

/**
 * Represents the high-level category of a dilemma.
 */
public enum DilemmaCategory {
    PERSONAL,
    EMOTIONAL,
    ETHICAL,
    PROFESSIONAL,
    RELATIONAL,
    FAMILY,
    CONSUMER,
    TECHNICAL,
    @JsonEnumDefaultValue
    OTHER
}
