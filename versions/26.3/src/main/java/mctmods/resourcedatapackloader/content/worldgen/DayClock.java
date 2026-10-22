package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.world.clock.ClockInstance;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.level.Level;
import javax.annotation.Nonnull;

public record DayClock(Level level, ClockManager inner, long period) implements ClockManager {
    private record Stretched(Level level, ClockInstance base, long period) implements ClockInstance {
        @Override public long totalTicks() { return ContentRuleSwitch.stretch(level, base.totalTicks(), period); }

        @Override public float partialTick() { return base.partialTick() * ContentRuleSwitch.scale(level, period); }

        @Override public float rate() { return base.rate() * ContentRuleSwitch.scale(level, period); }

        @Override public boolean isPaused() { return base.isPaused(); }
    }

    @Override @Nonnull public ClockInstance getInstance(@Nonnull Holder<WorldClock> definition) { return new Stretched(level, inner.getInstance(definition), period); }

    @Override public void setRate(@Nonnull Holder<WorldClock> definition, float rate) { inner.setRate(definition, rate); }
}
