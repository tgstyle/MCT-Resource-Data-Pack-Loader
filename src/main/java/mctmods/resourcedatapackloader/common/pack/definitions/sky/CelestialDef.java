package mctmods.resourcedatapackloader.common.pack.definitions.sky;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public record CelestialDef(ResourceLocation key, String kind, String name, String parent, ResourceLocation icon, float relativeSize, float distance, float orbitTime, float phaseShift, int ringColor, @Nullable ResourceLocation ring, float ringSize, RingFront ringFront, float beltWidth, List<String> requires) {
    public static final String SYSTEM = "system";
    public static final String PLANET = "planet";
    public static final String MOON = "moon";
    public static final float DEFAULT_RING_SIZE = 2.5F;

    public boolean isMoon() { return MOON.equals(kind); }

    public boolean isBelt() { return beltWidth > 0.0F; }

    public boolean named(String asked) { return key.toString().equals(asked) || key.getPath().equals(asked) || name.equalsIgnoreCase(asked); }
}
