package com.robedev.dilemma_api.dilemma.infraestructure.controller;

import com.robedev.dilemma_api.dilemma.application.port.in.SolveDilemmaUseCase;
import com.robedev.dilemma_api.dilemma.domain.model.Dilemma;
import com.robedev.dilemma_api.dilemma.domain.model.Advice;
import com.robedev.dilemma_api.dilemma.infraestructure.controller.dto.AdviceResponse;
import com.robedev.dilemma_api.dilemma.infrastructure.controller.dto.DilemmaRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * REST controller for receiving user dilemmas and returning reasoned advice.
 */
@RestController
@RequestMapping("/api/v1/dilemmas")
public class DilemmaController {

    private final SolveDilemmaUseCase useCase;

    public DilemmaController(SolveDilemmaUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<AdviceResponse> resolve(@RequestBody @Valid DilemmaRequest request) {
        Dilemma dilemma = new Dilemma(request.getDescription());
        Advice advice = useCase.solve(dilemma);
        AdviceResponse response = new AdviceResponse(advice);
        return ResponseEntity.ok(response);
    }
}
