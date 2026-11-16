package mctmods.resourcedatapackloader.common.util.worldgen;

import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;

public record FillRules(Identifier key, List<PickDef> spawners, @Nullable Identifier loot) {
    public boolean fills() { return !spawners.isEmpty() || loot != null; }
}
