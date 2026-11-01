package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.containers.ContentContainerUpgrades;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerUpgradeDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;

import javax.annotation.Nonnull;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class ContentContainerUpgradeItem extends Item {
    private final ContainerUpgradeDef upgrade;
    private final int cooldown;

    public ContentContainerUpgradeItem(ItemDef def, Item.Properties properties) {
        super(properties);
        this.upgrade = def.upgrade() == null ? ContainerUpgradeDef.PLAIN : def.upgrade();
        this.cooldown = def.cooldown();
    }

    @Override @Nonnull public InteractionResult onItemUseFirst(@Nonnull ItemStack stack, @Nonnull UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) { return InteractionResult.PASS; }
        InteractionResult result = ContentContainerUpgrades.use(upgrade, this, player, context.getHand(), context.getLevel(), context.getClickedPos(), context.getClickedFace());
        if (result != InteractionResult.SUCCESS || context.getLevel().isClientSide()) { return result; }
        if (upgrade.consume() && !player.getAbilities().instabuild) { stack.shrink(1); }
        if (cooldown > 0) { player.getCooldowns().addCooldown(this, cooldown); }
        return result;
    }
}
