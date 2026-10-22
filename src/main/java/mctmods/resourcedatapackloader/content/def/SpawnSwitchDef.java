package mctmods.resourcedatapackloader.content.def;

import net.minecraft.world.biome.Biome;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

public final class SpawnSwitchDef {
    public static final SpawnSwitchDef NONE = new SpawnSwitchDef("", filled(-1.0F), filled(-1), Collections.emptyMap());
    public static final int SLOTS = 4;
    public final String owner;
    private final float[] rates;
    private final int[] caps;
    public final Map<String, Float> weights;
    private final Map<Biome.SpawnListEntry, Biome.SpawnListEntry> memo = Collections.synchronizedMap(new IdentityHashMap<>());

    public SpawnSwitchDef(String owner, float[] rates, int[] caps, Map<String, Float> weights) {
        this.owner = owner;
        this.rates = rates;
        this.caps = caps;
        this.weights = Collections.unmodifiableMap(weights);
    }

    public static float[] filled(float value) {
        float[] values = new float[SLOTS];
        Arrays.fill(values, value);
        return values;
    }

    public static int[] filled(int value) {
        int[] values = new int[SLOTS];
        Arrays.fill(values, value);
        return values;
    }

    public float rate(boolean sky, boolean day) { return this.rates[(sky ? 0 : 2) + (day ? 0 : 1)]; }

    public int cap(int slot) { return this.caps[slot]; }

    public Biome.SpawnListEntry scaled(Biome.SpawnListEntry entry, float multiplier) {
        int weight = Math.max(1, Math.round(entry.itemWeight * multiplier));
        if (weight == entry.itemWeight) { return entry; }
        return this.memo.computeIfAbsent(entry, original -> new Biome.SpawnListEntry(original.entityClass, weight, original.minGroupCount, original.maxGroupCount));
    }
}
