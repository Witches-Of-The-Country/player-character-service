package com.dungeoneer.core.web;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid request content.");
        problemDetail.setTitle("Validation Error");
        problemDetail.setType(URI.create("https://dungeoneer.app/errors/validation"));
        return problemDetail;
    }

    @ExceptionHandler(CharacterValidationException.class)
    public ProblemDetail handleCharacterValidationExceptions(CharacterValidationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problemDetail.setTitle("Validation Error");
        problemDetail.setType(URI.create("https://dungeoneer.app/errors/validation"));
        return problemDetail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentExceptions(IllegalArgumentException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problemDetail.setTitle("Validation Error");
        problemDetail.setType(URI.create("https://dungeoneer.app/errors/validation"));
        return problemDetail;
    }
}
