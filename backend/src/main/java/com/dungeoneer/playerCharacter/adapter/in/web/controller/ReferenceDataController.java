package com.dungeoneer.playerCharacter.adapter.in.web.controller;

import com.dungeoneer.playerCharacter.application.dto.ArchetypeDto;
import com.dungeoneer.playerCharacter.application.dto.BackgroundDto;
import com.dungeoneer.playerCharacter.application.dto.LineageDto;
import com.dungeoneer.playerCharacter.application.port.in.GetReferenceDataUseCase;
import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/reference")
public class ReferenceDataController {

    private final GetReferenceDataUseCase getReferenceDataUseCase;

    public ReferenceDataController(GetReferenceDataUseCase getReferenceDataUseCase) {
        this.getReferenceDataUseCase = getReferenceDataUseCase;
    }

    @GetMapping("/classes")
    public ResponseEntity<List<ArchetypeDto>> getClasses(@RequestParam(required = false) String ruleset) {
        Ruleset parsedRuleset = parseRuleset(ruleset);
        return ResponseEntity.ok(getReferenceDataUseCase.getClasses(parsedRuleset));
    }

    @GetMapping("/lineages")
    public ResponseEntity<List<LineageDto>> getLineages(@RequestParam(required = false) String ruleset) {
        Ruleset parsedRuleset = parseRuleset(ruleset);
        return ResponseEntity.ok(getReferenceDataUseCase.getLineages(parsedRuleset));
    }

    @GetMapping("/backgrounds")
    public ResponseEntity<List<BackgroundDto>> getBackgrounds(@RequestParam(required = false) String ruleset) {
        Ruleset parsedRuleset = parseRuleset(ruleset);
        return ResponseEntity.ok(getReferenceDataUseCase.getBackgrounds(parsedRuleset));
    }

    private Ruleset parseRuleset(String ruleset) {
        if (ruleset == null || ruleset.isBlank()) {
            return null;
        }
        try {
            return Ruleset.valueOf(ruleset.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CharacterValidationException("Invalid ruleset: '" + ruleset + "'. Supported values are: " + Arrays.toString(Ruleset.values()));
        }
    }
}
