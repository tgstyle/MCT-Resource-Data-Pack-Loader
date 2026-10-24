package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.ContentSleepingBags;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.util.ContentEffects;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import javax.annotation.Nonnull;

public final class ContentSleepingBagItem extends Item {
    private final ItemDef def;

    public ContentSleepingBagItem(ItemDef def, Properties properties) {
        super(properties);
        this.def = def;
    }

    @Override @Nonnull public InteractionResult useOn(@Nonnull UseOnContext context) {
        if (context.getLevel().isClientSide() || !(context.getPlayer() instanceof ServerPlayer player)) { return InteractionResult.PASS; }
        InteractionResult result = ContentSleepingBags.use(player, context.getClickedPos(), context.getClickedFace());
        if (result.consumesAction()) { ContentEffects.cooldown(player, context.getItemInHand(), def.cooldown()); }
        return result;
    }
}
