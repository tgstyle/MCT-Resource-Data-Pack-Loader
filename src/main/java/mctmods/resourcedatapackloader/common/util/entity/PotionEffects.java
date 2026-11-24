package mctmods.resourcedatapackloader.common.util.entity;

import mctmods.resourcedatapackloader.common.pack.definitions.effects.PotionEffectDef;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import javax.annotation.Nullable;

public final class PotionEffects {
    private PotionEffects() {}

    public static PotionEffect build(Potion potion, PotionEffectDef def, int duration, boolean showParticles) {
        return new PotionEffect(potion, duration, def.amplifier, def.ambient, showParticles);
    }

    @Nullable public static PotionEffect resolve(PotionEffectDef def, int duration, boolean showParticles) {
        Potion potion = Potion.getPotionFromResourceLocation(def.potion);
        return potion == null ? null : build(potion, def, duration, showParticles);
    }
}
