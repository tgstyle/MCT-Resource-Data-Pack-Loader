package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;
import javax.annotation.Nullable;

public record OverrideDef(ResourceLocation target, ResourceLocation source, @Nullable Float hardness, @Nullable Float resistance, @Nullable Float slipperiness, @Nullable Integer light, @Nullable Integer lightOpacity, @Nullable String soundType,
                          @Nullable String harvestTool, int harvestToolLevel, @Nullable Integer flammability, int fireSpread, @Nullable Integer maxStackSize, @Nullable Integer maxDamage, @Nullable String containerItem,
                          @Nullable List<PotionEffectDef> effects, @Nullable FoodDef food, List<String> requires) {
    public boolean touchesBlock() {
        return hardness != null || resistance != null || slipperiness != null || light != null || lightOpacity != null || soundType != null || harvestTool != null || flammability != null;
    }

    public boolean touchesItem() { return maxStackSize != null || maxDamage != null || containerItem != null || food != null; }

    public boolean touchesPotionType() { return effects != null; }

    public record FoodDef(int heal, float saturation, boolean alwaysEdible, List<PotionEffectDef> effects) {}
}
