package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.environment.ContentSeasons;
import mctmods.resourcedatapackloader.common.environment.SeasonState;
import mctmods.resourcedatapackloader.common.pack.definitions.items.SeasonClockDef;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class ContentSeasonClock {
    private ContentSeasonClock() {}

    public static float year(SeasonClockDef def, ItemStack stack, @Nullable Level level, @Nullable LivingEntity holder) {
        SeasonState state = state(def, stack, level, holder);
        return state == null ? 0.0F : state.yearProgress();
    }

    public static float season(SeasonClockDef def, ItemStack stack, @Nullable Level level, @Nullable LivingEntity holder) {
        SeasonState state = state(def, stack, level, holder);
        return state == null ? 0.0F : (float) state.index();
    }

    public static void tell(SeasonClockDef def, Player player) {
        SeasonState state = ContentSeasons.state(def.dimension(), player.level().dimension().location());
        if (state == null) { player.displayClientMessage(Component.translatable("rdpl.season.none"), true); }
        else { player.displayClientMessage(Component.translatable("rdpl.season.now", state.name(), state.dayInSeason() + 1, state.daysPerSeason()), true); }
    }

    @Nullable private static SeasonState state(SeasonClockDef def, ItemStack stack, @Nullable Level level, @Nullable LivingEntity holder) {
        Entity entity = holder != null ? holder : stack.getEntityRepresentation();
        Level where = level != null ? level : entity == null ? null : entity.level();
        return where == null ? null : ContentSeasons.state(def.dimension(), where.dimension().location());
    }
}
