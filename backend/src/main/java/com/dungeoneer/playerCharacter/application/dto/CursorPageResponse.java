package com.dungeoneer.playerCharacter.application.dto;

import java.util.List;

public record CursorPageResponse<T>(
        List<T> items,
        String nextCursor,
        boolean hasNext,
        int pageSize
) {
}
