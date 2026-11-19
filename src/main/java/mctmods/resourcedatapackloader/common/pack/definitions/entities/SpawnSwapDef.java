package mctmods.resourcedatapackloader.common.pack.definitions.entities;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;

import java.util.List;
import javax.annotation.Nullable;

public record SpawnSwapDef(List<String> entities, List<PickDef> becomes, List<String> biomes, List<String> biomeTypes, List<BlockMatchDef> blocks, boolean requireAll, SpawnGateDef gate,
                           @Nullable Elsewhere elsewhere, int extra, boolean fromSpawners, String runs) {
    public record Elsewhere(float chance, int lightBelow, boolean night) {}

    public boolean swaps() { return !becomes.isEmpty(); }

    public boolean placed() { return !biomes.isEmpty() || !biomeTypes.isEmpty() || !blocks.isEmpty(); }
}
