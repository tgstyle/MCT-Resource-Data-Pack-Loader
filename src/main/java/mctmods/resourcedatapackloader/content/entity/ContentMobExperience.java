package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.mixin.rdpl.common.IEntityLivingBase;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ContentMobExperience {
    private static final String LEVEL = "rdplXpLevel";
    private static final String PROGRESS = "rdplXp";
    private static final String TOTAL = "rdplXpTotal";
    private static final double REACH = 8.0D;
    private static final int CREDIT = 100;

    private ContentMobExperience() {}

    public static int level(Entity entity) { return entity.getEntityData().getInteger(LEVEL); }

    public static void collect(EntityLiving mob) {
        AxisAlignedBB touch = mob.getEntityBoundingBox().grow(1.0D, 0.5D, 1.0D);
        boolean ready = mob.ticksExisted % 2 == 0;
        for (EntityXPOrb orb : mob.world.getEntitiesWithinAABB(EntityXPOrb.class, mob.getEntityBoundingBox().grow(REACH))) {
            if (orb.isDead) { continue; }
            if (orb.getEntityBoundingBox().intersects(touch)) {
                if (ready && orb.delayBeforeCanPickup == 0) {
                    take(mob, orb);
                    ready = false;
                }
                continue;
            }
            if (mob.world.getClosestPlayerToEntity(orb, REACH) == null) { pull(orb, mob); }
        }
    }

    private static void take(EntityLiving mob, EntityXPOrb orb) {
        mob.onItemPickup(orb, 1);
        Enchantment mending = Enchantments.MENDING;
        ItemStack mended = mending == null ? ItemStack.EMPTY : EnchantmentHelper.getEnchantedItem(mending, mob);
        if (!mended.isEmpty() && mended.isItemDamaged()) {
            float ratio = mended.getItem().getXpRepairRatio(mended);
            int repaired = Math.min(roundAverage(orb.xpValue * ratio), mended.getItemDamage());
            orb.xpValue -= roundAverage(repaired / ratio);
            mended.setItemDamage(mended.getItemDamage() - repaired);
        }
        if (orb.xpValue > 0) { add(mob, orb.xpValue); }
        orb.setDead();
    }

    private static void pull(EntityXPOrb orb, EntityLiving mob) {
        double dx = (mob.posX - orb.posX) / REACH;
        double dy = (mob.posY + mob.getEyeHeight() / 2.0D - orb.posY) / REACH;
        double dz = (mob.posZ - orb.posZ) / REACH;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double strength = 1.0D - distance;
        if (strength <= 0.0D || distance <= 0.0D) { return; }
        strength = strength * strength;
        orb.motionX += dx / distance * strength * 0.1D;
        orb.motionY += dy / distance * strength * 0.1D;
        orb.motionZ += dz / distance * strength * 0.1D;
    }

    public static void add(EntityLiving mob, int amount) {
        NBTTagCompound data = mob.getEntityData();
        int level = data.getInteger(LEVEL);
        float progress = data.getFloat(PROGRESS);
        int total = data.getInteger(TOTAL);
        int before = level;
        amount = Math.min(amount, Integer.MAX_VALUE - total);
        progress += (float) amount / cap(level);
        for (total += amount; progress >= 1.0F; progress /= cap(level)) {
            progress = (progress - 1.0F) * cap(level);
            level++;
        }
        data.setInteger(LEVEL, level);
        data.setFloat(PROGRESS, progress);
        data.setInteger(TOTAL, total);
        scores(mob, level, total);
        if (level > before) { ContentLog.LOGGER.info("{} reached experience level {}, {} point(s) in all", mob.getName(), level, total); }
    }

    public static void addLevels(EntityLiving mob, int levels) {
        NBTTagCompound data = mob.getEntityData();
        int level = data.getInteger(LEVEL) + levels;
        if (level < 0) {
            level = 0;
            data.setFloat(PROGRESS, 0.0F);
            data.setInteger(TOTAL, 0);
        }
        data.setInteger(LEVEL, level);
        scores(mob, level, data.getInteger(TOTAL));
    }

    private static int cap(int level) {
        if (level >= 30) { return 112 + (level - 30) * 9; }
        return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
    }

    private static int roundAverage(float value) {
        double floor = Math.floor(value);
        return (int) floor + (Math.random() < value - floor ? 1 : 0);
    }

    private static void scores(EntityLiving mob, int level, int total) {
        Scoreboard board = mob.world.getScoreboard();
        String row = mob.getCachedUniqueIdString();
        for (ScoreObjective objective : board.getObjectivesFromCriteria(IScoreCriteria.XP)) { board.getOrCreateScore(row, objective).setScorePoints(total); }
        for (ScoreObjective objective : board.getObjectivesFromCriteria(IScoreCriteria.LEVEL)) { board.getOrCreateScore(row, objective).setScorePoints(level); }
    }

    @SubscribeEvent public static void onHurt(LivingHurtEvent event) {
        EntityLivingBase hurt = event.getEntityLiving();
        Entity by = event.getSource().getTrueSource();
        if (hurt.world.isRemote || hurt instanceof EntityPlayer || !(by instanceof EntityLiving) || !ContentEntities.collectsExperience(by)) { return; }
        ((IEntityLivingBase) hurt).rdpl$setRecentlyHit(CREDIT);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onDeath(LivingDeathEvent event) {
        EntityLivingBase died = event.getEntityLiving();
        if (event.isCanceled() || died.world.isRemote || !(died instanceof EntityLiving) || !ContentEntities.collectsExperience(died)) { return; }
        int dropped = died.world.getGameRules().getBoolean("keepInventory") ? 0 : Math.min(level(died) * 7, 100);
        NBTTagCompound data = died.getEntityData();
        data.removeTag(LEVEL);
        data.removeTag(PROGRESS);
        data.removeTag(TOTAL);
        while (dropped > 0) {
            int split = EntityXPOrb.getXPSplit(dropped);
            dropped -= split;
            died.world.spawnEntity(new EntityXPOrb(died.world, died.posX, died.posY, died.posZ, split));
        }
    }
}
