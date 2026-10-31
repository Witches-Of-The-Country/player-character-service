package com.dungeoneer.core.web;

import com.dungeoneer.playerCharacter.domain.exception.CharacterNotFoundException;
import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import com.dungeoneer.playerCharacter.domain.exception.InvalidRollTokenException;
import com.dungeoneer.playerCharacter.domain.exception.RollConflictException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(jakarta.validation.ConstraintViolationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Constraint Violation");
        problemDetail.setType(URI.create("https://dungeoneer.app/errors/bad-request"));
        return problemDetail;
    }

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

    @ExceptionHandler(InvalidRollTokenException.class)
    public ProblemDetail handleInvalidRollTokenExceptions(InvalidRollTokenException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problemDetail.setTitle("Invalid Roll Token");
        problemDetail.setType(URI.create("https://dungeoneer.app/errors/invalid-roll-token"));
        return problemDetail;
    }

    @ExceptionHandler(RollConflictException.class)
    public ProblemDetail handleRollConflictExceptions(RollConflictException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Roll Conflict");
        problemDetail.setType(URI.create("https://dungeoneer.app/errors/conflict"));
        return problemDetail;
    }

    @ExceptionHandler(CharacterNotFoundException.class)
    public ProblemDetail handleCharacterNotFoundExceptions(CharacterNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Resource Not Found");
        problemDetail.setType(URI.create("https://dungeoneer.app/errors/not-found"));
        return problemDetail;
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ProblemDetail handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        HttpStatus status = "X-User-Id".equalsIgnoreCase(ex.getHeaderName())
                ? HttpStatus.UNAUTHORIZED
                : HttpStatus.BAD_REQUEST;
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, "Missing required request header: " + ex.getHeaderName());
        problemDetail.setTitle(status == HttpStatus.UNAUTHORIZED ? "Unauthorized" : "Bad Request");
        problemDetail.setType(URI.create("https://dungeoneer.app/errors/" + (status == HttpStatus.UNAUTHORIZED ? "unauthorized" : "bad-request")));
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
