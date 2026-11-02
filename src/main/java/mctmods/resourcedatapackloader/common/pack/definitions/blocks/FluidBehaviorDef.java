package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import mctmods.resourcedatapackloader.common.cards.CardPlace;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;

public record FluidBehaviorDef(int flowTicks, int spread, boolean infinite, List<Row> byDimension, Creatures creatures, Items items, List<FluidReactionDef> reactions) {
    public static final FluidBehaviorDef NONE = new FluidBehaviorDef(0, 0, false, Collections.emptyList(), Creatures.NONE, Items.NONE, Collections.emptyList());

    public int maxSpread() {
        int most = spread;
        for (Row row : byDimension) { most = Math.max(most, row.spread()); }
        return most;
    }

    @Nullable public Row rowFor(Level level) {
        for (Row row : byDimension) {
            if (CardPlace.inAny(row.dimensions(), level)) { return row; }
        }
        return null;
    }

    public record Row(List<String> dimensions, int flowTicks, int spread, @Nullable Boolean infinite) {}

    public record Creatures(@Nullable Float damage, int damageEvery, String damageType, @Nullable Integer fireSeconds, @Nullable Boolean drown, @Nullable Boolean swim, @Nullable Float push, @Nullable Float drag) {
        public static final Creatures NONE = new Creatures(null, 20, "generic", null, null, null, null, null);
    }

    public record Items(@Nullable Boolean burn, float dissolve, List<String> immune, @Nullable Float drift, @Nullable Float buoyancy) {
        public static final Items NONE = new Items(null, 0.0F, Collections.emptyList(), null, null);
    }
}
