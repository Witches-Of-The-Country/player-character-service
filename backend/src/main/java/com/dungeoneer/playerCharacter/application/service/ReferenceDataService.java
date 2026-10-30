package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.application.dto.ArchetypeDto;
import com.dungeoneer.playerCharacter.application.dto.BackgroundDto;
import com.dungeoneer.playerCharacter.application.dto.LineageDto;
import com.dungeoneer.playerCharacter.application.port.in.GetReferenceDataUseCase;
import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReferenceDataService implements GetReferenceDataUseCase {

    private final ReferenceDataRepository repository;

    public ReferenceDataService(ReferenceDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ArchetypeDto> getClasses(Ruleset ruleset) {
        return repository.findArchetypesByRuleset(ruleset)
                .stream()
                .map(ArchetypeDto::fromDomain)
                .toList();
    }

    @Override
    public List<LineageDto> getLineages(Ruleset ruleset) {
        return repository.findLineagesByRuleset(ruleset)
                .stream()
                .map(LineageDto::fromDomain)
                .toList();
    }

    @Override
    public List<BackgroundDto> getBackgrounds(Ruleset ruleset) {
        return repository.findBackgroundsByRuleset(ruleset)
                .stream()
                .map(BackgroundDto::fromDomain)
                .toList();
    }
}
