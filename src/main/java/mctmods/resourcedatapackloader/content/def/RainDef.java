package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;
import javax.annotation.Nullable;

public final class RainDef {
    public final String particle;
    public final ResourceLocation sound;
    public final float volume;
    public final int interval;
    public final int color;
    public final int snowColor;
    public final float angle;
    public final float heading;
    public final boolean splashUpward;
    @Nullable public final WindDef wind;

    public RainDef(String particle, ResourceLocation sound, float volume, int interval, int color, int snowColor, float angle, float heading, boolean splashUpward, @Nullable WindDef wind) {
        this.particle = particle;
        this.sound = sound;
        this.volume = volume;
        this.interval = interval;
        this.color = color;
        this.snowColor = snowColor;
        this.angle = angle;
        this.heading = heading;
        this.splashUpward = splashUpward;
        this.wind = wind;
    }

    public boolean upward() { return angle > 90.0F; }

    public boolean splashless() { return upward() && !splashUpward; }
}
