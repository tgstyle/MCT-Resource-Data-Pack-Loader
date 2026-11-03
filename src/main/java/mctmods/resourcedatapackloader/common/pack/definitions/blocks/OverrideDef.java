package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import mctmods.resourcedatapackloader.common.pack.definitions.effects.PotionEffectDef;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;

public record OverrideDef(Identifier target, Identifier source, @Nullable Float hardness, @Nullable Float resistance, @Nullable Float slipperiness, @Nullable Integer light, @Nullable Integer lightOpacity, @Nullable String soundType,
                          @Nullable String harvestTool, int harvestToolLevel, @Nullable Integer flammability, int fireSpread, @Nullable Integer maxStackSize, @Nullable Integer maxDamage, @Nullable String containerItem,
                          @Nullable List<PotionEffectDef> effects, @Nullable FoodDef food, List<String> requires, int weight) {
    public boolean touchesBlock() { return hardness != null || resistance != null || slipperiness != null || light != null || lightOpacity != null || soundType != null || harvestTool != null || flammability != null; }

    public boolean touchesItem() { return maxStackSize != null || maxDamage != null || containerItem != null || food != null || weight >= 0; }

    public boolean touchesPotionType() { return effects != null; }

    public record FoodDef(int heal, float saturation, boolean alwaysEdible, List<PotionEffectDef> effects) {}
}
