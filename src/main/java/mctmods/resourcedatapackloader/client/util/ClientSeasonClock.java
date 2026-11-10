package mctmods.resourcedatapackloader.client.util;

import mctmods.resourcedatapackloader.common.environment.ContentSeasons;
import mctmods.resourcedatapackloader.common.environment.SeasonState;
import mctmods.resourcedatapackloader.common.pack.definitions.items.SeasonClockDef;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class ClientSeasonClock {
    private ClientSeasonClock() {}

    public static ClampedItemPropertyFunction year(SeasonClockDef def) {
        return (stack, level, holder, seed) -> {
            SeasonState state = state(def, stack, level, holder);
            return state == null ? 0.0F : state.yearProgress();
        };
    }

    public static ClampedItemPropertyFunction season(SeasonClockDef def) {
        return (stack, level, holder, seed) -> {
            SeasonState state = state(def, stack, level, holder);
            return state == null ? 0.0F : (float) state.index();
        };
    }

    @Nullable private static SeasonState state(SeasonClockDef def, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity holder) {
        Entity entity = holder != null ? holder : stack.getEntityRepresentation();
        Level where = level != null ? level : entity == null ? null : entity.level();
        return where == null ? null : ContentSeasons.state(def.dimension(), where.dimension().location());
    }
}
