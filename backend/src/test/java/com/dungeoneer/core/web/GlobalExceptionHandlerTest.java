package com.dungeoneer.core.web;

import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dungeoneer.core.config.SecurityConfig;

@WebMvcTest(controllers = DummyController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenMethodArgumentNotValid_thenReturns422ProblemDetail() throws Exception {
        String invalidPayload = "{ \"name\": \"\" }";

        mockMvc.perform(post("/dummy/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPayload))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://dungeoneer.app/errors/validation"))
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.detail").value("Invalid request content."))
                .andExpect(jsonPath("$.instance").value("/dummy/validate"));
    }

    @Test
    void whenCharacterValidationException_thenReturns422ProblemDetail() throws Exception {
        mockMvc.perform(get("/dummy/character-validation"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://dungeoneer.app/errors/validation"))
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.detail").value("Character validation failed."))
                .andExpect(jsonPath("$.instance").value("/dummy/character-validation"));
    }

    @Test
    void whenIllegalArgumentException_thenReturns422ProblemDetail() throws Exception {
        mockMvc.perform(get("/dummy/illegal-argument"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://dungeoneer.app/errors/validation"))
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.detail").value("Illegal argument supplied."))
                .andExpect(jsonPath("$.instance").value("/dummy/illegal-argument"));
    }

    @Test
    void whenHttpMessageNotReadable_thenReturns400ProblemDetail() throws Exception {
        String malformedUuidPayload = "{ \"name\": \"Valid\", \"targetId\": \"not-a-uuid\" }";

        mockMvc.perform(post("/dummy/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedUuidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://dungeoneer.app/errors/bad-request"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void whenDataIntegrityViolation_thenReturns409ProblemDetail() throws Exception {
        mockMvc.perform(get("/dummy/data-integrity"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://dungeoneer.app/errors/data-conflict"))
                .andExpect(jsonPath("$.title").value("Conflito de Integridade de Dados"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value("Já existe um registro conflitante com os mesmos identificadores ou dados únicos."))
                .andExpect(jsonPath("$.instance").value("/dummy/data-integrity"));
    }

    @Test
    void whenUnhandledException_thenReturns500ProblemDetail() throws Exception {
        mockMvc.perform(get("/dummy/generic-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://dungeoneer.app/errors/internal-error"))
                .andExpect(jsonPath("$.title").value("Erro Interno do Servidor"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").value("Erro interno no servidor: Unexpected internal failure"))
                .andExpect(jsonPath("$.instance").value("/dummy/generic-error"));
    }
}

@RestController
class DummyController {
    @PostMapping("/dummy/validate")
    public void dummyEndpoint(@Valid @RequestBody DummyDto dto) {
    }

    @GetMapping("/dummy/character-validation")
    public void characterValidationEndpoint() {
        throw new CharacterValidationException("Character validation failed.");
    }

    @GetMapping("/dummy/illegal-argument")
    public void illegalArgumentEndpoint() {
        throw new IllegalArgumentException("Illegal argument supplied.");
    }

    @GetMapping("/dummy/data-integrity")
    public void dataIntegrityEndpoint() {
        throw new org.springframework.dao.DataIntegrityViolationException(
                "could not execute statement",
                new org.hibernate.exception.ConstraintViolationException("duplicate key value violates unique constraint \"uq_character_feat\"", null, "uq_character_feat")
        );
    }

    @GetMapping("/dummy/generic-error")
    public void genericErrorEndpoint() {
        throw new RuntimeException("Unexpected internal failure");
    }
}

record DummyDto(@NotBlank(message = "Name cannot be blank") String name, UUID targetId) {}
