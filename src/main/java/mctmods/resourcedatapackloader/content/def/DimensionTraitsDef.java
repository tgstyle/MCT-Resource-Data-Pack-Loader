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
    @Nullable public final RainDef rain;
    @Nullable public final Sky sky;
    @Nullable public final AmbienceDef ambience;

    public DimensionTraitsDef(double gravity, double fallDamage, double arrowGravity, long dayLength, boolean precipitation, boolean lightning, boolean snow, boolean freeze, @Nullable Cycle cycle, @Nullable RainDef rain, @Nullable Sky sky,
                              @Nullable AmbienceDef ambience) {
        this.gravity = gravity;
        this.fallDamage = fallDamage;
        this.arrowGravity = arrowGravity;
        this.dayLength = dayLength;
        this.precipitation = precipitation;
        this.lightning = lightning;
        this.snow = snow;
        this.freeze = freeze;
        this.cycle = cycle;
        this.rain = rain;
        this.sky = sky;
        this.ambience = ambience;
    }

    public static final class Cycle {
        public final int rainMin;
        public final int rainMax;
        public final int clearMin;
        public final int clearMax;
        public final float strength;
        public final int thunderMin;
        public final int thunderMax;
        public final int calmMin;
        public final int calmMax;
        public final float thunderStrength;

        public Cycle(int[] rain, int[] clear, float strength, int[] thunder, int[] calm, float thunderStrength) {
            this.rainMin = rain[0];
            this.rainMax = rain[1];
            this.clearMin = clear[0];
            this.clearMax = clear[1];
            this.strength = strength;
            this.thunderMin = thunder[0];
            this.thunderMax = thunder[1];
            this.calmMin = calm[0];
            this.calmMax = calm[1];
            this.thunderStrength = thunderStrength;
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
