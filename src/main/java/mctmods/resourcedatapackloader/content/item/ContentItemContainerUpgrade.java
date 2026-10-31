package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.ContentContainerUpgrades;
import mctmods.resourcedatapackloader.content.def.ContainerUpgradeDef;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.util.ContentEffects;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import javax.annotation.Nonnull;

public final class ContentItemContainerUpgrade extends Item {
    private final ItemDef def;
    private final ContainerUpgradeDef upgrade;

    public ContentItemContainerUpgrade(ItemDef def, Properties properties) {
        super(properties);
        this.def = def;
        this.upgrade = def.upgrade() == null ? ContainerUpgradeDef.PLAIN : def.upgrade();
    }

    @Override @Nonnull public InteractionResult onItemUseFirst(@Nonnull ItemStack stack, @Nonnull UseOnContext context) {
        InteractionResult result = ContentContainerUpgrades.use(upgrade, this, context);
        if (result != InteractionResult.SUCCESS || context.getLevel().isClientSide() || !(context.getPlayer() instanceof ServerPlayer player)) { return result; }
        ContentEffects.cooldown(player, stack, def.cooldown());
        if (upgrade.consume() && !player.getAbilities().instabuild) { stack.shrink(1); }
        return result;
    }
}
