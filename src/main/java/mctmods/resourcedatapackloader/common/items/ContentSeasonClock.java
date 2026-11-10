package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.environment.ContentSeasons;
import mctmods.resourcedatapackloader.common.environment.SeasonState;
import mctmods.resourcedatapackloader.common.pack.definitions.items.SeasonClockDef;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public final class ContentSeasonClock {
    private ContentSeasonClock() {}

    public static IItemPropertyGetter year(SeasonClockDef def) {
        return (stack, world, holder) -> {
            SeasonState state = state(def, stack, world, holder);
            return state == null ? 0.0F : state.yearProgress();
        };
    }

    public static IItemPropertyGetter season(SeasonClockDef def) {
        return (stack, world, holder) -> {
            SeasonState state = state(def, stack, world, holder);
            return state == null ? 0.0F : (float) state.index();
        };
    }

    public static void tell(SeasonClockDef def, EntityPlayer player) {
        SeasonState state = ContentSeasons.state(def.dimension, player.dimension);
        if (state == null) { player.sendStatusMessage(new TextComponentTranslation("rdpl.season.none"), true); }
        else { player.sendStatusMessage(new TextComponentTranslation("rdpl.season.now", state.name(), state.dayInSeason() + 1, state.daysPerSeason()), true); }
    }

    @Nullable private static SeasonState state(SeasonClockDef def, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase holder) {
        Entity entity = holder != null ? holder : stack.getItemFrame();
        World where = world != null ? world : entity == null ? null : entity.world;
        return where == null ? null : ContentSeasons.state(def.dimension, where.provider.getDimension());
    }
}
