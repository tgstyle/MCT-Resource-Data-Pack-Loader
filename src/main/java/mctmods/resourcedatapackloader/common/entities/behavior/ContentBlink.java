package mctmods.resourcedatapackloader.common.entities.behavior;

import mctmods.resourcedatapackloader.common.entities.ContentEntities;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.EntityVariantDef;
import mctmods.resourcedatapackloader.core.util.LineCompat;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
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
        if (def == null || def.stalk().teleportsWhenHit() <= 0 || event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) { return; }
        event.setCanceled(true);
        if (event.getSource().getEntity() != null || event.getSource().getDirectEntity() != null) { blink(victim, def.stalk().teleportsWhenHit()); }
    }

    private static void blink(LivingEntity living, int reach) {
        double fromX = living.getX();
        double fromY = living.getY();
        double fromZ = living.getZ();
        double least = reach / 2.0D;
        int spread = reach / 2;
        for (int i = 0; i < TRIES; i++) {
            double x = fromX + (living.getRandom().nextDouble() * 2.0D - 1.0D) * reach;
            double y = fromY + living.getRandom().nextInt(spread * 2 + 1) - spread;
            double z = fromZ + (living.getRandom().nextDouble() * 2.0D - 1.0D) * reach;
            if ((x - fromX) * (x - fromX) + (z - fromZ) * (z - fromZ) < least * least || !LineCompat.randomTeleport(living, x, y, z)) { continue; }
            living.level().playSound(null, fromX, fromY, fromZ, SoundEvents.ENDERMAN_TELEPORT, living.getSoundSource(), 1.0F, 1.0F);
            living.level().playSound(null, living.getX(), living.getY(), living.getZ(), SoundEvents.ENDERMAN_TELEPORT, living.getSoundSource(), 1.0F, 1.0F);
            if (living instanceof Mob mob) { mob.getNavigation().stop(); }
            return;
        }
    }
}
