package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;
import java.util.List;
import javax.annotation.Nullable;

public final class DimensionTraitsDef {
    public static final long VANILLA_DAY = 24000L;
    public static final long NO_DAY = 0L;
    public static final float VANILLA_SUN = 30.0F;
    public final double gravity;
    public final double fallDamage;
    public final double arrowGravity;
    public final long dayLength;
    public final boolean precipitation;
    public final boolean lightning;
    public final boolean snow;
    public final boolean freeze;
    @Nullable public final Cycle cycle;
    @Nullable public final Sky sky;

    public DimensionTraitsDef(double gravity, double fallDamage, double arrowGravity, long dayLength, boolean precipitation, boolean lightning, boolean snow, boolean freeze, @Nullable Cycle cycle, @Nullable Sky sky) {
        this.gravity = gravity;
        this.fallDamage = fallDamage;
        this.arrowGravity = arrowGravity;
        this.dayLength = dayLength;
        this.precipitation = precipitation;
        this.lightning = lightning;
        this.snow = snow;
        this.freeze = freeze;
        this.cycle = cycle;
        this.sky = sky;
    }

    public static final class Cycle {
        public final int rainMin;
        public final int rainMax;
        public final int clearMin;
        public final int clearMax;
        public final float strength;

        public Cycle(int rainMin, int rainMax, int clearMin, int clearMax, float strength) {
            this.rainMin = rainMin;
            this.rainMax = rainMax;
            this.clearMin = clearMin;
            this.clearMax = clearMax;
            this.strength = strength;
        }
    }

    public static final class Sky {
        public final ResourceLocation sunTexture;
        public final float sunSize;
        @Nullable public final List<Body> bodies;
        public final int starCount;
        public final float starSize;

        public Sky(ResourceLocation sunTexture, float sunSize, @Nullable List<Body> bodies, int starCount, float starSize) {
            this.sunTexture = sunTexture;
            this.sunSize = sunSize;
            this.bodies = bodies;
            this.starCount = starCount;
            this.starSize = starSize;
        }
    }

    public static final class Body {
        public final ResourceLocation texture;
        public final float size;
        public final float angle;
        public final float tilt;
        public final boolean followsTime;

        public Body(ResourceLocation texture, float size, float angle, float tilt, boolean followsTime) {
            this.texture = texture;
            this.size = size;
            this.angle = angle;
            this.tilt = tilt;
            this.followsTime = followsTime;
        }
    }
}
