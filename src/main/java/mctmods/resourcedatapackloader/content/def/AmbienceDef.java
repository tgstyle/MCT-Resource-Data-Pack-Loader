package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;
import javax.annotation.Nullable;

public final class AmbienceDef {
    public static final int NO_COLOR = -1;
    @Nullable public final ResourceLocation music;
    public final int musicMinDelay;
    public final int musicMaxDelay;
    @Nullable public final ResourceLocation loopSound;
    public final String ambientSound;
    public final float soundChance;
    public final String particle;
    public final float particleChance;
    public final int particleColor;

    public AmbienceDef(@Nullable ResourceLocation music, int musicMinDelay, int musicMaxDelay, @Nullable ResourceLocation loopSound, String ambientSound, float soundChance, String particle, float particleChance, int particleColor) {
        this.music = music;
        this.musicMinDelay = musicMinDelay;
        this.musicMaxDelay = musicMaxDelay;
        this.loopSound = loopSound;
        this.ambientSound = ambientSound;
        this.soundChance = soundChance;
        this.particle = particle;
        this.particleChance = particleChance;
        this.particleColor = particleColor;
    }

    public boolean sounds() { return !ambientSound.isEmpty() && soundChance > 0.0F; }

    public boolean shows() { return !particle.isEmpty() && particleChance > 0.0F; }
}
