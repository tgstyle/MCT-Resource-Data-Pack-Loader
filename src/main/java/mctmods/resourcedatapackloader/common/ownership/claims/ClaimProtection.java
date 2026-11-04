package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.util.ItemUseGate;
import mctmods.resourcedatapackloader.common.util.world.PlacePositions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecartContainer;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.transfer.access.ItemAccess;

public final class ClaimProtection {
    private ClaimProtection() {}

    public static void onBreak(BreakBlockEvent event) { ItemUseGate.cancel(event, ClaimSettings.protectBreak() && refuse(event.getPlayer(), event.getPos())); }

    public static void onDig(PlayerInteractEvent.LeftClickBlock event) { ItemUseGate.cancel(event, ClaimSettings.protectBreak() && refuse(event.getEntity(), event.getPos())); }

    public static void onAttack(AttackEntityEvent event) {
        Entity target = event.getTarget();
        ItemUseGate.cancel(event, ClaimSettings.protectBreak() && (target instanceof HangingEntity || target instanceof ArmorStand) && refuse(event.getEntity(), target.blockPosition()));
    }

    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (ClaimSettings.placeOpen() || !(event.getEntity() instanceof Player player) || ClaimSettings.isClaimBlock(event.getPlacedBlock().getBlock())) { return; }
        for (BlockPos pos : PlacePositions.of(event)) {
            if (refuse(player, pos)) {
                event.setCanceled(true);
                return;
            }
        }
    }

    public static void onBucketBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack held = event.getItemStack();
        if (ClaimSettings.placeOpen() || carriesNoFluid(event) || ClaimSettings.isClaimItem(held) || ClaimSettings.isClaimBlock(event.getLevel().getBlockState(event.getPos()).getBlock())) { return; }
        Direction face = event.getFace();
        if (refuse(event.getEntity(), face == null ? event.getPos() : event.getPos().relative(face))) { deny(event); }
    }

    public static void onBucketItem(PlayerInteractEvent.RightClickItem event) {
        if (ClaimSettings.placeOpen() || carriesNoFluid(event) || ClaimSettings.isClaimItem(event.getItemStack())) { return; }
        Player player = event.getEntity();
        if (!(player.pick(player.blockInteractionRange(), 1.0F, true) instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) { return; }
        if (refuse(player, hit.getBlockPos()) || refuse(player, hit.getBlockPos().relative(hit.getDirection()))) { event.setCanceled(true); }
    }

    public static void onUseBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!ClaimSettings.protectUse() || !ClaimSettings.useProtects(event.getLevel().getBlockState(event.getPos()).getBlock())) { return; }
        if (refuse(event.getEntity(), event.getPos())) { deny(event); }
    }

    public static void onUseEntity(PlayerInteractEvent.EntityInteract event) { ItemUseGate.cancel(event, refusesUse(event.getEntity(), event.getTarget())); }

    public static boolean refusesUse(Player player, Entity target) {
        boolean holds = target instanceof ItemFrame || target instanceof ArmorStand || target instanceof AbstractMinecartContainer;
        return ClaimSettings.protectUse() && holds && refuse(player, target.blockPosition());
    }

    public static void onHurt(LivingIncomingDamageEvent event) {
        Entity source = event.getSource().getEntity();
        if (!ClaimSettings.protectPvp() || !(event.getEntity() instanceof ServerPlayer) || !(source instanceof ServerPlayer attacker) || source == event.getEntity()) { return; }
        int claim = ClaimGuard.covers(event.getEntity().level(), event.getEntity().blockPosition());
        if (claim == 0 || ClaimOwners.bypasses(attacker)) { return; }
        event.setCanceled(true);
        ClaimMessages.deny(attacker, claim);
    }

    private static boolean refuse(Player player, BlockPos pos) {
        if (!(player instanceof ServerPlayer served) || ClaimGuard.passes(served, served.level(), pos)) { return false; }
        ClaimMessages.deny(served, ClaimGuard.covers(served.level(), pos));
        return true;
    }

    static void deny(PlayerInteractEvent.RightClickBlock event) {
        event.setCanceled(true);
        event.setUseBlock(TriState.FALSE);
        event.setUseItem(TriState.FALSE);
    }

    private static boolean carriesNoFluid(PlayerInteractEvent event) { return !(event.getItemStack().getItem() instanceof BucketItem) && ItemAccess.forPlayerInteraction(event.getEntity(), event.getHand()).getCapability(Capabilities.Fluid.ITEM) == null; }
}
