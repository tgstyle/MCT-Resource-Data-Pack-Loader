package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.util.Standing;

import net.minecraft.resources.ResourceLocation;
import java.util.List;
import javax.annotation.Nullable;

public record TradeDef(ResourceLocation key, String profession, int level, TradeStackDef buy, TradeStackDef buySecondary, TradeStackDef sell, int maxUses, int xp, List<String> requires, String advancement, Standing standing, @Nullable PriceScaleDef scale) {
    public boolean gated() { return !advancement.isEmpty() || standing.any() || scale != null; }
}
