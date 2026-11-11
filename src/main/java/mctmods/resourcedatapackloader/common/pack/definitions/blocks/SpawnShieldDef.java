package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import java.util.List;
import net.minecraft.util.ResourceLocation;

public final class SpawnShieldDef {
    public final int radius;
    public final Mobs mobs;
    public final List<ResourceLocation> entities;

    public SpawnShieldDef(int radius, Mobs mobs, List<ResourceLocation> entities) {
        this.radius = radius;
        this.mobs = mobs;
        this.entities = entities;
    }

    public enum Mobs { HOSTILE, PASSIVE, ALL }
}
