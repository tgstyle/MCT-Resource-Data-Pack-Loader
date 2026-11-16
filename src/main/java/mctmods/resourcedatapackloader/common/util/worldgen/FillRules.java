package mctmods.resourcedatapackloader.common.util.worldgen;

import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public final class FillRules {
    public final ResourceLocation key;
    public final List<PickDef> spawners;
    @Nullable public final ResourceLocation loot;

    public FillRules(ResourceLocation key, List<PickDef> spawners, @Nullable ResourceLocation loot) {
        this.key = key;
        this.spawners = spawners;
        this.loot = loot;
    }

    public boolean fills() { return !spawners.isEmpty() || loot != null; }
}
