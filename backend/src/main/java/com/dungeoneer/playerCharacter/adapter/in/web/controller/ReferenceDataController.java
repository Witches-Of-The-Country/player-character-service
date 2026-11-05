package com.dungeoneer.playerCharacter.adapter.in.web.controller;

import com.dungeoneer.playerCharacter.application.dto.ArchetypeDto;
import com.dungeoneer.playerCharacter.application.dto.SubclassDto;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewSubclassRequest;
import com.dungeoneer.playerCharacter.application.dto.BackgroundDto;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewArchetypeRequest;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewFeatRequest;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewItemRequest;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewLineageRequest;
import com.dungeoneer.playerCharacter.application.dto.CreateHomebrewSpellRequest;
import com.dungeoneer.playerCharacter.application.dto.CursorPageResponse;
import com.dungeoneer.playerCharacter.application.dto.FeatDto;
import com.dungeoneer.playerCharacter.application.dto.ItemDto;
import com.dungeoneer.playerCharacter.application.dto.LineageDto;
import com.dungeoneer.playerCharacter.application.dto.SpellDto;
import com.dungeoneer.playerCharacter.application.port.in.CreateHomebrewUseCase;
import com.dungeoneer.playerCharacter.application.port.in.GetReferenceDataUseCase;
import com.dungeoneer.playerCharacter.domain.exception.CharacterValidationException;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reference")
@Validated
public class ReferenceDataController {

    private final GetReferenceDataUseCase getReferenceDataUseCase;
    private final CreateHomebrewUseCase createHomebrewUseCase;

    public ReferenceDataController(
            GetReferenceDataUseCase getReferenceDataUseCase,
            CreateHomebrewUseCase createHomebrewUseCase
    ) {
        this.getReferenceDataUseCase = getReferenceDataUseCase;
        this.createHomebrewUseCase = createHomebrewUseCase;
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
            @RequestParam(required = false) String ruleset,
            @RequestHeader(value = "X-User-Id", required = false) UUID userId
    ) {
        Ruleset parsedRuleset = parseRuleset(ruleset);
        return ResponseEntity.ok(getReferenceDataUseCase.getClassesPaginated(cursor, limit, q, parsedRuleset, userId));
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
            @RequestParam(required = false) String ruleset,
            @RequestHeader(value = "X-User-Id", required = false) UUID userId
    ) {
        Ruleset parsedRuleset = parseRuleset(ruleset);
        return ResponseEntity.ok(getReferenceDataUseCase.getLineagesPaginated(cursor, limit, q, parsedRuleset, userId));
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
            @RequestParam(required = false) List<Integer> levels,
            @RequestParam(required = false) String school,
            @RequestParam(required = false) List<String> schools,
            @RequestHeader(value = "X-User-Id", required = false) UUID userId
    ) {
        Ruleset parsedRuleset = parseRuleset(ruleset);
        List<Integer> effectiveLevels = (levels != null && !levels.isEmpty()) ? levels : (level != null ? List.of(level) : null);
        List<String> effectiveSchools = (schools != null && !schools.isEmpty()) ? schools : (school != null ? List.of(school) : null);
        return ResponseEntity.ok(getReferenceDataUseCase.getSpells(cursor, limit, q, parsedRuleset, effectiveLevels, effectiveSchools, userId));
    }

    @GetMapping("/feats")
    public ResponseEntity<CursorPageResponse<FeatDto>> getFeats(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String ruleset,
            @RequestParam(required = false) List<String> featTypes,
            @RequestHeader(value = "X-User-Id", required = false) UUID userId
    ) {
        Ruleset parsedRuleset = parseRuleset(ruleset);
        return ResponseEntity.ok(getReferenceDataUseCase.getFeats(cursor, limit, q, parsedRuleset, featTypes, userId));
    }

