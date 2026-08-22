package com.dungeoneer.playerCharacter.adapter.in.web.controller;

import com.dungeoneer.playerCharacter.adapter.in.web.dto.CreatePlayerCharacterRequest;
import com.dungeoneer.playerCharacter.adapter.in.web.mapper.PlayerCharacterWebMapper;
import com.dungeoneer.playerCharacter.application.dto.CharacterResponseDto;
import com.dungeoneer.playerCharacter.application.port.in.CreateCharacterCommand;
import com.dungeoneer.playerCharacter.application.port.in.CreateCharacterUseCase;
import com.dungeoneer.playerCharacter.domain.model.PlayerCharacter;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/characters")
public class PlayerCharacterController {

    private final CreateCharacterUseCase createCharacterUseCase;

    public PlayerCharacterController(CreateCharacterUseCase createCharacterUseCase) {
        this.createCharacterUseCase = createCharacterUseCase;
    }

    @PostMapping
    public ResponseEntity<CharacterResponseDto> createCharacter(@Valid @RequestBody CreatePlayerCharacterRequest request) {
        CreateCharacterCommand command = PlayerCharacterWebMapper.toCommand(request);
        PlayerCharacter character = createCharacterUseCase.createCharacter(command);
        CharacterResponseDto responseDto = PlayerCharacterWebMapper.toDto(character);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDto.id())
                .toUri();

        return ResponseEntity.created(location).body(responseDto);
    }
}
