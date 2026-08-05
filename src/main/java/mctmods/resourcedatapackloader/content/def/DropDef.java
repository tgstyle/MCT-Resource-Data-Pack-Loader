package mctmods.resourcedatapackloader.content.def;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public final class DropDef {
    public final ResourceLocation block;
    public final int meta;
    public final int amount;
    public final boolean guaranteed;
    public final int[] bonusChance;
    private Item resolved;

    public DropDef(ResourceLocation block, int meta, int amount, boolean guaranteed, int[] bonusChance) {
        this.block = block;
        this.meta = meta;
        this.amount = amount;
        this.guaranteed = guaranteed;
        this.bonusChance = bonusChance;
    }

    public void resolve(Item item) { this.resolved = item; }

    public Item getResolved() { return resolved; }

    public int chanceFor(int fortune) {
        if (bonusChance.length == 0) { return 0; }
        if (fortune < 0) { return bonusChance[0]; }
        return fortune >= bonusChance.length ? bonusChance[bonusChance.length - 1] : bonusChance[fortune];
    }
}
