package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;
import java.util.List;
import javax.annotation.Nullable;

public final class CelestialDef {
    public static final String SYSTEM = "system";
    public static final String STAR = "star";
    public static final String PLANET = "planet";
    public static final String MOON = "moon";
    public final ResourceLocation key;
    public final String kind;
    public final String name;
    public final String parent;
    public final ResourceLocation icon;
    public final float relativeSize;
    public final float distance;
    public final float scaledDistance;
    public final float orbitTime;
    public final float phaseShift;
    public final int ringColor;
    public final int tier;
    public final String galaxy;
    public final float mapX;
    public final float mapY;
    public final float mapZ;
    @Nullable public final CelestialDef star;
    public final List<String> requires;
    @Nullable public final GalaxySpaceDef galaxySpace;

    public CelestialDef(ResourceLocation key, String kind, String name, String parent, ResourceLocation icon, float relativeSize, float distance, float scaledDistance, float orbitTime, float phaseShift, int ringColor, int tier, String galaxy, float mapX, float mapY, float mapZ, @Nullable CelestialDef star, List<String> requires, @Nullable GalaxySpaceDef galaxySpace) {
        this.key = key;
        this.kind = kind;
        this.name = name;
        this.parent = parent;
        this.icon = icon;
        this.relativeSize = relativeSize;
        this.distance = distance;
        this.scaledDistance = scaledDistance;
        this.orbitTime = orbitTime;
        this.phaseShift = phaseShift;
        this.ringColor = ringColor;
        this.tier = tier;
        this.galaxy = galaxy;
        this.mapX = mapX;
        this.mapY = mapY;
        this.mapZ = mapZ;
        this.star = star;
        this.requires = requires;
        this.galaxySpace = galaxySpace;
    }
}
