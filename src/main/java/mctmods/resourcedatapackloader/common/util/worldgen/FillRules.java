package mctmods.resourcedatapackloader.common.util.worldgen;

import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public record FillRules(ResourceLocation key, List<PickDef> spawners, @Nullable ResourceLocation loot) {
    public boolean fills() { return !spawners.isEmpty() || loot != null; }
}
