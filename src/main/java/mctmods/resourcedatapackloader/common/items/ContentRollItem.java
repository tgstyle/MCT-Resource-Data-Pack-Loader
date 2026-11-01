package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.board.ContentDice;
import mctmods.resourcedatapackloader.common.board.ContentTurns;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.common.util.ContentEffects;

import javax.annotation.Nonnull;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class ContentRollItem extends Item {
    private final ItemDef def;

    public ContentRollItem(ItemDef def, Properties properties) {
        super(properties);
        this.def = def;
    }

    @Override @Nonnull public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        if (!level.isClientSide() && player instanceof ServerPlayer server) {
            if (def.passesTurn()) { ContentTurns.byItem(server); }
            else { ContentDice.byItem(server, def.rolls(), def.rollStore()); }
        }
        ContentEffects.cooldown(player, this, def.cooldown());
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
