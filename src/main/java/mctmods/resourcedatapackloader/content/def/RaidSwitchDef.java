package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;

public final class RaidSwitchDef {
    public static final RaidSwitchDef NONE = new RaidSwitchDef("", 0, List.of(), 1.0F, 1.0F);
    private final String owner;
    private final int every;
    private final List<ResourceLocation> start;
    private final float waveScale;
    private final float pointScale;

    public RaidSwitchDef(String owner, int every, List<ResourceLocation> start, float waveScale, float pointScale) {
        this.owner = owner;
        this.every = every;
        this.start = start;
        this.waveScale = waveScale;
        this.pointScale = pointScale;
    }

    public String owner() { return owner; }

    public int every() { return every; }

    public List<ResourceLocation> start() { return start; }

    public int waves(int given) { return Math.max(1, Math.round(given * waveScale)); }

    public int points(int given) { return Math.round(given * pointScale); }
}
