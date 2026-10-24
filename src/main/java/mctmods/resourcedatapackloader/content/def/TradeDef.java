package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.util.Standing;

import net.minecraft.util.ResourceLocation;
import java.util.List;
import javax.annotation.Nullable;

public final class TradeDef {
    public final ResourceLocation key;
    public final String profession;
    public final String career;
    public final int level;
    public final TradeStackDef buy;
    public final TradeStackDef buySecondary;
    public final TradeStackDef sell;
    public final int maxUses;
    public final List<String> requires;
    public final String advancement;
    public final Standing standing;
    @Nullable public final PriceScaleDef scale;

    public TradeDef(ResourceLocation key, String profession, String career, int level, TradeStackDef buy, TradeStackDef buySecondary, TradeStackDef sell, int maxUses, List<String> requires, String advancement, Standing standing, @Nullable PriceScaleDef scale) {
        this.key = key;
        this.profession = profession;
        this.career = career;
        this.level = level;
        this.buy = buy;
        this.buySecondary = buySecondary;
        this.sell = sell;
        this.maxUses = maxUses;
        this.requires = requires;
        this.advancement = advancement;
        this.standing = standing;
        this.scale = scale;
    }

    public boolean gated() { return !advancement.isEmpty() || standing.any() || scale != null; }
}
