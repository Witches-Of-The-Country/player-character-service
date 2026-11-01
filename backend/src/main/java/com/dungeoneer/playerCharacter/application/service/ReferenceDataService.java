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
    public CursorPageResponse<SpellDto> getSpells(String cursor, int limit, String q, Ruleset ruleset, Integer level, String school, UUID ownerId) {
        CursorData cursorData = decodeCursor(cursor);
        int fetchLimit = limit + 1;

        List<SpellJpaEntity> results = repository.findSpellsWithCursor(
                ruleset,
                level,
                school,
                q,
                cursorData.afterName(),
                cursorData.afterId(),
                fetchLimit,
                ownerId
        );

        boolean hasMore = results.size() > limit;
        List<SpellJpaEntity> pageItems = hasMore ? results.subList(0, limit) : results;

        String nextCursor = null;
        if (hasMore && !pageItems.isEmpty()) {
            SpellJpaEntity last = pageItems.get(pageItems.size() - 1);
            nextCursor = encodeCursor(last.getName(), last.getId());
        }

        List<SpellDto> dtos = pageItems.stream().map(SpellDto::fromEntity).toList();
        return new CursorPageResponse<>(dtos, nextCursor, hasMore, pageItems.size());
    }

    @Override
    public CursorPageResponse<FeatDto> getFeats(String cursor, int limit, String q, Ruleset ruleset, UUID ownerId) {
        CursorData cursorData = decodeCursor(cursor);
        int fetchLimit = limit + 1;

        List<FeatJpaEntity> results = repository.findFeatsWithCursor(
                ruleset,
                q,
                cursorData.afterName(),
                cursorData.afterId(),
                fetchLimit,
                ownerId
        );

        boolean hasMore = results.size() > limit;
        List<FeatJpaEntity> pageItems = hasMore ? results.subList(0, limit) : results;

        String nextCursor = null;
        if (hasMore && !pageItems.isEmpty()) {
            FeatJpaEntity last = pageItems.get(pageItems.size() - 1);
            nextCursor = encodeCursor(last.getName(), last.getId());
        }

        List<FeatDto> dtos = pageItems.stream().map(FeatDto::fromEntity).toList();
        return new CursorPageResponse<>(dtos, nextCursor, hasMore, pageItems.size());
    }

    @Override
    public CursorPageResponse<ItemDto> getItems(String cursor, int limit, String q, Ruleset ruleset, String category, String rarity, UUID ownerId) {
        CursorData cursorData = decodeCursor(cursor);
        int fetchLimit = limit + 1;

        List<Item> results = repository.findItemsWithCursor(
                ruleset,
                category,
                rarity,
                q,
                cursorData.afterName(),
                cursorData.afterId(),
                fetchLimit,
                ownerId
        );

        boolean hasMore = results.size() > limit;
        List<Item> pageItems = hasMore ? results.subList(0, limit) : results;

        String nextCursor = null;
        if (hasMore && !pageItems.isEmpty()) {
            Item last = pageItems.get(pageItems.size() - 1);
            nextCursor = encodeCursor(last.getName(), last.getId());
        }

        List<ItemDto> dtos = pageItems.stream().map(ItemDto::fromDomain).toList();
        return new CursorPageResponse<>(dtos, nextCursor, hasMore, pageItems.size());
    }

    @Override
    public CursorPageResponse<ArchetypeDto> getClassesPaginated(String cursor, int limit, String q, Ruleset ruleset, UUID ownerId) {
        CursorData cursorData = decodeCursor(cursor);
        int fetchLimit = limit + 1;

        List<Archetype> results = repository.findArchetypesWithCursor(
                ruleset,
                q,
                cursorData.afterName(),
                cursorData.afterId(),
                fetchLimit,
                ownerId
        );

        boolean hasMore = results.size() > limit;
        List<Archetype> pageItems = hasMore ? results.subList(0, limit) : results;

        String nextCursor = null;
        if (hasMore && !pageItems.isEmpty()) {
            Archetype last = pageItems.get(pageItems.size() - 1);
            nextCursor = encodeCursor(last.getName(), last.getId());
        }

        List<ArchetypeDto> dtos = pageItems.stream().map(ArchetypeDto::fromDomain).toList();
        return new CursorPageResponse<>(dtos, nextCursor, hasMore, pageItems.size());
    }

    @Override
    public CursorPageResponse<LineageDto> getLineagesPaginated(String cursor, int limit, String q, Ruleset ruleset, UUID ownerId) {
        CursorData cursorData = decodeCursor(cursor);
        int fetchLimit = limit + 1;

        List<Lineage> results = repository.findLineagesWithCursor(
                ruleset,
                q,
                cursorData.afterName(),
                cursorData.afterId(),
                fetchLimit,
                ownerId
        );

        boolean hasMore = results.size() > limit;
        List<Lineage> pageItems = hasMore ? results.subList(0, limit) : results;

        String nextCursor = null;
        if (hasMore && !pageItems.isEmpty()) {
            Lineage last = pageItems.get(pageItems.size() - 1);
            nextCursor = encodeCursor(last.getName(), last.getId());
        }

        List<LineageDto> dtos = pageItems.stream().map(LineageDto::fromDomain).toList();
        return new CursorPageResponse<>(dtos, nextCursor, hasMore, pageItems.size());
    }

    private String encodeCursor(String name, UUID id) {
        String raw = name + ":" + id;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    private CursorData decodeCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return new CursorData(null, null);
        }
        try {
            byte[] bytes = Base64.getUrlDecoder().decode(cursor);
            String raw = new String(bytes, StandardCharsets.UTF_8);
            int lastColonIndex = raw.lastIndexOf(':');
            if (lastColonIndex == -1) {
                return new CursorData(null, null);
            }
            String name = raw.substring(0, lastColonIndex);
            UUID id = UUID.fromString(raw.substring(lastColonIndex + 1));
            return new CursorData(name, id);
        } catch (Exception e) {
            return new CursorData(null, null);
        }
    }

    private record CursorData(String afterName, UUID afterId) {}
}
