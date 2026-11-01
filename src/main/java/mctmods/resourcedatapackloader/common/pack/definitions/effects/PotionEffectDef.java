package mctmods.resourcedatapackloader.common.pack.definitions.effects;

public final class PotionEffectDef {
    public final String potion;
    public final int duration;
    public final int amplifier;
    public final boolean ambient;
    public final boolean showParticles;

    public PotionEffectDef(String potion, int duration, int amplifier, boolean ambient, boolean showParticles) {
        this.potion = potion;
        this.duration = duration;
        this.amplifier = amplifier;
        this.ambient = ambient;
        this.showParticles = showParticles;
    }
}
