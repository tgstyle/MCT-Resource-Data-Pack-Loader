package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.Identifier;
import java.util.List;
import javax.annotation.Nullable;

public record BlockVariant(Identifier id, String name, String rarity, int maxSize, List<String> tags, float hardness, float resistance, int harvestLevel, int light, List<DropDef> drops, @Nullable PortalDef portal) {}
