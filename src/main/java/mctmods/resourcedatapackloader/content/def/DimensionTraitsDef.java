package mctmods.resourcedatapackloader.content.def;

import net.minecraft.SharedConstants;
import net.minecraft.resources.Identifier;
import java.util.List;
import javax.annotation.Nullable;

public record DimensionTraitsDef(double gravity, double fallDamage, double arrowGravity, long dayLength, boolean precipitation, boolean lightning, boolean snow, boolean freeze, boolean alwaysRain,
                                 @Nullable Cycle cycle, @Nullable RainDef rain, @Nullable Sky sky, @Nullable Moon moon, @Nullable AmbienceDef ambience) {
    public static final long VANILLA_DAY = SharedConstants.TICKS_PER_GAME_DAY;
    public static final float VANILLA_SUN = 30.0F;
    public static final float VANILLA_MOON = 20.0F;
    public static final DimensionTraitsDef DEFAULTS = new DimensionTraitsDef(-1.0D, -1.0D, -1.0D, VANILLA_DAY, true, true, true, true, false, null, null, null, null, null);

    public record Cycle(int rainMin, int rainMax, int clearMin, int clearMax, float strength, int thunderMin, int thunderMax, int calmMin, int calmMax, float thunderStrength) {}

    public record Sky(Identifier sunTexture, float sunSize, @Nullable List<Body> bodies, int starCount, float starSize) {}

    public record Moon(@Nullable Identifier texture, float size) {}

    public record Body(Identifier texture, float size, float angle, float tilt, boolean followsTime) {}
}
