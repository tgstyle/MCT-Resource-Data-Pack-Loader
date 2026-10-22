package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.level.Level;
import javax.annotation.Nonnull;

public record LevelClock(Level level, ClockManager inner) implements ClockManager {
    @Override public long getTotalTicks(@Nonnull Holder<WorldClock> definition) { return inner.getTotalTicks(definition); }

    @Override public float getPartialTick(@Nonnull Holder<WorldClock> definition) { return inner.getPartialTick(definition); }

    @Override public void setRate(@Nonnull Holder<WorldClock> definition, float rate) { inner.setRate(definition, rate); }
}
