package mctmods.resourcedatapackloader.common.pack.definitions.entities;

import net.minecraft.util.ResourceLocation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

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
    public final Map<String, String> equipment;
    public final float dropChance;
    @Nullable public final Boolean picksUpLoot;
    public final boolean digs;
    public final String digItem;
    public final boolean breaksDoors;
    public final boolean steals;
    public final RankRandomDef random;
    @Nullable public final RankScalingDef scaling;

    public RankDef(ResourceLocation key, List<String> entities, int weight, String label, float health, float damage, Map<String, Integer> effects, float loot, List<String> dimensions, Map<String, String> equipment, float dropChance, @Nullable Boolean picksUpLoot, boolean digs, String digItem, boolean breaksDoors, boolean steals, RankRandomDef random, @Nullable RankScalingDef scaling) {
        this.key = key;
        this.entities = Collections.unmodifiableList(entities);
        this.weight = weight;
        this.label = label;
        this.health = health;
        this.damage = damage;
        this.effects = Collections.unmodifiableMap(effects);
        this.loot = loot;
        this.dimensions = Collections.unmodifiableList(dimensions);
        this.equipment = Collections.unmodifiableMap(equipment);
        this.dropChance = dropChance;
        this.picksUpLoot = picksUpLoot;
        this.digs = digs;
        this.digItem = digItem;
        this.breaksDoors = breaksDoors;
        this.steals = steals;
        this.random = random;
        this.scaling = scaling;
    }
}
