package mctmods.resourcedatapackloader.content.def;

import net.minecraft.world.level.biome.MobSpawnSettings;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

public final class SpawnSwitchDef {
    public static final SpawnSwitchDef NONE = new SpawnSwitchDef("", new float[] {-1.0F, -1.0F, -1.0F, -1.0F}, new int[] {-1, -1, -1, -1}, Map.of());
    private final String owner;
    private final float[] rates;
    private final int[] caps;
    private final Map<String, Float> weights;
    private final Map<MobSpawnSettings.SpawnerData, MobSpawnSettings.SpawnerData> copies = Collections.synchronizedMap(new IdentityHashMap<>());

    public SpawnSwitchDef(String owner, float[] rates, int[] caps, Map<String, Float> weights) {
        this.owner = owner;
        this.rates = rates;
        this.caps = caps;
        this.weights = weights;
    }

    public String owner() { return owner; }

    public float rate(int index) { return rates[index]; }

    public int cap(int index) { return caps[index]; }

    public Map<String, Float> weights() { return weights; }

    public MobSpawnSettings.SpawnerData reweighted(MobSpawnSettings.SpawnerData held, int weight) {
        return copies.computeIfAbsent(held, original -> new MobSpawnSettings.SpawnerData(original.type, weight, original.minCount, original.maxCount));
    }
}
