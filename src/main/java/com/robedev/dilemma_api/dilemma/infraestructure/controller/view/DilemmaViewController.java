package com.robedev.dilemma_api.dilemma.infraestructure.controller.view;

import com.robedev.dilemma_api.dilemma.application.port.in.SolveDilemmaUseCase;
import com.robedev.dilemma_api.dilemma.domain.model.Advice;
import com.robedev.dilemma_api.dilemma.domain.model.Dilemma;
import com.robedev.dilemma_api.dilemma.infrastructure.controller.dto.DilemmaRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller that serves the Thymeleaf view for submitting and resolving dilemmas.
 */
@Controller
@RequestMapping("/")
public class DilemmaViewController {

    private final SolveDilemmaUseCase useCase;

    public DilemmaViewController(SolveDilemmaUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    public String home(Model model) {
        model.addAttribute("dilemmaRequest", new DilemmaRequest());
        return "index";
    }

    @PostMapping
    public String submit(@ModelAttribute("dilemmaRequest") @Valid DilemmaRequest request, Model model) {
        Dilemma dilemma = new Dilemma(request.getDescription());
        Advice advice = useCase.solve(dilemma);
        model.addAttribute("advice", advice);
        return "index";
    }
}
