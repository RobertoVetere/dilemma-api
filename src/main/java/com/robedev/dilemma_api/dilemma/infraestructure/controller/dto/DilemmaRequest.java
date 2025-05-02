package com.robedev.dilemma_api.dilemma.infrastructure.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO representing the input data sent by the user when submitting a dilemma.
 */
@Getter
@Setter
public class DilemmaRequest {

    @NotBlank(message = "The dilemma description must not be blank.")
    private String description;
}
