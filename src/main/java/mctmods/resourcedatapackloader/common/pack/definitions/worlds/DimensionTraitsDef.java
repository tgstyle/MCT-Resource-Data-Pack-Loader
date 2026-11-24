package mctmods.resourcedatapackloader.common.pack.definitions.worlds;

import mctmods.resourcedatapackloader.common.pack.definitions.sky.AmbienceDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.RainDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.SkyPoseDef;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public record DimensionTraitsDef(double gravity, double fallDamage, double arrowGravity, long dayLength, boolean precipitation, boolean lightning, boolean snow, boolean freeze, boolean alwaysRain,
                                 @Nullable Cycle cycle, @Nullable RainDef rain, @Nullable Sky sky, @Nullable Moon moon, @Nullable AmbienceDef ambience, @Nullable DarkDamage darkDamage) {
    public static final long VANILLA_DAY = Level.TICKS_PER_DAY;
    public static final float VANILLA_SUN = 30.0F;
    public static final float VANILLA_MOON = 20.0F;

    public static boolean inWindow(long tick, long from, long to, boolean endInclusive) {
        boolean beforeEnd = endInclusive ? tick <= to : tick < to;
        return from <= to ? tick >= from && beforeEnd : tick >= from || beforeEnd;
    }
    public static final DimensionTraitsDef DEFAULTS = new DimensionTraitsDef(-1.0D, -1.0D, -1.0D, VANILLA_DAY, true, true, true, true, false, null, null, null, null, null, null);

    public DimensionTraitsDef withSky(Sky held) { return new DimensionTraitsDef(gravity, fallDamage, arrowGravity, dayLength, precipitation, lightning, snow, freeze, alwaysRain, cycle, rain, held, moon, ambience, darkDamage); }

    public record Cycle(int rainMin, int rainMax, int clearMin, int clearMax, float strength, int thunderMin, int thunderMax, int calmMin, int calmMax, float thunderStrength) {}

    public record Sky(ResourceLocation sunTexture, float sunSize, float sunTilt, SkyPoseDef sunPose, @Nullable List<Body> bodies, int starCount, float starSize) {
        public Sky with(Body body) {
            List<Body> held = new ArrayList<>(bodies == null ? List.of() : bodies);
            held.add(body);
            return new Sky(sunTexture, sunSize, sunTilt, sunPose, List.copyOf(held), starCount, starSize);
        }
    }

    public record Moon(ResourceLocation texture, float size, float tilt, SkyPoseDef pose) {}

    public record Body(ResourceLocation texture, float size, float angle, float tilt, boolean followsTime, SkyPoseDef pose, String gate) {}

    public record DarkDamage(float damage, int interval, float brightness) {}
}
