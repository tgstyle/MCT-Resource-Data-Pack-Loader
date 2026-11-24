package mctmods.resourcedatapackloader.common.raids;

import java.util.Collection;
import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;

public final class RaidTarget {
    public enum Kind {
        VILLAGE, BASE, PLAYER, FREE;

        public String key() {
            return name().toLowerCase(Locale.ROOT);
        }

        public boolean guarded() {
            return this == BASE || this == PLAYER;
        }

        public static boolean anyGuarded(Collection<Kind> kinds) {
            for (Kind kind : kinds) {
                if (kind.guarded()) {
                    return true;
                }
            }
            return false;
        }

        public static Kind parse(@Nullable String value) {
            if (value == null) {
                return VILLAGE;
            }
            for (Kind kind : values()) {
                if (kind.key().equals(value)) {
                    return kind;
                }
            }
            return VILLAGE;
        }
    }

    private final Kind kind;
    private final BlockPos center;
    private final int radius;
    private final int ownerHandle;
    @Nullable private final UUID targetUuid;
    private final int score;

    public RaidTarget(Kind kind, BlockPos center, int radius, int ownerHandle, @Nullable UUID targetUuid, int score) {
        this.kind = kind;
        this.center = center;
        this.radius = radius;
        this.ownerHandle = ownerHandle;
        this.targetUuid = targetUuid;
        this.score = score;
    }

    public Kind kind() {
        return kind;
    }

    public BlockPos center() {
        return center;
    }

    public int radius() {
        return radius;
    }

    public int ownerHandle() {
        return ownerHandle;
    }

    @Nullable
    public UUID targetUuid() {
        return targetUuid;
    }

    public int score() { return score; }
}
