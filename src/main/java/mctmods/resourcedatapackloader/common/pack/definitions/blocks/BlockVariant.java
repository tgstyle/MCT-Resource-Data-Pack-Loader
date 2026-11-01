package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import mctmods.resourcedatapackloader.common.pack.definitions.portals.PortalDef;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;

public record BlockVariant(Identifier id, String name, String rarity, int maxSize, List<String> tags, float hardness, float resistance, int harvestLevel, int light, List<DropDef> drops, @Nullable PortalDef portal) {}
