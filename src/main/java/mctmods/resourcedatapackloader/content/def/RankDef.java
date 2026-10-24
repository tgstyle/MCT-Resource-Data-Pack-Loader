package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class RankDef {
    public final ResourceLocation key;
    public final List<String> entities;
    public final int weight;
    public final String label;
    public final float health;
    public final float damage;
    public final Map<String, Integer> effects;
    public final float loot;
    public final List<String> dimensions;

    public RankDef(ResourceLocation key, List<String> entities, int weight, String label, float health, float damage, Map<String, Integer> effects, float loot, List<String> dimensions) {
        this.key = key;
        this.entities = Collections.unmodifiableList(entities);
        this.weight = weight;
        this.label = label;
        this.health = health;
        this.damage = damage;
        this.effects = Collections.unmodifiableMap(effects);
        this.loot = loot;
        this.dimensions = Collections.unmodifiableList(dimensions);
    }
}
