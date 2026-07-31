package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;
import java.util.List;

public final class PotionDef {
    public final ResourceLocation registryName;
    public final String name;
    public final boolean badEffect;
    public final boolean beneficial;
    public final int liquidColor;
    public final int iconX;
    public final int iconY;
    public final String iconTexture;
    public final boolean instant;
    public final double effectiveness;
    public final List<AttributeDef> attributes;
    public final List<String> requires;

    public PotionDef(ResourceLocation registryName, String name, boolean badEffect, boolean beneficial, int liquidColor, int iconX, int iconY, String iconTexture, boolean instant, double effectiveness, List<AttributeDef> attributes, List<String> requires) {
        this.registryName = registryName;
        this.name = name;
        this.badEffect = badEffect;
        this.beneficial = beneficial;
        this.liquidColor = liquidColor;
        this.iconX = iconX;
        this.iconY = iconY;
        this.iconTexture = iconTexture;
        this.instant = instant;
        this.effectiveness = effectiveness;
        this.attributes = attributes;
        this.requires = requires;
    }
}
