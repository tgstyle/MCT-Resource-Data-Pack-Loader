package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.ContentDice;
import mctmods.resourcedatapackloader.content.ContentTurns;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.util.ContentEffects;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import javax.annotation.Nonnull;

public final class ContentRollItem extends Item {
    private final ItemDef def;

    public ContentRollItem(ItemDef def, Properties properties) {
        super(properties);
        this.def = def;
    }

    @Override @Nonnull public InteractionResult use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        if (!level.isClientSide() && player instanceof ServerPlayer server) {
            if (def.passesTurn()) { ContentTurns.byItem(server); }
            else { ContentDice.byItem(server, def.rolls(), def.rollStore()); }
        }
        ContentEffects.cooldown(player, player.getItemInHand(hand), def.cooldown());
        return InteractionResult.SUCCESS;
    }
}
