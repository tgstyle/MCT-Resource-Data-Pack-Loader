package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import mctmods.resourcedatapackloader.common.pack.definitions.portals.PortalDef;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public record BlockVariant(ResourceLocation id, String name, String rarity, int maxSize, List<String> tags, float hardness, float resistance, int harvestLevel, int light, List<DropDef> drops, @Nullable PortalDef portal) {}
