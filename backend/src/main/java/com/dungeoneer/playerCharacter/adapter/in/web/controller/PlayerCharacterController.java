package com.dungeoneer.playerCharacter.adapter.in.web.controller;

import com.dungeoneer.playerCharacter.adapter.in.web.dto.AbilityRollResponse;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.CharacterNoteResponse;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.CharacterSheetResponse;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.CharacterSummaryResponse;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.CreateCharacterNoteRequest;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.CreateCharacterRequest;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.HpRollResponse;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.RollHpRequest;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.UpdateCharacterNoteRequest;
import com.dungeoneer.playerCharacter.adapter.in.web.dto.UpdateCharacterRequest;
import com.dungeoneer.playerCharacter.adapter.in.web.mapper.PlayerCharacterWebMapper;
import com.dungeoneer.playerCharacter.application.dto.AddCharacterItemCommand;
import com.dungeoneer.playerCharacter.application.dto.AddExperienceCommand;
import com.dungeoneer.playerCharacter.application.dto.CharacterNoteResult;
import com.dungeoneer.playerCharacter.application.dto.CharacterSheetResult;
import com.dungeoneer.playerCharacter.application.dto.CharacterSummaryResult;
import com.dungeoneer.playerCharacter.application.dto.LevelUpCommand;
import com.dungeoneer.playerCharacter.application.dto.RestCommand;
import com.dungeoneer.playerCharacter.application.dto.RollAbilityScoresResult;
import com.dungeoneer.playerCharacter.application.dto.RollHitPointsCommand;
import com.dungeoneer.playerCharacter.application.dto.RollHitPointsResult;
import com.dungeoneer.playerCharacter.application.dto.UpdateCharacterItemCommand;
import com.dungeoneer.playerCharacter.application.dto.UseConsumableResult;
import com.dungeoneer.playerCharacter.application.port.in.AddCharacterNoteUseCase;
import com.dungeoneer.playerCharacter.application.port.in.AddExperienceUseCase;
import com.dungeoneer.playerCharacter.application.port.in.CreateCharacterUseCase;
import com.dungeoneer.playerCharacter.application.port.in.DeleteCharacterUseCase;
import com.dungeoneer.playerCharacter.application.port.in.GetCharacterUseCase;
import com.dungeoneer.playerCharacter.application.port.in.LevelUpUseCase;
import com.dungeoneer.playerCharacter.application.port.in.ListCharacterNotesUseCase;
import com.dungeoneer.playerCharacter.application.port.in.ListCharactersUseCase;
import com.dungeoneer.playerCharacter.application.port.in.ManageInventoryUseCase;
import com.dungeoneer.playerCharacter.application.port.in.RestUseCase;
import com.dungeoneer.playerCharacter.application.port.in.RollAbilityScoresUseCase;
import com.dungeoneer.playerCharacter.application.port.in.RollHitPointsUseCase;
import com.dungeoneer.playerCharacter.application.port.in.UpdateCharacterNoteUseCase;
import com.dungeoneer.playerCharacter.application.port.in.UpdateCharacterUseCase;
import com.dungeoneer.playerCharacter.application.port.in.UseConsumableUseCase;
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
    private final AddCharacterNoteUseCase addCharacterNoteUseCase;
    private final ListCharacterNotesUseCase listCharacterNotesUseCase;
    private final UpdateCharacterNoteUseCase updateCharacterNoteUseCase;
    private final AddExperienceUseCase addExperienceUseCase;
    private final LevelUpUseCase levelUpUseCase;
    private final ManageInventoryUseCase manageInventoryUseCase;
    private final UseConsumableUseCase useConsumableUseCase;
    private final RestUseCase restUseCase;

    public PlayerCharacterController(
            CreateCharacterUseCase createCharacterUseCase,
            GetCharacterUseCase getCharacterUseCase,
            ListCharactersUseCase listCharactersUseCase,
            UpdateCharacterUseCase updateCharacterUseCase,
            DeleteCharacterUseCase deleteCharacterUseCase,
            RollAbilityScoresUseCase rollAbilityScoresUseCase,
            RollHitPointsUseCase rollHitPointsUseCase,
            AddCharacterNoteUseCase addCharacterNoteUseCase,
            ListCharacterNotesUseCase listCharacterNotesUseCase,
            UpdateCharacterNoteUseCase updateCharacterNoteUseCase,
            AddExperienceUseCase addExperienceUseCase,
            LevelUpUseCase levelUpUseCase,
            ManageInventoryUseCase manageInventoryUseCase,
            UseConsumableUseCase useConsumableUseCase,
            RestUseCase restUseCase
    ) {
        this.createCharacterUseCase = createCharacterUseCase;
        this.getCharacterUseCase = getCharacterUseCase;
        this.listCharactersUseCase = listCharactersUseCase;
        this.updateCharacterUseCase = updateCharacterUseCase;
        this.deleteCharacterUseCase = deleteCharacterUseCase;
        this.rollAbilityScoresUseCase = rollAbilityScoresUseCase;
        this.rollHitPointsUseCase = rollHitPointsUseCase;
        this.addCharacterNoteUseCase = addCharacterNoteUseCase;
        this.listCharacterNotesUseCase = listCharacterNotesUseCase;
        this.updateCharacterNoteUseCase = updateCharacterNoteUseCase;
        this.addExperienceUseCase = addExperienceUseCase;
        this.levelUpUseCase = levelUpUseCase;
        this.manageInventoryUseCase = manageInventoryUseCase;
        this.useConsumableUseCase = useConsumableUseCase;
        this.restUseCase = restUseCase;
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
        RollHitPointsCommand command = new RollHitPointsCommand(
                userId,
                request.archetypeId(),
                request.level(),
                Boolean.TRUE.equals(request.isSecondaryClass())
        );
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

    @PostMapping("/{id}/notes")
    public ResponseEntity<CharacterNoteResponse> addCharacterNote(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("id") UUID id,
            @Valid @RequestBody CreateCharacterNoteRequest request
    ) {
        CharacterNoteResult result = addCharacterNoteUseCase.addNote(
                id,
                userId,
                PlayerCharacterWebMapper.toCommand(request)
        );
        CharacterNoteResponse response = PlayerCharacterWebMapper.toResponse(result);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{noteId}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}/notes/{noteId}")
    public ResponseEntity<CharacterNoteResponse> updateCharacterNote(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("id") UUID id,
            @PathVariable("noteId") UUID noteId,
            @Valid @RequestBody UpdateCharacterNoteRequest request
    ) {
        CharacterNoteResult result = updateCharacterNoteUseCase.updateNote(
                id,
                noteId,
                userId,
                PlayerCharacterWebMapper.toCommand(request)
        );
        return ResponseEntity.ok(PlayerCharacterWebMapper.toResponse(result));
    }

    @GetMapping("/{id}/notes")
    public ResponseEntity<List<CharacterNoteResponse>> listCharacterNotes(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("id") UUID id
    ) {
        List<CharacterNoteResult> notes = listCharacterNotesUseCase.listNotes(id, userId);
        List<CharacterNoteResponse> response = notes.stream()
                .map(PlayerCharacterWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/xp")
    public ResponseEntity<CharacterSheetResponse> addExperience(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("id") UUID id,
            @Valid @RequestBody AddExperienceCommand command
    ) {
        CharacterSheetResult result = addExperienceUseCase.addExperience(id, userId, command);
        return ResponseEntity.ok(CharacterSheetResponse.fromResult(result));
    }

    @PostMapping("/{id}/level-up")
    public ResponseEntity<CharacterSheetResponse> levelUp(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("id") UUID id,
            @Valid @RequestBody LevelUpCommand command
    ) {
        CharacterSheetResult result = levelUpUseCase.levelUp(id, userId, command);
        return ResponseEntity.ok(CharacterSheetResponse.fromResult(result));
    }

    @PostMapping("/{id}/rest")
    public ResponseEntity<CharacterSheetResponse> takeRest(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("id") UUID id,
            @Valid @RequestBody RestCommand command
    ) {
        CharacterSheetResult result = restUseCase.takeRest(id, userId, command);
        return ResponseEntity.ok(CharacterSheetResponse.fromResult(result));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<CharacterSheetResponse> addItem(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("id") UUID id,
            @Valid @RequestBody AddCharacterItemCommand command
    ) {
        CharacterSheetResult result = manageInventoryUseCase.addItem(id, userId, command);
        return ResponseEntity.ok(CharacterSheetResponse.fromResult(result));
    }

    @PatchMapping("/{id}/items/{itemId}")
    public ResponseEntity<CharacterSheetResponse> updateItem(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("id") UUID id,
            @PathVariable("itemId") UUID itemId,
            @Valid @RequestBody UpdateCharacterItemCommand command
    ) {
        CharacterSheetResult result = manageInventoryUseCase.updateItem(id, userId, itemId, command);
        return ResponseEntity.ok(CharacterSheetResponse.fromResult(result));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<CharacterSheetResponse> removeItem(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("id") UUID id,
            @PathVariable("itemId") UUID itemId
    ) {
        CharacterSheetResult result = manageInventoryUseCase.removeItem(id, userId, itemId);
        return ResponseEntity.ok(CharacterSheetResponse.fromResult(result));
    }

    @PostMapping("/{id}/items/{itemId}/use")
    public ResponseEntity<UseConsumableResult> useConsumable(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("id") UUID id,
            @PathVariable("itemId") UUID itemId
    ) {
        UseConsumableResult result = useConsumableUseCase.useConsumable(id, userId, itemId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/feats/{featId}/use")
    public ResponseEntity<CharacterSheetResponse> useFeat(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("id") UUID id,
            @PathVariable("featId") UUID featId
    ) {
        CharacterSheetResult result = restUseCase.useFeat(id, userId, featId);
        return ResponseEntity.ok(CharacterSheetResponse.fromResult(result));
    }
}
