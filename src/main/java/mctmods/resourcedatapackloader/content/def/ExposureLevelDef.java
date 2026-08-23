package mctmods.resourcedatapackloader.content.def;

import java.util.List;

public final class ExposureLevelDef {
    public final String effect;
    public final float damage;
    public final int damageInterval;
    public final List<PotionEffectDef> extras;

    public ExposureLevelDef(String effect, float damage, int damageInterval, List<PotionEffectDef> extras) {
        this.effect = effect;
        this.damage = damage;
        this.damageInterval = damageInterval;
        this.extras = extras;
    }
}
