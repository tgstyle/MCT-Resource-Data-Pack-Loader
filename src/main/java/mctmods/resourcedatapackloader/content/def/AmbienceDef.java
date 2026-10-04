package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.Identifier;
import javax.annotation.Nullable;

public record AmbienceDef(@Nullable Identifier music, int musicMinDelay, int musicMaxDelay, @Nullable Identifier loopSound, @Nullable Identifier ambientSound, float soundChance, @Nullable Identifier particle,
                          float particleChance, int particleColor) {
    public static final int NO_COLOR = -1;

    public boolean sounds() { return ambientSound != null && soundChance > 0.0F; }

    public boolean shows() { return particle != null && particleChance > 0.0F; }
}
