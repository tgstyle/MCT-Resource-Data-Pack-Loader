package mctmods.resourcedatapackloader.common.raids;

import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;

public record RaidTarget(Kind kind, BlockPos center, int radius, int ownerHandle, @Nullable UUID targetUuid, boolean free, int score) {
    public enum Kind {
        VILLAGE, BASE, PLAYER, FREE;

        public String key() { return name().toLowerCase(Locale.ROOT); }

        public static Kind parse(@Nullable String value) {
            if (value == null) { return VILLAGE; }
            for (Kind kind : values()) {
                if (kind.key().equals(value)) { return kind; }
            }
            return VILLAGE;
        }
    }
}
