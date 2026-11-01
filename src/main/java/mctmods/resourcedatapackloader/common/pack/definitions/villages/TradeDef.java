package mctmods.resourcedatapackloader.common.pack.definitions.villages;

import mctmods.resourcedatapackloader.common.util.entity.Standing;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public record TradeDef(ResourceLocation key, String profession, int level, TradeStackDef buy, TradeStackDef buySecondary, TradeStackDef sell, int maxUses, int xp, List<String> requires, String advancement, Standing standing, @Nullable PriceScaleDef scale) {
    public boolean gated() { return !advancement.isEmpty() || standing.any() || scale != null; }
}
