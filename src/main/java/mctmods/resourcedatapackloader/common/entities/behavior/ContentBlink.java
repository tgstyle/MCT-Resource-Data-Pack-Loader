package mctmods.resourcedatapackloader.common.entities.behavior;

import mctmods.resourcedatapackloader.common.entities.ContentEntities;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.EntityVariantDef;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public final class ContentBlink {
    private static final int TRIES = 16;

    private ContentBlink() {}

    public static void onAttacked(LivingIncomingDamageEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide()) { return; }
        EntityVariantDef def = ContentEntities.def(victim);
        DamageSource source = event.getSource();
        if (def == null || def.stalk().teleportsWhenHit() == 0 || source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) { return; }
        event.setCanceled(true);
        if (victim instanceof Mob mob && (source.getEntity() != null || source.getDirectEntity() != null)) { blink(mob, def.stalk().teleportsWhenHit()); }
    }

    private static void blink(Mob mob, int blocks) {
        double minimum = blocks / 2.0D;
        double fromX = mob.getX();
        double fromY = mob.getY();
        double fromZ = mob.getZ();
        for (int i = 0; i < TRIES; i++) {
            double x = fromX + (mob.getRandom().nextDouble() * 2.0D - 1.0D) * blocks;
            int lift = mob.getRandom().nextInt(blocks + 1) - blocks / 2;
            double y = fromY + lift;
            double z = fromZ + (mob.getRandom().nextDouble() * 2.0D - 1.0D) * blocks;
            double dx = x - fromX;
            double dy = y - fromY;
            double dz = z - fromZ;
            if (dx * dx + dy * dy + dz * dz < minimum * minimum || !mob.randomTeleport(x, y, z, true)) { continue; }
            mob.level().playSound(null, fromX, fromY, fromZ, SoundEvents.ENDERMAN_TELEPORT, mob.getSoundSource(), 1.0F, 1.0F);
            mob.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            mob.getNavigation().stop();
            return;
        }
    }
}
