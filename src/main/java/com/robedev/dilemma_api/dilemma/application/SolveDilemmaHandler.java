package com.robedev.dilemma_api.dilemma.application;

import com.robedev.dilemma_api.dilemma.application.port.in.SolveDilemmaUseCase;
import com.robedev.dilemma_api.dilemma.application.port.out.SonarGateway;
import com.robedev.dilemma_api.dilemma.domain.model.Advice;
import com.robedev.dilemma_api.dilemma.domain.model.Dilemma;
import com.robedev.dilemma_api.dilemma.infraestructure.sonar.DilemmaAnalysis;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Application service that handles the dilemma resolution process by coordinating
 * semantic analysis and reasoning through the Sonar AI.
 */
@Service
public class SolveDilemmaHandler implements SolveDilemmaUseCase {

    private final SonarGateway sonarGateway;

    public SolveDilemmaHandler(@Qualifier("sonarOkHttp") SonarGateway sonarGateway) {
        this.sonarGateway = sonarGateway;
    }

    @Override
    public Advice solve(Dilemma dilemma) {
        // Step 1: classify the dilemma and extract context
        DilemmaAnalysis analysis = sonarGateway.analyze(dilemma);

        // Step 2: use the analysis to guide a deeper reasoning process
        return sonarGateway.resolve(dilemma, analysis);
    }
}
