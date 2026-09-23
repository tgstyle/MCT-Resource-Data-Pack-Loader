package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;
import javax.annotation.Nullable;

public record BlockVariant(ResourceLocation id, String name, String rarity, int maxSize, List<String> tags, float hardness, float resistance, int harvestLevel, int light, List<DropDef> drops, @Nullable PortalDef portal) {}
