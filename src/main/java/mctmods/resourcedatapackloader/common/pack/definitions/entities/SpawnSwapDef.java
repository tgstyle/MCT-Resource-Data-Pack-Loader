package mctmods.resourcedatapackloader.common.pack.definitions.entities;

import mctmods.resourcedatapackloader.common.blocks.BlockGroup;
import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;

public record SpawnSwapDef(List<Identifier> ids, List<String> kinds, SpawnGateDef gate, List<PickDef> becomes, List<String> biomes, List<String> biomeTypes, BlockGroup blocks, boolean requireAll,
                           @Nullable Elsewhere elsewhere, int extra, boolean fromSpawners, String runs) {
    public record Elsewhere(float chance, int lightBelow, boolean night) {}

    public boolean swaps() { return !becomes.isEmpty(); }

    public boolean placed() { return !biomes.isEmpty() || !biomeTypes.isEmpty() || !blocks.isEmpty(); }
}
