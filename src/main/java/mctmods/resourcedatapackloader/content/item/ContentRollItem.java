package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.ContentDice;
import mctmods.resourcedatapackloader.content.def.ItemDef;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import javax.annotation.Nonnull;

public class ContentRollItem extends Item {
    private final ItemDef def;

    public ContentRollItem(ItemDef def, Properties properties) {
        super(properties);
        this.def = def;
    }

    @Override @Nonnull public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        if (!level.isClientSide() && player instanceof ServerPlayer server) { ContentDice.byItem(server, def.rolls()); }
        if (def.cooldown() > 0) { player.getCooldowns().addCooldown(this, def.cooldown()); }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
