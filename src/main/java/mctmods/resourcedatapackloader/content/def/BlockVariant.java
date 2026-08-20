package mctmods.resourcedatapackloader.content.def;

import net.minecraft.item.EnumRarity;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public final class BlockVariant {
    public final String name;
    public final int meta;
    public final EnumRarity rarity;
    public final int maxSize;
    public final List<String> oreDict;
    public final float hardness;
    public final float resistance;
    public final int harvestLevel;
    public final int light;
    @Nullable public final PortalDef portal;
    public final List<DropDef> drops;
    public final boolean hidden;

    public BlockVariant(String name, int meta, EnumRarity rarity, int maxSize, List<String> oreDict, float hardness, float resistance, int harvestLevel, int light, @Nullable PortalDef portal, List<DropDef> drops, boolean hidden) {
        this.name = name;
        this.meta = meta;
        this.rarity = rarity;
        this.maxSize = maxSize;
        this.oreDict = oreDict;
        this.hardness = hardness;
        this.resistance = resistance;
        this.harvestLevel = harvestLevel;
        this.light = light;
        this.portal = portal;
        this.drops = drops;
        this.hidden = hidden;
    }

    public static BlockVariant placeholder(String name, int meta) {
        return new BlockVariant(name, meta, EnumRarity.COMMON, 64, Collections.emptyList(), 1.0F, 5.0F, 0, 0, null, Collections.emptyList(), true);
    }
}
