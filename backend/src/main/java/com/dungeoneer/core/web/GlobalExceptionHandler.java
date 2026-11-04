package com.dungeoneer.core.web;

import com.dungeoneer.playerCharacter.domain.exception.CharacterNotFoundException;
import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import com.dungeoneer.playerCharacter.domain.exception.InvalidRollTokenException;
import com.dungeoneer.playerCharacter.domain.exception.RestValidationException;
import com.dungeoneer.playerCharacter.domain.exception.RollConflictException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;
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

    @ExceptionHandler(RestValidationException.class)
    public ProblemDetail handleRestValidationExceptions(RestValidationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Bad Request");
        problemDetail.setType(URI.create("https://dungeoneer.app/errors/bad-request"));
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

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ProblemDetail handleMalformedPayloadExceptions(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Malformed request payload or invalid field format: " + ex.getMessage()
        );
        problemDetail.setTitle("Bad Request");
        problemDetail.setType(URI.create("https://dungeoneer.app/errors/bad-request"));
        return problemDetail;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        String rootMsg = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
        String userDetail = "Conflito de integridade de dados ao processar a operação.";
        if (rootMsg != null) {
            String lower = rootMsg.toLowerCase();
            if (lower.contains("uq_") || lower.contains("unique") || lower.contains("duplicate key")) {
                userDetail = "Já existe um registro conflitante com os mesmos identificadores ou dados únicos.";
            } else if (lower.contains("foreign key") || lower.contains("fk_")) {
                userDetail = "Referência a entidade inexistente ou inválida no banco de dados.";
            } else if (lower.contains("invalid input syntax for type json")) {
                userDetail = "Formato de payload ou dados JSON inválidos para persistência.";
            }
        }
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, userDetail);
        problemDetail.setTitle("Conflito de Integridade de Dados");
        problemDetail.setType(URI.create("https://dungeoneer.app/errors/data-conflict"));
        if (request != null) {
            problemDetail.setInstance(URI.create(request.getRequestURI()));
        }
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, HttpServletRequest request) {
        String detailMessage = ex.getMessage() != null && !ex.getMessage().isBlank()
                ? "Erro interno no servidor: " + ex.getMessage()
                : "Ocorreu um erro interno inesperado ao processar a requisição.";
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, detailMessage);
        problemDetail.setTitle("Erro Interno do Servidor");
        problemDetail.setType(URI.create("https://dungeoneer.app/errors/internal-error"));
        if (request != null) {
            problemDetail.setInstance(URI.create(request.getRequestURI()));
        }
        return problemDetail;
    }
}
