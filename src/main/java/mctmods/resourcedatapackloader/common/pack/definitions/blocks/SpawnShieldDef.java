package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import java.util.List;
import net.minecraft.resources.ResourceLocation;

public record SpawnShieldDef(int radius, Mobs mobs, List<ResourceLocation> entities, List<ResourceLocation> entityTags) {
    public enum Mobs { HOSTILE, PASSIVE, ALL }
}
