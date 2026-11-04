package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.util.ItemUseGate;
import mctmods.resourcedatapackloader.common.util.world.PlacePositions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ClaimProtection {
    private ClaimProtection() {}

    @SubscribeEvent public static void onBreak(BlockEvent.BreakEvent event) { ItemUseGate.cancel(event, ClaimSettings.protectBreak() && refuse(event.getPlayer(), event.getPos())); }

    @SubscribeEvent public static void onDig(PlayerInteractEvent.LeftClickBlock event) { ItemUseGate.cancel(event, ClaimSettings.protectBreak() && refuse(event.getEntityPlayer(), event.getPos())); }

    @SubscribeEvent public static void onAttack(AttackEntityEvent event) {
        Entity target = event.getTarget();
        ItemUseGate.cancel(event, ClaimSettings.protectBreak() && (target instanceof EntityHanging || target instanceof EntityArmorStand) && refuse(event.getEntityPlayer(), target.getPosition()));
    }

    @SubscribeEvent public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (ClaimSettings.placeOpen() || !(event.getEntity() instanceof EntityPlayer) || ClaimSettings.isClaimBlock(event.getPlacedBlock().getBlock())) { return; }
        EntityPlayer player = (EntityPlayer) event.getEntity();
        for (BlockPos pos : PlacePositions.of(event)) {
            if (refuse(player, pos)) {
                event.setCanceled(true);
                return;
            }
        }
    }

    @SubscribeEvent public static void onBucketBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack held = event.getItemStack();
        if (ClaimSettings.placeOpen() || carriesNoFluid(held) || ClaimSettings.isClaimItem(held) || ClaimSettings.isClaimBlock(event.getWorld().getBlockState(event.getPos()).getBlock())) { return; }
        EnumFacing face = event.getFace();
        if (refuse(event.getEntityPlayer(), face == null ? event.getPos() : event.getPos().offset(face))) { deny(event); }
    }

    @SubscribeEvent public static void onBucketItem(PlayerInteractEvent.RightClickItem event) {
        ItemStack held = event.getItemStack();
        if (ClaimSettings.placeOpen() || carriesNoFluid(held) || ClaimSettings.isClaimItem(held)) { return; }
        EntityPlayer player = event.getEntityPlayer();
        RayTraceResult hit = aimed(player);
        if (hit == null || hit.typeOfHit != RayTraceResult.Type.BLOCK) { return; }
        if (refuse(player, hit.getBlockPos()) || refuse(player, hit.getBlockPos().offset(hit.sideHit))) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onUseBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!ClaimSettings.protectUse() || !ClaimSettings.useProtects(event.getWorld().getBlockState(event.getPos()).getBlock())) { return; }
        if (refuse(event.getEntityPlayer(), event.getPos())) { deny(event); }
    }

    @SubscribeEvent public static void onUseEntity(PlayerInteractEvent.EntityInteract event) { useEntity(event, event.getEntityPlayer(), event.getTarget()); }

    @SubscribeEvent public static void onUseEntitySpecific(PlayerInteractEvent.EntityInteractSpecific event) { useEntity(event, event.getEntityPlayer(), event.getTarget()); }

    @SubscribeEvent public static void onHurt(LivingAttackEvent event) {
        Entity source = event.getSource().getTrueSource();
        if (!ClaimSettings.protectPvp() || !(event.getEntityLiving() instanceof EntityPlayer) || !(source instanceof EntityPlayer) || source == event.getEntityLiving()) { return; }
        EntityPlayer attacker = (EntityPlayer) source;
        int claim = ClaimGuard.covers(event.getEntityLiving().world, event.getEntityLiving().getPosition());
        if (claim == 0 || ClaimOwners.bypasses(attacker)) { return; }
        event.setCanceled(true);
        ClaimMessages.deny(attacker, claim);
    }

    private static void useEntity(PlayerInteractEvent event, EntityPlayer player, Entity target) {
        boolean holds = target instanceof EntityItemFrame || target instanceof EntityArmorStand || target instanceof EntityMinecartContainer;
        ItemUseGate.cancel(event, ClaimSettings.protectUse() && holds && refuse(player, target.getPosition()));
    }

    private static boolean refuse(EntityPlayer player, BlockPos pos) {
        if (ClaimGuard.passes(player, player.world, pos)) { return false; }
        ClaimMessages.deny(player, ClaimGuard.covers(player.world, pos));
        return true;
    }

    static void deny(PlayerInteractEvent.RightClickBlock event) {
        event.setCanceled(true);
        event.setUseBlock(Event.Result.DENY);
        event.setUseItem(Event.Result.DENY);
    }

    private static boolean carriesNoFluid(ItemStack stack) { return !(stack.getItem() instanceof ItemBucket) && !stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null); }

    private static RayTraceResult aimed(EntityPlayer player) {
        Vec3d eyes = player.getPositionEyes(1.0F);
        double reach = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
        Vec3d look = player.getLook(1.0F);
        return player.world.rayTraceBlocks(eyes, eyes.add(look.x * reach, look.y * reach, look.z * reach), true, false, false);
    }
}
