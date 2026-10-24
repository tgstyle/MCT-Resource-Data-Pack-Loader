package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;
import java.util.List;

public final class EnchantmentDef {
    public final ResourceLocation registryName;
    public final String name;
    public final int maxLevel;
    public final String rarity;
    public final List<String> slots;
    public final String category;
    public final List<String> items;
    public final int minBase;
    public final int minPerLevel;
    public final int maxBase;
    public final int maxPerLevel;
    public final boolean treasure;
    public final List<EnchantmentEffectDef> effects;
    public final List<String> requires;

    public EnchantmentDef(ResourceLocation registryName, String name, int maxLevel, String rarity, List<String> slots, String category, List<String> items, int minBase, int minPerLevel, int maxBase, int maxPerLevel, boolean treasure, List<EnchantmentEffectDef> effects, List<String> requires) {
        this.registryName = registryName;
        this.name = name;
        this.maxLevel = maxLevel;
        this.rarity = rarity;
        this.slots = slots;
        this.category = category;
        this.items = items;
        this.minBase = minBase;
        this.minPerLevel = minPerLevel;
        this.maxBase = maxBase;
        this.maxPerLevel = maxPerLevel;
        this.treasure = treasure;
        this.effects = effects;
        this.requires = requires;
    }
}
