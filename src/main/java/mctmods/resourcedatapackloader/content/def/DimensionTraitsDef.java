package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.Identifier;
import java.util.List;
import javax.annotation.Nullable;

public record DimensionTraitsDef(double gravity, double fallDamage, double arrowGravity, long dayLength, boolean precipitation, boolean lightning, boolean snow, boolean freeze, @Nullable Cycle cycle,
                                 @Nullable Sky sky) {
    public static final long VANILLA_DAY = 24000L;
    public static final float VANILLA_SUN = 30.0F;
    public static final DimensionTraitsDef DEFAULTS = new DimensionTraitsDef(-1.0D, -1.0D, -1.0D, VANILLA_DAY, true, true, true, true, null, null);

    public record Cycle(int rainMin, int rainMax, int clearMin, int clearMax, float strength) {}

    public record Sky(Identifier sunTexture, float sunSize, @Nullable List<Body> bodies, int starCount, float starSize) {}

    public record Body(Identifier texture, float size, float angle, float tilt, boolean followsTime) {}
}
