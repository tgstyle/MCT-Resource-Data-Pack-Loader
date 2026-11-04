package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.util.ItemUseGate;
import mctmods.resourcedatapackloader.common.util.world.PlacePositions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public final class ClaimProtection {
    private ClaimProtection() {}

    @SubscribeEvent public static void onBreak(BlockEvent.BreakEvent event) { ItemUseGate.cancel(event, ClaimSettings.protectBreak() && refuse(event.getPlayer(), event.getPos())); }

    @SubscribeEvent public static void onDig(PlayerInteractEvent.LeftClickBlock event) { ItemUseGate.cancel(event, ClaimSettings.protectBreak() && refuse(event.getEntity(), event.getPos())); }

    @SubscribeEvent public static void onAttack(AttackEntityEvent event) {
        Entity target = event.getTarget();
        ItemUseGate.cancel(event, ClaimSettings.protectBreak() && (target instanceof HangingEntity || target instanceof ArmorStand) && refuse(event.getEntity(), target.blockPosition()));
    }

    @SubscribeEvent public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (ClaimSettings.placeOpen() || !(event.getEntity() instanceof Player player) || ClaimSettings.isClaimBlock(event.getPlacedBlock().getBlock())) { return; }
        for (BlockPos pos : PlacePositions.of(event)) {
            if (refuse(player, pos)) {
                event.setCanceled(true);
                return;
            }
        }
    }

    @SubscribeEvent public static void onBucketBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack held = event.getItemStack();
        if (ClaimSettings.placeOpen() || carriesNoFluid(held) || ClaimSettings.isClaimItem(held) || ClaimSettings.isClaimBlock(event.getLevel().getBlockState(event.getPos()).getBlock())) { return; }
        Direction face = event.getFace();
        if (refuse(event.getEntity(), face == null ? event.getPos() : event.getPos().relative(face))) { deny(event); }
    }

    @SubscribeEvent public static void onBucketItem(PlayerInteractEvent.RightClickItem event) {
        ItemStack held = event.getItemStack();
        if (ClaimSettings.placeOpen() || carriesNoFluid(held) || ClaimSettings.isClaimItem(held)) { return; }
        Player player = event.getEntity();
        if (!(player.pick(player.blockInteractionRange(), 1.0F, true) instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) { return; }
        if (refuse(player, hit.getBlockPos()) || refuse(player, hit.getBlockPos().relative(hit.getDirection()))) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onUseBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!ClaimSettings.protectUse() || !ClaimSettings.useProtects(event.getLevel().getBlockState(event.getPos()).getBlock())) { return; }
        if (refuse(event.getEntity(), event.getPos())) { deny(event); }
    }

    @SubscribeEvent public static void onUseEntity(PlayerInteractEvent.EntityInteract event) { useEntity(event, event.getEntity(), event.getTarget()); }

    @SubscribeEvent public static void onUseEntitySpecific(PlayerInteractEvent.EntityInteractSpecific event) { useEntity(event, event.getEntity(), event.getTarget()); }

    @SubscribeEvent public static void onHurt(LivingIncomingDamageEvent event) {
        Entity victim = event.getEntity();
        if (!ClaimSettings.protectPvp() || !(victim instanceof Player) || !(event.getSource().getEntity() instanceof ServerPlayer attacker) || attacker == victim) { return; }
        int claim = ClaimGuard.covers(victim.level(), victim.blockPosition());
        if (claim == 0 || ClaimOwners.bypasses(attacker)) { return; }
        event.setCanceled(true);
        ClaimMessages.deny(attacker, claim);
    }

    private static void useEntity(ICancellableEvent event, Player player, Entity target) {
        boolean holds = target instanceof ItemFrame || target instanceof ArmorStand || target instanceof AbstractMinecartContainer;
        ItemUseGate.cancel(event, ClaimSettings.protectUse() && holds && refuse(player, target.blockPosition()));
    }

    private static boolean refuse(Player player, BlockPos pos) {
        if (ClaimGuard.passes(player, player.level(), pos)) { return false; }
        if (player instanceof ServerPlayer member) { ClaimMessages.deny(member, ClaimGuard.covers(player.level(), pos)); }
        return true;
    }

    static void deny(PlayerInteractEvent.RightClickBlock event) {
        event.setCanceled(true);
        event.setUseBlock(TriState.FALSE);
        event.setUseItem(TriState.FALSE);
    }

    private static boolean carriesNoFluid(ItemStack stack) { return !(stack.getItem() instanceof BucketItem) && stack.getCapability(Capabilities.FluidHandler.ITEM) == null; }
}
