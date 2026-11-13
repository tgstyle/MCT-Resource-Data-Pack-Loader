package mctmods.resourcedatapackloader.common.entities.behavior;

import mctmods.resourcedatapackloader.common.entities.ContentEntities;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.EntityVariantDef;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ContentBlink {
    private static final int TRIES = 16;

    private ContentBlink() {}

    @SubscribeEvent public static void onAttacked(LivingAttackEvent event) {
        EntityLivingBase victim = event.getEntityLiving();
        if (victim.world.isRemote) { return; }
        EntityVariantDef def = ContentEntities.variantOf(victim);
        DamageSource source = event.getSource();
        if (def == null || def.teleportsWhenHit == 0 || source.canHarmInCreative()) { return; }
        event.setCanceled(true);
        if (source.getTrueSource() != null || source.getImmediateSource() != null) { blink(victim, def.teleportsWhenHit); }
    }

    private static void blink(EntityLivingBase living, int blocks) {
        double fromX = living.posX;
        double fromY = living.posY;
        double fromZ = living.posZ;
        int half = blocks / 2;
        for (int i = 0; i < TRIES; i++) {
            double x = fromX + (living.getRNG().nextDouble() * 2.0D - 1.0D) * blocks;
            double y = fromY + living.getRNG().nextInt(blocks + 1) - half;
            double z = fromZ + (living.getRNG().nextDouble() * 2.0D - 1.0D) * blocks;
            double dx = x - fromX;
            double dy = y - fromY;
            double dz = z - fromZ;
            if (dx * dx + dy * dy + dz * dz < half * half || !living.attemptTeleport(x, y, z)) { continue; }
            living.world.playSound(null, fromX, fromY, fromZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE, 1.0F, 1.0F);
            living.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
            if (living instanceof EntityLiving) { ((EntityLiving) living).getNavigator().clearPath(); }
            return;
        }
    }
}
