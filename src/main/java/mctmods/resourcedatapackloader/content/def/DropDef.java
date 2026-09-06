package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import javax.annotation.Nullable;

public record DropDef(@Nullable ResourceLocation item, @Nullable ResourceLocation entity, AmountDef amount, int chance, int weight, int[] bonusChance) {
    public boolean isEntity() { return entity != null; }

    public boolean weighted() { return weight > 0; }

    public boolean hasBonus() { return bonusChance.length > 0; }

    public int chanceFor(int fortune) {
        if (bonusChance.length == 0) { return 0; }
        if (fortune < 0) { return bonusChance[0]; }
        return fortune >= bonusChance.length ? bonusChance[bonusChance.length - 1] : bonusChance[fortune];
    }
}
