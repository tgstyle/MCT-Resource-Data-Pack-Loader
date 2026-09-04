package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;

public record TradeDef(ResourceLocation key, String profession, String career, int level, TradeStackDef buy, TradeStackDef buySecondary, TradeStackDef sell, int maxUses, int xp, List<String> requires) {}
