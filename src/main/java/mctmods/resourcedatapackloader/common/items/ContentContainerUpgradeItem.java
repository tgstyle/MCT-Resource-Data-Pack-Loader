package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.containers.ContentContainerUpgrades;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerUpgradeDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContentContainerUpgradeItem extends ContentItem {
    private final ContainerUpgradeDef upgrade;
    private final int cooldown;

    public ContentContainerUpgradeItem(ItemDef def) {
        super(def);
        this.upgrade = def.upgrade == null ? ContainerUpgradeDef.PLAIN : def.upgrade;
        this.cooldown = def.cooldown;
    }

    @Override @Nonnull public EnumActionResult onItemUseFirst(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ, @Nonnull EnumHand hand) {
        EnumActionResult result = ContentContainerUpgrades.use(upgrade, this, player, hand, world, pos, side);
        if (result != EnumActionResult.SUCCESS || world.isRemote) { return result; }
        if (upgrade.consume && !player.capabilities.isCreativeMode) { player.getHeldItem(hand).shrink(1); }
        if (cooldown > 0) { player.getCooldownTracker().setCooldown(this, cooldown); }
        return result;
    }
}
