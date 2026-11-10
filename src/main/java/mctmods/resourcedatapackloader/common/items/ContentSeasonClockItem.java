package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.environment.ContentSeasons;
import mctmods.resourcedatapackloader.common.environment.SeasonState;
import mctmods.resourcedatapackloader.common.pack.definitions.items.SeasonClockDef;

import javax.annotation.Nonnull;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public final class ContentSeasonClockItem extends Item {
    private final SeasonClockDef def;

    public ContentSeasonClockItem(SeasonClockDef def, Properties properties) {
        super(properties);
        this.def = def;
    }

    public SeasonClockDef clock() { return def; }

    @Override @Nonnull public InteractionResult use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        if (!def.message()) { return super.use(level, player, hand); }
        if (!level.isClientSide() && player instanceof ServerPlayer server) {
            SeasonState state = ContentSeasons.state(def.dimension(), level.dimension().identifier());
            if (state == null) { server.sendOverlayMessage(Component.translatable("rdpl.season.none")); }
            else { server.sendOverlayMessage(Component.translatable("rdpl.season.now", state.name(), state.dayInSeason() + 1, state.daysPerSeason())); }
        }
        return InteractionResult.SUCCESS;
    }
}
