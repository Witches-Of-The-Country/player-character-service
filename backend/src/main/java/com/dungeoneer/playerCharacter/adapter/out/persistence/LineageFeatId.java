package com.dungeoneer.playerCharacter.adapter.out.persistence;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class LineageFeatId implements Serializable {
    private UUID lineageId;
    private UUID featId;

    public LineageFeatId() {}

    public LineageFeatId(UUID lineageId, UUID featId) {
        this.lineageId = lineageId;
        this.featId = featId;
    }

    public UUID getLineageId() {
        return lineageId;
    }

    public void setLineageId(UUID lineageId) {
        this.lineageId = lineageId;
    }

    public UUID getFeatId() {
        return featId;
    }

    public void setFeatId(UUID featId) {
        this.featId = featId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineageFeatId that = (LineageFeatId) o;
        return Objects.equals(lineageId, that.lineageId) && Objects.equals(featId, that.featId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineageId, featId);
    }
}
