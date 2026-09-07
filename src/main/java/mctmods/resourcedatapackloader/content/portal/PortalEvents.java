package mctmods.resourcedatapackloader.content.portal;

import mctmods.resourcedatapackloader.content.block.ContentPortalBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public final class PortalEvents {
    private PortalEvents() {}

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { ContentPortalBlock.forget(event.getEntity().getUUID()); }

    public static void onBroken(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof Level level) { ContentPortals.shaken(level, event.getPos(), event.getState()); }
    }

    public static void onLit(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        ItemStack held = event.getItemStack();
        if (held.isEmpty()) { return; }
        BlockPos clicked = event.getPos();
        if (level.isClientSide()) {
            if (ContentPortals.find(level, clicked, event.getFace(), held) == null) { return; }
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }
        if (!ContentPortals.light(level, clicked, event.getFace(), held)) { return; }
        Player player = event.getEntity();
        level.playSound(null, clicked, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 0.8F);
        if (held.isDamageableItem()) { held.hurtAndBreak(1, player, LivingEntity.getSlotForHand(event.getHand())); }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }
}
