package com.robedev.dilemma_api.dilemma.application.port.in;

import com.robedev.dilemma_api.dilemma.domain.model.Advice;
import com.robedev.dilemma_api.dilemma.domain.model.Dilemma;

/**
 * Input port for handling dilemma resolution use cases.
 */
public interface SolveDilemmaUseCase {
    Advice solve(Dilemma dilemma);
}
