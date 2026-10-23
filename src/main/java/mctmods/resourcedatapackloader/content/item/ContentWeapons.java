package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.def.WeaponDef;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ResourceDataPackLoader.MOD_ID) public final class ContentWeapons {
    private static final float FULL_STRENGTH = 0.9F;
    private static final float SWEEP_KNOCKBACK = 0.4F;
    private static final double VERTICAL_SLACK = 1.0D;
    private static final Map<UUID, Progress> COMBOS = new HashMap<>();
    private static final Map<UUID, Blow> BLOWS = new HashMap<>();
    private static boolean sweeping;

    private ContentWeapons() {}

    @Nullable public static WeaponDef weapon(ItemStack stack) {
        if (stack.isEmpty()) { return null; }
        ResourceLocation key = stack.getItem().getRegistryName();
        ItemDef def = key == null ? null : ContentRegistry.itemDef(key);
        return def == null ? null : def.weapon;
    }

    public static double reachSqr(EntityPlayer player, double vanilla, double slack) {
        WeaponDef weapon = weapon(player.getHeldItemMainhand());
        if (weapon == null || !weapon.hasReach()) { return vanilla; }
        double reach = weapon.attackReach + slack;
        return Math.max(vanilla, reach * reach);
    }

    public static void clear() {
        COMBOS.clear();
        BLOWS.clear();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onAttack(AttackEntityEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player.world.isRemote) { return; }
        ItemStack held = player.getHeldItemMainhand();
        WeaponDef weapon = weapon(held);
        UUID id = player.getUniqueID();
        if (weapon == null || (weapon.combo == null && weapon.sweep == null)) {
            COMBOS.remove(id);
            BLOWS.remove(id);
            return;
        }
        BLOWS.put(id, new Blow(event.getTarget().getEntityId(), weapon, held.getItem().getRegistryName(), player.getCooledAttackStrength(0.5F), player.world.getTotalWorldTime()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (sweeping || !(source.getTrueSource() instanceof EntityPlayer) || source.getImmediateSource() != source.getTrueSource()) { return; }
        EntityPlayer player = (EntityPlayer) source.getTrueSource();
        if (player.world.isRemote) { return; }
        Blow blow = BLOWS.remove(player.getUniqueID());
        EntityLivingBase target = event.getEntityLiving();
        if (blow == null || blow.target != target.getEntityId() || blow.time != player.world.getTotalWorldTime()) { return; }
        boolean full = blow.strength > FULL_STRENGTH;
        WeaponDef.Sweep sweep = blow.weapon.sweep;
        WeaponDef.Combo combo = blow.weapon.combo;
        if (combo != null) {
            Progress progress = COMBOS.computeIfAbsent(player.getUniqueID(), id -> new Progress());
            if (!full) { progress.reset(); }
            else {
                int index = progress.next(blow.item, blow.time, combo);
                WeaponDef.Step step = combo.steps.get(index);
                event.setAmount(event.getAmount() * step.damage);
                if (step.sweep != null) { sweep = step.sweep; }
            }
        }
        if (full && sweep != null) { strike(player, target, blow.weapon, sweep, event.getAmount() * sweep.damage); }
    }

    @SubscribeEvent public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        COMBOS.remove(event.player.getUniqueID());
        BLOWS.remove(event.player.getUniqueID());
    }

    private static void strike(EntityPlayer player, EntityLivingBase target, WeaponDef weapon, WeaponDef.Sweep sweep, float damage) {
        sweeping = true;
        try {
            float yaw = player.rotationYaw * 0.017453292F;
            for (EntityLivingBase victim : gather(player, target, weapon, sweep)) {
                if (damage <= 0.0F) { break; }
                victim.knockBack(player, SWEEP_KNOCKBACK, MathHelper.sin(yaw), -MathHelper.cos(yaw));
                victim.attackEntityFrom(DamageSource.causePlayerDamage(player), damage);
            }
        }
        finally { sweeping = false; }
        player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
        player.spawnSweepParticles();
    }

    private static List<EntityLivingBase> gather(EntityPlayer player, EntityLivingBase target, WeaponDef weapon, WeaponDef.Sweep sweep) {
        World world = player.world;
        AxisAlignedBB body = player.getEntityBoundingBox();
        boolean box = sweep.shape == WeaponDef.Shape.BOX;
        double reach = box ? weapon.reachOrVanilla() : sweep.reach;
        double halfWidth = sweep.shape == WeaponDef.Shape.LINE ? sweep.width / 2.0D : 0.0D;
        AxisAlignedBB search = box ? target.getEntityBoundingBox().grow(1.0D, 0.25D, 1.0D) : body.grow(reach + halfWidth);
        List<EntityLivingBase> found = world.getEntitiesWithinAABB(EntityLivingBase.class, search, other -> other != null && other != player && other != target && other.isEntityAlive() && EntitySelectors.NOT_SPECTATING.apply(other) && !player.isOnSameTeam(other) && !(other instanceof EntityArmorStand && ((EntityArmorStand) other).hasMarker()));
        List<EntityLivingBase> inside = new ArrayList<>();
        for (EntityLivingBase other : found) {
            if (contains(player, other, sweep, reach, halfWidth)) { inside.add(other); }
        }
        return inside;
    }

    private static boolean contains(EntityPlayer player, EntityLivingBase other, WeaponDef.Sweep sweep, double reach, double halfWidth) {
        AxisAlignedBB box = other.getEntityBoundingBox();
        if (sweep.shape == WeaponDef.Shape.BOX) { return player.getDistanceSq(other) < reach * reach; }
        if (sweep.shape == WeaponDef.Shape.LINE) {
            Vec3d eye = player.getPositionEyes(1.0F);
            Vec3d look = player.getLookVec();
            AxisAlignedBB wide = box.grow(halfWidth);
            return wide.contains(eye) || wide.calculateIntercept(eye, eye.add(look.x * reach, look.y * reach, look.z * reach)) != null;
        }
        AxisAlignedBB body = player.getEntityBoundingBox();
        if (box.maxY <= body.minY - VERTICAL_SLACK || box.minY >= body.maxY + VERTICAL_SLACK) { return false; }
        double gapX = Math.max(Math.max(box.minX - player.posX, player.posX - box.maxX), 0.0D);
        double gapZ = Math.max(Math.max(box.minZ - player.posZ, player.posZ - box.maxZ), 0.0D);
        double gap = Math.sqrt(gapX * gapX + gapZ * gapZ);
        if (gap > reach) { return false; }
        if (sweep.shape == WeaponDef.Shape.CIRCLE || gap < 1.0E-3D) { return true; }
        double toX = (box.minX + box.maxX) / 2.0D - player.posX;
        double toZ = (box.minZ + box.maxZ) / 2.0D - player.posZ;
        double length = Math.sqrt(toX * toX + toZ * toZ);
        if (length < 1.0E-6D) { return true; }
        float yaw = player.rotationYaw * 0.017453292F;
        double facing = (toX * -MathHelper.sin(yaw) + toZ * MathHelper.cos(yaw)) / length;
        return facing >= Math.cos(Math.toRadians(sweep.angle / 2.0D));
    }

    private static final class Blow {
        final int target;
        final WeaponDef weapon;
        final ResourceLocation item;
        final float strength;
        final long time;

        Blow(int target, WeaponDef weapon, ResourceLocation item, float strength, long time) {
            this.target = target;
            this.weapon = weapon;
            this.item = item;
            this.strength = strength;
            this.time = time;
        }
    }

    private static final class Progress {
        @Nullable ResourceLocation item;
        int step;
        long last;

        void reset() { this.item = null; }

        int next(ResourceLocation held, long now, WeaponDef.Combo combo) {
            boolean fresh = item == null || !item.equals(held) || step >= combo.steps.size() || now - last > combo.window;
            int index = fresh ? 0 : step;
            this.item = held;
            this.step = index + 1;
            this.last = now;
            return index;
        }
    }
}