    @GetMapping("/items")
    public ResponseEntity<CursorPageResponse<ItemDto>> getItems(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String ruleset,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) String rarity,
            @RequestParam(required = false) List<String> rarities,
            @RequestHeader(value = "X-User-Id", required = false) UUID userId
    ) {
        Ruleset parsedRuleset = parseRuleset(ruleset);
        List<String> effectiveCategories = (categories != null && !categories.isEmpty()) ? categories : (category != null ? List.of(category) : null);
        List<String> effectiveRarities = (rarities != null && !rarities.isEmpty()) ? rarities : (rarity != null ? List.of(rarity) : null);
        return ResponseEntity.ok(getReferenceDataUseCase.getItems(cursor, limit, q, parsedRuleset, effectiveCategories, effectiveRarities, userId));
    }

    @PostMapping("/spells")
    public ResponseEntity<SpellDto> createSpell(
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @Valid @RequestBody CreateHomebrewSpellRequest request
    ) {
        SpellDto created = createHomebrewUseCase.createSpell(userId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PostMapping("/feats")
    public ResponseEntity<FeatDto> createFeat(
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @Valid @RequestBody CreateHomebrewFeatRequest request
    ) {
        FeatDto created = createHomebrewUseCase.createFeat(userId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PostMapping("/items")
    public ResponseEntity<ItemDto> createItem(
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @Valid @RequestBody CreateHomebrewItemRequest request
    ) {
        ItemDto created = createHomebrewUseCase.createItem(userId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PostMapping("/classes")
    public ResponseEntity<ArchetypeDto> createArchetype(
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @Valid @RequestBody CreateHomebrewArchetypeRequest request
    ) {
        ArchetypeDto created = createHomebrewUseCase.createArchetype(userId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PostMapping("/lineages")
    public ResponseEntity<LineageDto> createLineage(
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @Valid @RequestBody CreateHomebrewLineageRequest request
    ) {
        LineageDto created = createHomebrewUseCase.createLineage(userId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/subclasses")
    public ResponseEntity<List<SubclassDto>> getSubclasses(
            @RequestParam UUID archetypeId,
            @RequestParam(required = false) String ruleset
    ) {
        Ruleset parsedRuleset = parseRuleset(ruleset);
        return ResponseEntity.ok(getReferenceDataUseCase.getSubclasses(archetypeId, parsedRuleset));
    }

    @GetMapping("/progression-feats")
    public ResponseEntity<List<FeatDto>> getProgressionFeats(
            @RequestParam UUID archetypeId,
            @RequestParam int level
    ) {
        return ResponseEntity.ok(getReferenceDataUseCase.getProgressionFeats(archetypeId, level));
    }

    @GetMapping("/subclass-progression-feats")
    public ResponseEntity<List<FeatDto>> getSubclassProgressionFeats(
            @RequestParam UUID subclassId,
            @RequestParam int level
    ) {
        return ResponseEntity.ok(getReferenceDataUseCase.getSubclassProgressionFeats(subclassId, level));
    }

    @PostMapping("/subclasses")
    public ResponseEntity<SubclassDto> createSubclass(
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @Valid @RequestBody CreateHomebrewSubclassRequest request
    ) {
        SubclassDto created = createHomebrewUseCase.createSubclass(userId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/spells/{id}")
    public ResponseEntity<SpellDto> updateSpell(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @Valid @RequestBody CreateHomebrewSpellRequest request
    ) {
        return ResponseEntity.ok(createHomebrewUseCase.updateSpell(id, userId, request));
    }

    @PutMapping("/feats/{id}")
    public ResponseEntity<FeatDto> updateFeat(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @Valid @RequestBody CreateHomebrewFeatRequest request
    ) {
        return ResponseEntity.ok(createHomebrewUseCase.updateFeat(id, userId, request));
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<ItemDto> updateItem(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @Valid @RequestBody CreateHomebrewItemRequest request
    ) {
        return ResponseEntity.ok(createHomebrewUseCase.updateItem(id, userId, request));
    }

    @PutMapping("/classes/{id}")
    public ResponseEntity<ArchetypeDto> updateArchetype(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @Valid @RequestBody CreateHomebrewArchetypeRequest request
    ) {
        return ResponseEntity.ok(createHomebrewUseCase.updateArchetype(id, userId, request));
    }

    @PutMapping("/lineages/{id}")
    public ResponseEntity<LineageDto> updateLineage(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @Valid @RequestBody CreateHomebrewLineageRequest request
    ) {
        return ResponseEntity.ok(createHomebrewUseCase.updateLineage(id, userId, request));
    }

    @PutMapping("/subclasses/{id}")
    public ResponseEntity<SubclassDto> updateSubclass(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @Valid @RequestBody CreateHomebrewSubclassRequest request
    ) {
        return ResponseEntity.ok(createHomebrewUseCase.updateSubclass(id, userId, request));
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
