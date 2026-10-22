package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;
import java.util.Collections;
import java.util.List;

public final class RaidSwitchDef {
    public static final RaidSwitchDef NONE = new RaidSwitchDef("", 0, Collections.emptyList(), 1.0F, 1.0F);
    public final String owner;
    public final int every;
    public final List<ResourceLocation> start;
    private final float waveScale;
    private final float pointScale;

    public RaidSwitchDef(String owner, int every, List<ResourceLocation> start, float waveScale, float pointScale) {
        this.owner = owner;
        this.every = every;
        this.start = Collections.unmodifiableList(start);
        this.waveScale = waveScale;
        this.pointScale = pointScale;
    }

    public int waves(int given) { return Math.max(1, Math.round(given * waveScale)); }

    public int points(int given) { return Math.round(given * pointScale); }
}
