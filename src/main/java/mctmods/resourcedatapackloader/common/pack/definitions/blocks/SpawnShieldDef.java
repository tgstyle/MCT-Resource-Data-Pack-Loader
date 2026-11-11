package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import java.util.List;
import net.minecraft.resources.Identifier;

public record SpawnShieldDef(int radius, Mobs mobs, List<Identifier> entities, List<Identifier> entityTags) {
    public enum Mobs { HOSTILE, PASSIVE, ALL }
}
