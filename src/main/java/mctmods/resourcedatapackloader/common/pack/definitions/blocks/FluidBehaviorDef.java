package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import mctmods.resourcedatapackloader.common.cards.CardPlace;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public final class FluidBehaviorDef {
    public static final FluidBehaviorDef NONE = new FluidBehaviorDef(0, 0, false, Collections.emptyList(), Creatures.NONE, Items.NONE, Collections.emptyList());
    public final int flowTicks;
    public final int spread;
    public final boolean infinite;
    public final List<Row> byDimension;
    public final Creatures creatures;
    public final Items items;
    public final List<FluidReactionDef> reactions;

    public FluidBehaviorDef(int flowTicks, int spread, boolean infinite, List<Row> byDimension, Creatures creatures, Items items, List<FluidReactionDef> reactions) {
        this.flowTicks = flowTicks;
        this.spread = spread;
        this.infinite = infinite;
        this.byDimension = byDimension;
        this.creatures = creatures;
        this.items = items;
        this.reactions = reactions;
    }

    public int maxSpread() {
        int most = spread;
        for (Row row : byDimension) { most = Math.max(most, row.spread); }
        return most;
    }

    @Nullable public Row rowFor(int dimension) {
        for (Row row : byDimension) {
            if (CardPlace.inAny(row.dimensions, dimension)) { return row; }
        }
        return null;
    }

    public static final class Row {
        public final List<String> dimensions;
        public final int flowTicks;
        public final int spread;
        @Nullable public final Boolean infinite;

        public Row(List<String> dimensions, int flowTicks, int spread, @Nullable Boolean infinite) {
            this.dimensions = dimensions;
            this.flowTicks = flowTicks;
            this.spread = spread;
            this.infinite = infinite;
        }
    }

    public static final class Creatures {
        public static final Creatures NONE = new Creatures(null, 20, "generic", null, null, null, null, null);
        @Nullable public final Float damage;
        public final int damageEvery;
        public final String damageType;
        @Nullable public final Integer fireSeconds;
        @Nullable public final Boolean drown;
        @Nullable public final Boolean swim;
        @Nullable public final Float push;
        @Nullable public final Float drag;

        public Creatures(@Nullable Float damage, int damageEvery, String damageType, @Nullable Integer fireSeconds, @Nullable Boolean drown, @Nullable Boolean swim, @Nullable Float push, @Nullable Float drag) {
            this.damage = damage;
            this.damageEvery = damageEvery;
            this.damageType = damageType;
            this.fireSeconds = fireSeconds;
            this.drown = drown;
            this.swim = swim;
            this.push = push;
            this.drag = drag;
        }
    }

    public static final class Items {
        public static final Items NONE = new Items(null, 0.0F, Collections.emptyList(), null, null);
        @Nullable public final Boolean burn;
        public final float dissolve;
        public final List<String> immune;
        @Nullable public final Float drift;
        @Nullable public final Float buoyancy;

        public Items(@Nullable Boolean burn, float dissolve, List<String> immune, @Nullable Float drift, @Nullable Float buoyancy) {
            this.burn = burn;
            this.dissolve = dissolve;
            this.immune = immune;
            this.drift = drift;
            this.buoyancy = buoyancy;
        }
    }
}
