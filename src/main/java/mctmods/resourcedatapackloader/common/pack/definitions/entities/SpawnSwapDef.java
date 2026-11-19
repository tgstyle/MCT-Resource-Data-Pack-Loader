package mctmods.resourcedatapackloader.common.pack.definitions.entities;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;

import java.util.List;
import net.minecraft.resources.ResourceLocation;

public record SpawnSwapDef(ResourceLocation key, List<String> entities, List<PickDef> becomes, List<String> biomes, List<String> biomeTypes, List<BlockMatchDef> blocks, boolean requireAll,
                           SpawnGateDef gate, boolean elsewhere, float chance, int lightBelow, boolean night, int extra, boolean fromSpawners, String runs) {
    public boolean swaps() { return !becomes.isEmpty(); }

    public boolean placed() { return !biomes.isEmpty() || !biomeTypes.isEmpty() || !blocks.isEmpty(); }
}
