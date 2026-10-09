package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.Identifier;
import java.util.List;

public record TradeDef(Identifier key, String profession, int level, TradeStackDef buy, TradeStackDef buySecondary, TradeStackDef sell, int maxUses, int xp, List<String> requires) {}
