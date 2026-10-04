package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;

public final class RainDef {
    public final String particle;
    public final ResourceLocation sound;
    public final float volume;
    public final int interval;
    public final int color;
    public final int snowColor;
    public final float angle;
    public final float heading;

    public RainDef(String particle, ResourceLocation sound, float volume, int interval, int color, int snowColor, float angle, float heading) {
        this.particle = particle;
        this.sound = sound;
        this.volume = volume;
        this.interval = interval;
        this.color = color;
        this.snowColor = snowColor;
        this.angle = angle;
        this.heading = heading;
    }
}
