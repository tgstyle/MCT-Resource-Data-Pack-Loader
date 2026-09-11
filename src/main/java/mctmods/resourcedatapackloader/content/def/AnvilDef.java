package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;
import java.util.Map;

public final class AnvilDef {
    public final ResourceLocation registryName;
    public final String item;
    public final String with;
    public final int withCount;
    public final String result;
    public final int levels;
    public final Map<String, Integer> enchantments;
    public final String grants;
    public final boolean locks;

    public AnvilDef(ResourceLocation registryName, String item, String with, int withCount, String result, int levels, Map<String, Integer> enchantments, String grants, boolean locks) {
        this.registryName = registryName;
        this.item = item;
        this.with = with;
        this.withCount = withCount;
        this.result = result;
        this.levels = levels;
        this.enchantments = enchantments;
        this.grants = grants;
        this.locks = locks;
    }
}
