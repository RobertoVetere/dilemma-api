package com.robedev.dilemma_api.dilemma.infraestructure.controller.advice;

import com.robedev.dilemma_api.dilemma.domain.exception.InvalidDilemmaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidDilemmaException.class)
    public ResponseEntity<String> handleInvalidDilemma(InvalidDilemmaException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}
