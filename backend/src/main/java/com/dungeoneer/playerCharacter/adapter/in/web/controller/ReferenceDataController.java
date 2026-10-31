package com.dungeoneer.playerCharacter.adapter.in.web.controller;

import com.dungeoneer.playerCharacter.application.dto.ArchetypeDto;
import com.dungeoneer.playerCharacter.application.dto.BackgroundDto;
import com.dungeoneer.playerCharacter.application.dto.CursorPageResponse;
import com.dungeoneer.playerCharacter.application.dto.FeatDto;
import com.dungeoneer.playerCharacter.application.dto.ItemDto;
import com.dungeoneer.playerCharacter.application.dto.LineageDto;
import com.dungeoneer.playerCharacter.application.dto.SpellDto;
import com.dungeoneer.playerCharacter.application.port.in.GetReferenceDataUseCase;
import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/reference")
@Validated
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

    @GetMapping("/classes/paginated")
    public ResponseEntity<CursorPageResponse<ArchetypeDto>> getClassesPaginated(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String ruleset
    ) {
        Ruleset parsedRuleset = parseRuleset(ruleset);
        return ResponseEntity.ok(getReferenceDataUseCase.getClassesPaginated(cursor, limit, q, parsedRuleset));
    }

    @GetMapping("/lineages")
    public ResponseEntity<List<LineageDto>> getLineages(@RequestParam(required = false) String ruleset) {
        Ruleset parsedRuleset = parseRuleset(ruleset);
        return ResponseEntity.ok(getReferenceDataUseCase.getLineages(parsedRuleset));
    }

    @GetMapping("/lineages/paginated")
    public ResponseEntity<CursorPageResponse<LineageDto>> getLineagesPaginated(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String ruleset
    ) {
        Ruleset parsedRuleset = parseRuleset(ruleset);
        return ResponseEntity.ok(getReferenceDataUseCase.getLineagesPaginated(cursor, limit, q, parsedRuleset));
    }

    @GetMapping("/backgrounds")
    public ResponseEntity<List<BackgroundDto>> getBackgrounds(@RequestParam(required = false) String ruleset) {
        Ruleset parsedRuleset = parseRuleset(ruleset);
        return ResponseEntity.ok(getReferenceDataUseCase.getBackgrounds(parsedRuleset));
    }

    @GetMapping("/spells")
    public ResponseEntity<CursorPageResponse<SpellDto>> getSpells(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String ruleset,
            @RequestParam(required = false) Integer level,
            @RequestParam(required = false) String school
    ) {
        Ruleset parsedRuleset = parseRuleset(ruleset);
        return ResponseEntity.ok(getReferenceDataUseCase.getSpells(cursor, limit, q, parsedRuleset, level, school));
    }

    @GetMapping("/feats")
    public ResponseEntity<CursorPageResponse<FeatDto>> getFeats(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String ruleset
    ) {
        Ruleset parsedRuleset = parseRuleset(ruleset);
        return ResponseEntity.ok(getReferenceDataUseCase.getFeats(cursor, limit, q, parsedRuleset));
    }

    @GetMapping("/items")
    public ResponseEntity<CursorPageResponse<ItemDto>> getItems(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String ruleset,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String rarity
    ) {
        Ruleset parsedRuleset = parseRuleset(ruleset);
        return ResponseEntity.ok(getReferenceDataUseCase.getItems(cursor, limit, q, parsedRuleset, category, rarity));
    }

    private Ruleset parseRuleset(String ruleset) {
        if (ruleset == null || ruleset.isBlank()) {
            return null;
        }
        try {
            return Ruleset.valueOf(ruleset.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CharacterValidationException(
                    "Invalid ruleset: " + ruleset + ". Valid values are: " + Arrays.toString(Ruleset.values())
            );
        }
    }
}
