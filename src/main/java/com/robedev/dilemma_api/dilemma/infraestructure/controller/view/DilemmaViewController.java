package com.robedev.dilemma_api.dilemma.infraestructure.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Simple view controller to serve the home page.
 */
@Controller
public class DilemmaViewController {

    @GetMapping("/")
    public String home(Model model) {
        return "index";
    }
}
