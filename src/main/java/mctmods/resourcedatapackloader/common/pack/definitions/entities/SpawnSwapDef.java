package mctmods.resourcedatapackloader.common.pack.definitions.entities;

import mctmods.resourcedatapackloader.common.blocks.BlockGroup;
import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;

import java.util.List;

public final class SpawnSwapDef {
    public final List<String> entities;
    public final List<PickDef> becomes;
    public final List<String> biomes;
    public final List<String> biomeTypes;
    public final BlockGroup blocks;
    public final boolean requireAll;
    public final SpawnGateDef gate;
    public final boolean elsewhere;
    public final float chance;
    public final int lightBelow;
    public final boolean night;
    public final int extra;
    public final boolean fromSpawners;
    public final String runs;

    public SpawnSwapDef(List<String> entities, List<PickDef> becomes, List<String> biomes, List<String> biomeTypes, BlockGroup blocks, boolean requireAll, SpawnGateDef gate,
                        boolean elsewhere, float chance, int lightBelow, boolean night, int extra, boolean fromSpawners, String runs) {
        this.entities = entities;
        this.becomes = becomes;
        this.biomes = biomes;
        this.biomeTypes = biomeTypes;
        this.blocks = blocks;
        this.requireAll = requireAll;
        this.gate = gate;
        this.elsewhere = elsewhere;
        this.chance = chance;
        this.lightBelow = lightBelow;
        this.night = night;
        this.extra = extra;
        this.fromSpawners = fromSpawners;
        this.runs = runs;
    }

    public boolean swaps() { return !becomes.isEmpty(); }

    public boolean placed() { return !biomes.isEmpty() || !biomeTypes.isEmpty() || !blocks.isEmpty(); }
}
