package mctmods.resourcedatapackloader.common.entities.behavior;

import mctmods.resourcedatapackloader.common.entities.ContentEntities;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.EntityVariantDef;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public final class ContentBlink {
    private static final int TRIES = 16;

    private ContentBlink() {}

    public static void onAttacked(LivingAttackEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide()) { return; }
        EntityVariantDef def = ContentEntities.def(victim);
        if (def == null || def.stalk().teleportsWhenHit() == 0) { return; }
        DamageSource source = event.getSource();
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) { return; }
        event.setCanceled(true);
        if (victim instanceof Mob mob && (source.getEntity() != null || source.getDirectEntity() != null)) { blink(mob, def.stalk().teleportsWhenHit()); }
    }

    private static void blink(Mob mob, int blocks) {
        double x = mob.getX();
        double y = mob.getY();
        double z = mob.getZ();
        int half = blocks / 2;
        double least = blocks / 2.0D;
        for (int i = 0; i < TRIES; i++) {
            double toX = x + (mob.getRandom().nextDouble() * 2.0D - 1.0D) * blocks;
            double toY = y + mob.getRandom().nextInt(half * 2 + 1) - half;
            double toZ = z + (mob.getRandom().nextDouble() * 2.0D - 1.0D) * blocks;
            double dx = toX - x;
            double dy = toY - y;
            double dz = toZ - z;
            if (dx * dx + dy * dy + dz * dz < least * least || !mob.randomTeleport(toX, toY, toZ, true)) { continue; }
            mob.level().playSound(null, x, y, z, SoundEvents.ENDERMAN_TELEPORT, mob.getSoundSource(), 1.0F, 1.0F);
            mob.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            mob.getNavigation().stop();
            return;
        }
    }
}
