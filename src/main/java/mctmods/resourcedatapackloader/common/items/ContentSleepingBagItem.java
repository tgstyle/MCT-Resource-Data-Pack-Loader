package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.common.sleeping.ContentSleepingBags;
import mctmods.resourcedatapackloader.common.util.ContentEffects;

import javax.annotation.Nonnull;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public final class ContentSleepingBagItem extends Item {
    private final ItemDef def;

    public ContentSleepingBagItem(ItemDef def, Properties properties) {
        super(properties);
        this.def = def;
    }

    @Override @Nonnull public InteractionResult useOn(@Nonnull UseOnContext context) {
        if (context.getLevel().isClientSide() || !(context.getPlayer() instanceof ServerPlayer player)) { return InteractionResult.PASS; }
        InteractionResult result = ContentSleepingBags.use(player, context.getClickedPos(), context.getClickedFace());
        if (result.consumesAction()) { ContentEffects.cooldown(player, this, def.cooldown()); }
        return result;
    }
}
