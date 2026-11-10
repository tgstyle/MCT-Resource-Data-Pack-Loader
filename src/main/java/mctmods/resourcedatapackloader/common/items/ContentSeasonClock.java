package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.environment.ContentSeasons;
import mctmods.resourcedatapackloader.common.environment.SeasonState;
import mctmods.resourcedatapackloader.common.pack.definitions.items.SeasonClockDef;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public final class ContentSeasonClock {
    private ContentSeasonClock() {}

    public static void tell(SeasonClockDef def, Player player) {
        SeasonState state = ContentSeasons.state(def.dimension(), player.level().dimension().location());
        if (state == null) { player.displayClientMessage(Component.translatable("rdpl.season.none"), true); }
        else { player.displayClientMessage(Component.translatable("rdpl.season.now", state.name(), state.dayInSeason() + 1, state.daysPerSeason()), true); }
    }
}
