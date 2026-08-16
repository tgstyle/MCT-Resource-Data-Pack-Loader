package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;
import java.util.List;
import javax.annotation.Nullable;

public final class OverrideDef {
    public final ResourceLocation target;
    public final ResourceLocation source;
    @Nullable public final Float hardness;
    @Nullable public final Float resistance;
    @Nullable public final Float slipperiness;
    @Nullable public final Integer light;
    @Nullable public final Integer lightOpacity;
    @Nullable public final String soundType;
    @Nullable public final String harvestTool;
    @Nullable public final Integer harvestToolLevel;
    @Nullable public final Integer flammability;
    @Nullable public final Integer fireSpread;
    @Nullable public final Integer maxStackSize;
    @Nullable public final Integer maxDamage;
    @Nullable public final String containerItem;
    @Nullable public final List<PotionEffectDef> effects;
    @Nullable public final FoodDef food;
    public final List<String> requires;

    public OverrideDef(ResourceLocation target, ResourceLocation source, @Nullable Float hardness, @Nullable Float resistance, @Nullable Float slipperiness, @Nullable Integer light, @Nullable Integer lightOpacity, @Nullable String soundType, @Nullable String harvestTool, @Nullable Integer harvestToolLevel, @Nullable Integer flammability, @Nullable Integer fireSpread, @Nullable Integer maxStackSize, @Nullable Integer maxDamage, @Nullable String containerItem, @Nullable List<PotionEffectDef> effects, @Nullable FoodDef food, List<String> requires) {
        this.target = target;
        this.source = source;
        this.hardness = hardness;
        this.resistance = resistance;
        this.slipperiness = slipperiness;
        this.light = light;
        this.lightOpacity = lightOpacity;
        this.soundType = soundType;
        this.harvestTool = harvestTool;
        this.harvestToolLevel = harvestToolLevel;
        this.flammability = flammability;
        this.fireSpread = fireSpread;
        this.maxStackSize = maxStackSize;
        this.maxDamage = maxDamage;
        this.containerItem = containerItem;
        this.effects = effects;
        this.food = food;
        this.requires = requires;
    }

    public boolean touchesBlock() {
        return hardness != null || resistance != null || slipperiness != null || light != null || lightOpacity != null || soundType != null || harvestTool != null || flammability != null;
    }

    public boolean touchesItem() { return maxStackSize != null || maxDamage != null || containerItem != null || food != null; }

    public boolean touchesPotionType() { return effects != null; }

    public static final class FoodDef {
        public final int heal;
        public final float saturation;
        public final boolean alwaysEdible;
        public final List<PotionEffectDef> effects;

        public FoodDef(int heal, float saturation, boolean alwaysEdible, List<PotionEffectDef> effects) {
            this.heal = heal;
            this.saturation = saturation;
            this.alwaysEdible = alwaysEdible;
            this.effects = effects;
        }
    }
}
