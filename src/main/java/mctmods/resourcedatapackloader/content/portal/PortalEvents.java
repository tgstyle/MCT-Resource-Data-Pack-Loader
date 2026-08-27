package mctmods.resourcedatapackloader.content.portal;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class PortalEvents {
    private PortalEvents() {}

    @SubscribeEvent public static void onBroken(BlockEvent.BreakEvent event) { ContentPortals.shaken(event.getWorld(), event.getPos(), event.getState()); }

    @SubscribeEvent public static void onLit(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        ItemStack held = event.getItemStack();
        if (held.isEmpty()) { return; }
        BlockPos clicked = event.getPos();
        if (world.isRemote) {
            if (ContentPortals.find(world, clicked, event.getFace(), held) == null) { return; }
            event.setCanceled(true);
            event.setCancellationResult(EnumActionResult.SUCCESS);
            return;
        }
        if (!ContentPortals.light(world, clicked, event.getFace(), held)) { return; }
        EntityPlayer player = event.getEntityPlayer();
        world.playSound(null, clicked, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
        if (held.isItemStackDamageable()) { held.damageItem(1, player); }
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
    }
}
