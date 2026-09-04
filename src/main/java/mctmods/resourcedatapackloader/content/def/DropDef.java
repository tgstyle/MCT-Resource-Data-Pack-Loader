package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.util.WeightedPicks;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public final class DropDef {
    public final ResourceLocation block;
    @Nullable public final ResourceLocation entity;
    public final int meta;
    public final AmountDef amount;
    public final int chance;
    public final int weight;
    public final int[] bonusChance;
    private Item resolved;

    public DropDef(ResourceLocation block, @Nullable ResourceLocation entity, int meta, AmountDef amount, int chance, int weight, int[] bonusChance) {
        this.block = block;
        this.entity = entity;
        this.meta = meta;
        this.amount = amount;
        this.chance = chance;
        this.weight = weight;
        this.bonusChance = bonusChance;
    }

    public boolean isEntity() { return entity != null; }

    public void resolve(Item item) { this.resolved = item; }

    public Item getResolved() { return resolved; }

    public int chanceFor(int fortune) {
        if (bonusChance.length == 0) { return 0; }
        if (fortune < 0) { return bonusChance[0]; }
        return fortune >= bonusChance.length ? bonusChance[bonusChance.length - 1] : bonusChance[fortune];
    }

    @Nullable public static DropDef pick(List<DropDef> pool, Random random) { return WeightedPicks.pick(pool, drop -> Math.max(1, drop.weight), random); }
}
