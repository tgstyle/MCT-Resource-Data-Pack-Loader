package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import javax.annotation.Nullable;

public record AmbienceDef(@Nullable ResourceLocation music, int musicMinDelay, int musicMaxDelay, @Nullable ResourceLocation loopSound, @Nullable ResourceLocation ambientSound, float soundChance,
                          @Nullable ResourceLocation particle, float particleChance, int particleColor) {
    public static final int NO_COLOR = -1;

    public boolean sounds() { return ambientSound != null && soundChance > 0.0F; }

    public boolean shows() { return particle != null && particleChance > 0.0F; }
}
