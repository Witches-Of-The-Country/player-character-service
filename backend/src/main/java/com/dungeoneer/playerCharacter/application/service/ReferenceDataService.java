package com.dungeoneer.playerCharacter.application.service;

import com.dungeoneer.playerCharacter.adapter.out.persistence.FeatJpaEntity;
import com.dungeoneer.playerCharacter.adapter.out.persistence.SpellJpaEntity;
import com.dungeoneer.playerCharacter.application.dto.ArchetypeDto;
import com.dungeoneer.playerCharacter.application.dto.BackgroundDto;
import com.dungeoneer.playerCharacter.application.dto.CursorPageResponse;
import com.dungeoneer.playerCharacter.application.dto.FeatDto;
import com.dungeoneer.playerCharacter.application.dto.ItemDto;
import com.dungeoneer.playerCharacter.application.dto.LineageDto;
import com.dungeoneer.playerCharacter.application.dto.SpellDto;
import com.dungeoneer.playerCharacter.application.port.in.GetReferenceDataUseCase;
import com.dungeoneer.playerCharacter.application.port.out.ReferenceDataRepository;
import com.dungeoneer.playerCharacter.domain.model.Archetype;
import com.dungeoneer.playerCharacter.domain.model.Item;
import com.dungeoneer.playerCharacter.domain.model.Lineage;
import com.dungeoneer.playerCharacter.domain.model.Ruleset;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

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

    @Override
    public CursorPageResponse<SpellDto> getSpells(String cursor, int limit, String q, Ruleset ruleset, Integer level, String school) {
        CursorParts cp = decodeCursor(cursor);
        int fetchLimit = limit + 1;
        List<SpellJpaEntity> fetched = repository.findSpellsWithCursor(ruleset, level, school, q, cp.name(), cp.id(), fetchLimit);

        boolean hasNext = fetched.size() > limit;
        List<SpellJpaEntity> pageItems = hasNext ? fetched.subList(0, limit) : fetched;
        String nextCursor = null;
        if (hasNext && !pageItems.isEmpty()) {
            SpellJpaEntity last = pageItems.get(pageItems.size() - 1);
            nextCursor = encodeCursor(last.getName(), last.getId());
        }

        List<SpellDto> dtoList = pageItems.stream().map(SpellDto::fromEntity).toList();
        return new CursorPageResponse<>(dtoList, nextCursor, hasNext, pageItems.size());
    }

    @Override
    public CursorPageResponse<FeatDto> getFeats(String cursor, int limit, String q, Ruleset ruleset) {
        CursorParts cp = decodeCursor(cursor);
        int fetchLimit = limit + 1;
        List<FeatJpaEntity> fetched = repository.findFeatsWithCursor(ruleset, q, cp.name(), cp.id(), fetchLimit);

        boolean hasNext = fetched.size() > limit;
        List<FeatJpaEntity> pageItems = hasNext ? fetched.subList(0, limit) : fetched;
        String nextCursor = null;
        if (hasNext && !pageItems.isEmpty()) {
            FeatJpaEntity last = pageItems.get(pageItems.size() - 1);
            nextCursor = encodeCursor(last.getName(), last.getId());
        }

        List<FeatDto> dtoList = pageItems.stream().map(FeatDto::fromEntity).toList();
        return new CursorPageResponse<>(dtoList, nextCursor, hasNext, pageItems.size());
    }

    @Override
    public CursorPageResponse<ItemDto> getItems(String cursor, int limit, String q, Ruleset ruleset, String category, String rarity) {
        CursorParts cp = decodeCursor(cursor);
        int fetchLimit = limit + 1;
        List<Item> fetched = repository.findItemsWithCursor(ruleset, category, rarity, q, cp.name(), cp.id(), fetchLimit);

        boolean hasNext = fetched.size() > limit;
        List<Item> pageItems = hasNext ? fetched.subList(0, limit) : fetched;
        String nextCursor = null;
        if (hasNext && !pageItems.isEmpty()) {
            Item last = pageItems.get(pageItems.size() - 1);
            nextCursor = encodeCursor(last.getName(), last.getId());
        }

        List<ItemDto> dtoList = pageItems.stream().map(ItemDto::fromDomain).toList();
        return new CursorPageResponse<>(dtoList, nextCursor, hasNext, pageItems.size());
    }

    @Override
    public CursorPageResponse<ArchetypeDto> getClassesPaginated(String cursor, int limit, String q, Ruleset ruleset) {
        CursorParts cp = decodeCursor(cursor);
        int fetchLimit = limit + 1;
        List<Archetype> fetched = repository.findArchetypesWithCursor(ruleset, q, cp.name(), cp.id(), fetchLimit);

        boolean hasNext = fetched.size() > limit;
        List<Archetype> pageItems = hasNext ? fetched.subList(0, limit) : fetched;
        String nextCursor = null;
        if (hasNext && !pageItems.isEmpty()) {
            Archetype last = pageItems.get(pageItems.size() - 1);
            nextCursor = encodeCursor(last.getName(), last.getId());
        }

        List<ArchetypeDto> dtoList = pageItems.stream().map(ArchetypeDto::fromDomain).toList();
        return new CursorPageResponse<>(dtoList, nextCursor, hasNext, pageItems.size());
    }

    @Override
    public CursorPageResponse<LineageDto> getLineagesPaginated(String cursor, int limit, String q, Ruleset ruleset) {
        CursorParts cp = decodeCursor(cursor);
        int fetchLimit = limit + 1;
        List<Lineage> fetched = repository.findLineagesWithCursor(ruleset, q, cp.name(), cp.id(), fetchLimit);

        boolean hasNext = fetched.size() > limit;
        List<Lineage> pageItems = hasNext ? fetched.subList(0, limit) : fetched;
        String nextCursor = null;
        if (hasNext && !pageItems.isEmpty()) {
            Lineage last = pageItems.get(pageItems.size() - 1);
            nextCursor = encodeCursor(last.getName(), last.getId());
        }

        List<LineageDto> dtoList = pageItems.stream().map(LineageDto::fromDomain).toList();
        return new CursorPageResponse<>(dtoList, nextCursor, hasNext, pageItems.size());
    }

    private String encodeCursor(String name, UUID id) {
        String raw = (name != null ? name : "") + "::" + id.toString();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    private CursorParts decodeCursor(String cursor) {
        if (cursor == null || cursor.trim().isEmpty()) {
            return new CursorParts(null, null);
        }
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(cursor.trim());
            String text = new String(decoded, StandardCharsets.UTF_8);
            String[] parts = text.split("::", 2);
            if (parts.length == 2) {
                return new CursorParts(parts[0], UUID.fromString(parts[1]));
            }
            return new CursorParts(null, UUID.fromString(parts[0]));
        } catch (Exception e) {
            return new CursorParts(null, null);
        }
    }

    private record CursorParts(String name, UUID id) {}
}
