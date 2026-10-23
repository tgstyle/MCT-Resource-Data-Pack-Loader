package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.def.WeaponDef;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.registries.ForgeRegistries;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ContentWeapons {
    private static final UUID REACH_ID = UUID.fromString("5f1c7a42-6d3b-4e0a-9a7e-2b8c1d4f6a90");
    private static final float FULL_STRENGTH = 0.9F;
    private static final float SWEEP_KNOCKBACK = 0.4F;
    private static final double VERTICAL_SLACK = 1.0D;
    private static final Map<UUID, Progress> COMBOS = new HashMap<>();
    private static final Map<UUID, Blow> BLOWS = new HashMap<>();
    private static boolean sweeping;

    private ContentWeapons() {}

    @Nullable public static WeaponDef weapon(ItemStack stack) {
        if (stack.isEmpty()) { return null; }
        ItemDef def = ContentRegistry.itemDef(ForgeRegistries.ITEMS.getKey(stack.getItem()));
        return def == null ? null : def.weapon();
    }

    public static void clear() {
        COMBOS.clear();
        BLOWS.clear();
    }

    public static void onModifiers(ItemAttributeModifierEvent event) {
        if (event.getSlotType() != EquipmentSlot.MAINHAND) { return; }
        WeaponDef weapon = weapon(event.getItemStack());
        if (weapon == null || !weapon.hasReach()) { return; }
        event.addModifier(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(REACH_ID, "RDPL attack reach", weapon.attackReach() - WeaponDef.VANILLA_REACH, AttributeModifier.Operation.ADDITION));
    }

    public static void onAttack(AttackEntityEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        ItemStack held = player.getMainHandItem();
        WeaponDef weapon = weapon(held);
        if (weapon == null || (weapon.combo() == null && weapon.sweep() == null)) {
            COMBOS.remove(player.getUUID());
            BLOWS.remove(player.getUUID());
            return;
        }
        BLOWS.put(player.getUUID(), new Blow(event.getTarget().getId(), weapon, ForgeRegistries.ITEMS.getKey(held.getItem()), player.getAttackStrengthScale(0.5F), player.level().getGameTime()));
    }

    public static void onHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (sweeping || !source.is(DamageTypes.PLAYER_ATTACK) || !(source.getEntity() instanceof ServerPlayer player) || source.getDirectEntity() != player) { return; }
        Blow blow = BLOWS.remove(player.getUUID());
        LivingEntity target = event.getEntity();
        if (blow == null || blow.target() != target.getId() || blow.time() != player.level().getGameTime()) { return; }
        boolean full = blow.strength() > FULL_STRENGTH;
        WeaponDef.Sweep sweep = blow.weapon().sweep();
        WeaponDef.Combo combo = blow.weapon().combo();
        if (combo != null) {
            Progress progress = COMBOS.computeIfAbsent(player.getUUID(), id -> new Progress());
            if (!full) { progress.reset(); }
            else {
                WeaponDef.Step step = combo.steps().get(progress.next(blow.item(), blow.time(), combo));
                event.setAmount(event.getAmount() * step.damage());
                if (step.sweep() != null) { sweep = step.sweep(); }
            }
        }
        if (full && sweep != null) { strike(player, target, blow.weapon(), sweep, event.getAmount() * sweep.damage()); }
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        COMBOS.remove(event.getEntity().getUUID());
        BLOWS.remove(event.getEntity().getUUID());
    }

    private static void strike(ServerPlayer player, LivingEntity target, WeaponDef weapon, WeaponDef.Sweep sweep, float damage) {
        sweeping = true;
        try {
            float yaw = player.getYRot() * Mth.DEG_TO_RAD;
            for (LivingEntity victim : gather(player, target, weapon, sweep)) {
                if (damage <= 0.0F) { break; }
                victim.knockback(SWEEP_KNOCKBACK, Mth.sin(yaw), -Mth.cos(yaw));
                victim.hurt(player.damageSources().playerAttack(player), damage);
            }
        }
        finally { sweeping = false; }
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F, 1.0F);
        player.sweepAttack();
    }

    private static List<LivingEntity> gather(ServerPlayer player, LivingEntity target, WeaponDef weapon, WeaponDef.Sweep sweep) {
        AABB body = player.getBoundingBox();
        boolean box = sweep.shape() == WeaponDef.Shape.BOX;
        double reach = box ? weapon.reachOrVanilla() : sweep.reach();
        double halfWidth = sweep.shape() == WeaponDef.Shape.LINE ? sweep.width() / 2.0D : 0.0D;
        AABB search = box ? target.getBoundingBox().inflate(1.0D, 0.25D, 1.0D) : body.inflate(reach + halfWidth);
        List<LivingEntity> found = player.level().getEntitiesOfClass(LivingEntity.class, search, other -> other != player && other != target && other.isAlive() && !other.isSpectator() && !player.isAlliedTo(other) && !(other instanceof ArmorStand stand && stand.isMarker()));
        List<LivingEntity> inside = new ArrayList<>();
        for (LivingEntity other : found) {
            if (contains(player, other, sweep, reach, halfWidth)) { inside.add(other); }
        }
        return inside;
    }

    private static boolean contains(ServerPlayer player, LivingEntity other, WeaponDef.Sweep sweep, double reach, double halfWidth) {
        AABB box = other.getBoundingBox();
        if (sweep.shape() == WeaponDef.Shape.BOX) { return player.distanceToSqr(other) < reach * reach; }
        if (sweep.shape() == WeaponDef.Shape.LINE) {
            Vec3 eye = player.getEyePosition();
            Vec3 look = player.getLookAngle();
            AABB wide = box.inflate(halfWidth);
            return wide.contains(eye) || wide.clip(eye, eye.add(look.x * reach, look.y * reach, look.z * reach)).isPresent();
        }
        AABB body = player.getBoundingBox();
        if (box.maxY <= body.minY - VERTICAL_SLACK || box.minY >= body.maxY + VERTICAL_SLACK) { return false; }
        double gapX = Math.max(Math.max(box.minX - player.getX(), player.getX() - box.maxX), 0.0D);
        double gapZ = Math.max(Math.max(box.minZ - player.getZ(), player.getZ() - box.maxZ), 0.0D);
        double gap = Math.sqrt(gapX * gapX + gapZ * gapZ);
        if (gap > reach) { return false; }
        if (sweep.shape() == WeaponDef.Shape.CIRCLE || gap < 1.0E-3D) { return true; }
        double toX = (box.minX + box.maxX) / 2.0D - player.getX();
        double toZ = (box.minZ + box.maxZ) / 2.0D - player.getZ();
        double length = Math.sqrt(toX * toX + toZ * toZ);
        if (length < 1.0E-6D) { return true; }
        float yaw = player.getYRot() * Mth.DEG_TO_RAD;
        double facing = (toX * -Mth.sin(yaw) + toZ * Mth.cos(yaw)) / length;
        return facing >= Math.cos(Math.toRadians(sweep.angle() / 2.0D));
    }

    private record Blow(int target, WeaponDef weapon, ResourceLocation item, float strength, long time) {}

    private static final class Progress {
        @Nullable private ResourceLocation item;
        private int step;
        private long last;

        void reset() { this.item = null; }

        int next(ResourceLocation held, long now, WeaponDef.Combo combo) {
            boolean fresh = item == null || !item.equals(held) || step >= combo.steps().size() || now - last > combo.window();
            int index = fresh ? 0 : step;
            this.item = held;
            this.step = index + 1;
            this.last = now;
            return index;
        }
    }
}
