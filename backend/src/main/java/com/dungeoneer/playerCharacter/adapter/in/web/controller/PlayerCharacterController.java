package com.dungeoneer.playerCharacter.adapter.in.web.controller;

import com.dungeoneer.playerCharacter.adapter.in.web.dto.AbilityRollResponse;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.CharacterSheetResponse;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.CharacterSummaryResponse;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.CreateCharacterRequest;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.HpRollResponse;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.RollHpRequest;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.UpdateCharacterRequest;
import com.dungeoneer.playerCharacter.adapter.in.web.mapper.PlayerCharacterWebMapper;
import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.dto.CharacterSummaryResult;
import com.dungeoneer.playerCharacter.application.dto.RollAbilityScoresResult;
import com.dungeoneer.playerCharacter.application.dto.RollHitPointsCommand;
import com.dungeoneer.playerCharacter.application.dto.RollHitPointsResult;
import com.dungeoneer.playerCharacter.application.port.in.CreateCharacterUseCase;
import com.dungeoneer.playerCharacter.application.port.in.DeleteCharacterUseCase;
import com.dungeoneer.playerCharacter.application.port.in.GetCharacterUseCase;
import com.dungeoneer.playerCharacter.application.port.in.ListCharactersUseCase;
import com.dungeoneer.playerCharacter.application.port.in.RollAbilityScoresUseCase;
import com.dungeoneer.playerCharacter.application.port.in.RollHitPointsUseCase;
import com.dungeoneer.playerCharacter.application.port.in.UpdateCharacterUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/characters")
public class PlayerCharacterController {

    private final CreateCharacterUseCase createCharacterUseCase;
    private final GetCharacterUseCase getCharacterUseCase;
    private final ListCharactersUseCase listCharactersUseCase;
    private final UpdateCharacterUseCase updateCharacterUseCase;
    private final DeleteCharacterUseCase deleteCharacterUseCase;
    private final RollAbilityScoresUseCase rollAbilityScoresUseCase;
    private final RollHitPointsUseCase rollHitPointsUseCase;

    public PlayerCharacterController(
            CreateCharacterUseCase createCharacterUseCase,
            GetCharacterUseCase getCharacterUseCase,
            ListCharactersUseCase listCharactersUseCase,
            UpdateCharacterUseCase updateCharacterUseCase,
            DeleteCharacterUseCase deleteCharacterUseCase,
            RollAbilityScoresUseCase rollAbilityScoresUseCase,
            RollHitPointsUseCase rollHitPointsUseCase
    ) {
        this.createCharacterUseCase = createCharacterUseCase;
        this.getCharacterUseCase = getCharacterUseCase;
        this.listCharactersUseCase = listCharactersUseCase;
        this.updateCharacterUseCase = updateCharacterUseCase;
        this.deleteCharacterUseCase = deleteCharacterUseCase;
        this.rollAbilityScoresUseCase = rollAbilityScoresUseCase;
        this.rollHitPointsUseCase = rollHitPointsUseCase;
    }

    @PostMapping("/ability-rolls")
    public ResponseEntity<AbilityRollResponse> rollAbilityScores(
            @RequestHeader("X-User-Id") UUID userId
    ) {
        RollAbilityScoresResult result = rollAbilityScoresUseCase.rollAbilityScores(userId);
        AbilityRollResponse response = PlayerCharacterWebMapper.toResponse(result);

        HttpStatus status = result.isNew() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/hp-rolls")
    public ResponseEntity<HpRollResponse> rollHitPoints(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody RollHpRequest request
    ) {
        RollHitPointsCommand command = new RollHitPointsCommand(userId, request.archetypeId(), request.level());
        RollHitPointsResult result = rollHitPointsUseCase.rollHitPoints(command);
        HpRollResponse response = PlayerCharacterWebMapper.toResponse(result);

        HttpStatus status = result.isNew() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping
    public ResponseEntity<CharacterSheetResponse> createCharacter(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CreateCharacterRequest request
    ) {
        CharacterSheetResult result = createCharacterUseCase.createCharacter(
                PlayerCharacterWebMapper.toCommand(userId, request)
        );
        CharacterSheetResponse response = PlayerCharacterWebMapper.toResponse(result);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CharacterSummaryResponse>> listCharacters(
            @RequestHeader("X-User-Id") UUID userId
    ) {
        List<CharacterSummaryResult> summaries = listCharactersUseCase.listCharacters(userId);
        List<CharacterSummaryResponse> response = summaries.stream()
                .map(PlayerCharacterWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CharacterSheetResponse> getCharacter(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("id") UUID id
    ) {
        CharacterSheetResult result = getCharacterUseCase.getCharacter(id, userId);
        return ResponseEntity.ok(PlayerCharacterWebMapper.toResponse(result));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CharacterSheetResponse> patchCharacter(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateCharacterRequest request
    ) {
        CharacterSheetResult result = updateCharacterUseCase.updateCharacter(
                PlayerCharacterWebMapper.toCommand(id, userId, request)
        );
        return ResponseEntity.ok(PlayerCharacterWebMapper.toResponse(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CharacterSheetResponse> updateCharacter(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateCharacterRequest request
    ) {
        return patchCharacter(userId, id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCharacter(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("id") UUID id
    ) {
        deleteCharacterUseCase.deleteCharacter(id, userId);
        return ResponseEntity.noContent().build();
    }
}
