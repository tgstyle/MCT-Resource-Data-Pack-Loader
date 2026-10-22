package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.level.Level;
import javax.annotation.Nonnull;

public record DayClock(Level level, ClockManager inner, long period) implements ClockManager {
    @Override public long getTotalTicks(@Nonnull Holder<WorldClock> definition) { return ContentRuleSwitch.stretch(level, inner.getTotalTicks(definition), period); }

    @Override public float getPartialTick(@Nonnull Holder<WorldClock> definition) { return inner.getPartialTick(definition) * ContentRuleSwitch.scale(level, period); }

    @Override public void setRate(@Nonnull Holder<WorldClock> definition, float rate) { inner.setRate(definition, rate); }
}
