package mctmods.resourcedatapackloader.content.def;

import net.minecraft.item.EnumRarity;
import net.minecraft.potion.PotionEffect;
import java.util.List;
import javax.annotation.Nullable;

public final class ItemVariant {
    public final String name;
    public final int meta;
    public final EnumRarity rarity;
    public final int maxSize;
    public final List<String> oreDict;
    public final int healAmount;
    public final float saturation;
    @Nullable public final String potion;
    @Nullable private PotionEffect resolvedPotion;

    public ItemVariant(String name, int meta, EnumRarity rarity, int maxSize, List<String> oreDict, int healAmount, float saturation, @Nullable String potion) {
        this.name = name;
        this.meta = meta;
        this.rarity = rarity;
        this.maxSize = maxSize;
        this.oreDict = oreDict;
        this.healAmount = healAmount;
        this.saturation = saturation;
        this.potion = potion;
    }

    public void resolvePotion(@Nullable PotionEffect effect) { this.resolvedPotion = effect; }

    @Nullable public PotionEffect getResolvedPotion() { return resolvedPotion; }
}
