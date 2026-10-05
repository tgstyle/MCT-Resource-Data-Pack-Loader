package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.Identifier;
import javax.annotation.Nullable;

public record RainDef(Identifier particle, Identifier sound, float volume, int interval, int color, int snowColor, float angle, float heading, boolean splashUpward, @Nullable WindDef wind) {
    public boolean upward() { return angle > 90.0F; }

    public boolean splashless() { return upward() && !splashUpward; }
}
