package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.mixin.rdpl.common.IExperienceOrb;
import mctmods.resourcedatapackloader.mixin.rdpl.common.ILivingEntity;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Scores;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import java.util.Optional;

public final class ContentMobExperience {
    private static final float MENDING_RATIO = 2.0F;
    private static final String LEVEL = "rdplXpLevel";
    private static final String PROGRESS = "rdplXp";
    private static final String TOTAL = "rdplXpTotal";
    private static final double REACH = 8.0D;
    private static final int CREDIT = 100;

    private ContentMobExperience() {}

    public static int level(Entity entity) { return entity.getPersistentData().getInt(LEVEL); }

    public static void collect(Mob mob) {
        AABB touch = mob.getBoundingBox().inflate(1.0D, 0.5D, 1.0D);
        boolean ready = mob.tickCount % 2 == 0;
        for (ExperienceOrb orb : mob.level().getEntitiesOfClass(ExperienceOrb.class, mob.getBoundingBox().inflate(REACH))) {
            if (!orb.isAlive()) { continue; }
            if (orb.getBoundingBox().intersects(touch)) {
                if (ready) {
                    take(mob, orb);
                    ready = false;
                }
                continue;
            }
            if (mob.level().getNearestPlayer(orb, REACH) == null) { pull(orb, mob); }
        }
    }

    private static void take(Mob mob, ExperienceOrb orb) {
        mob.take(orb, 1);
        int value = mob.level() instanceof ServerLevel ? mended(mob, orb.value) : orb.value;
        if (value > 0) { add(mob, value); }
        IExperienceOrb held = (IExperienceOrb) orb;
        held.rdpl$setCount(held.rdpl$getCount() - 1);
        if (held.rdpl$getCount() <= 0) { orb.discard(); }
    }

    private static int mended(Mob mob, int value) {
        Optional<EnchantedItemInUse> found = EnchantmentHelper.getRandomItemWith(EnchantmentEffectComponents.REPAIR_WITH_XP, mob, stack -> true);
        if (found.isEmpty() || !found.get().itemStack().isDamaged()) { return value; }
        ItemStack mended = found.get().itemStack();
        float ratio = MENDING_RATIO * mended.getXpRepairRatio();
        int repaired = Math.min(roundAverage(value * ratio), mended.getDamageValue());
        mended.setDamageValue(mended.getDamageValue() - repaired);
        return value - roundAverage(repaired / ratio);
    }

    private static void pull(ExperienceOrb orb, Mob mob) {
        Vec3 toward = new Vec3(mob.getX() - orb.getX(), mob.getY() + mob.getEyeHeight() / 2.0D - orb.getY(), mob.getZ() - orb.getZ()).scale(1.0D / REACH);
        double distance = toward.length();
        double strength = 1.0D - distance;
        if (strength <= 0.0D || distance <= 0.0D) { return; }
        orb.setDeltaMovement(orb.getDeltaMovement().add(toward.normalize().scale(strength * strength * 0.1D)));
    }

    public static void add(Mob mob, int amount) {
        CompoundTag data = mob.getPersistentData();
        int level = data.getInt(LEVEL);
        float progress = data.getFloat(PROGRESS);
        int total = data.getInt(TOTAL);
        int before = level;
        amount = Math.min(amount, Integer.MAX_VALUE - total);
        progress += (float) amount / cap(level);
        for (total += amount; progress >= 1.0F; progress /= cap(level)) {
            progress = (progress - 1.0F) * cap(level);
            level++;
        }
        data.putInt(LEVEL, level);
        data.putFloat(PROGRESS, progress);
        data.putInt(TOTAL, total);
        scores(mob, level, total);
        if (level > before) { ContentLog.LOGGER.info("{} reached experience level {}, {} point(s) in all", mob.getName().getString(), level, total); }
    }

    public static void addLevels(Mob mob, int levels) {
        CompoundTag data = mob.getPersistentData();
        int level = data.getInt(LEVEL) + levels;
        if (level < 0) {
            level = 0;
            data.putFloat(PROGRESS, 0.0F);
            data.putInt(TOTAL, 0);
        }
        data.putInt(LEVEL, level);
        scores(mob, level, data.getInt(TOTAL));
    }

    private static int cap(int level) {
        if (level >= 30) { return 112 + (level - 30) * 9; }
        return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
    }

    private static int roundAverage(float value) {
        double floor = Math.floor(value);
        return (int) floor + (Math.random() < value - floor ? 1 : 0);
    }

    private static void scores(Mob mob, int level, int total) {
        if (mob.getServer() == null) { return; }
        Scoreboard board = Scores.board(mob.getServer());
        String row = mob.getStringUUID();
        for (Objective objective : board.getObjectives()) {
            if (objective.getCriteria() == ObjectiveCriteria.EXPERIENCE) { Scores.set(board, row, objective, total); }
            else if (objective.getCriteria() == ObjectiveCriteria.LEVEL) { Scores.set(board, row, objective, level); }
        }
    }

    public static void onHurt(LivingDamageEvent.Pre event) {
        LivingEntity hurt = event.getEntity();
        Entity by = event.getSource().getEntity();
        if (hurt.level().isClientSide() || hurt instanceof Player || !(by instanceof Mob) || ContentEntities.ignoresExperience(by)) { return; }
        ((ILivingEntity) hurt).rdpl$setLastHurtByPlayerTime(CREDIT);
    }

    public static void onDeath(LivingDeathEvent event) {
        LivingEntity died = event.getEntity();
        if (event.isCanceled() || !(died.level() instanceof ServerLevel level) || !(died instanceof Mob) || ContentEntities.ignoresExperience(died)) { return; }
        int dropped = level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) ? 0 : Math.min(level(died) * 7, 100);
        CompoundTag data = died.getPersistentData();
        data.remove(LEVEL);
        data.remove(PROGRESS);
        data.remove(TOTAL);
        if (dropped > 0) { ExperienceOrb.award(level, died.position(), dropped); }
    }
}
