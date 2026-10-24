package mctmods.resourcedatapackloader.content.def;

import net.minecraft.item.ItemStack;
import java.util.List;

public final class AffixDef {
    public final ItemStack item;
    public final int minRolls;
    public final int maxRolls;
    public final int minSockets;
    public final int maxSockets;
    public final List<AffixEntry> affixes;

    public AffixDef(ItemStack item, int[] rolls, int[] sockets, List<AffixEntry> affixes) {
        this.item = item;
        this.minRolls = rolls[0];
        this.maxRolls = rolls[1];
        this.minSockets = sockets[0];
        this.maxSockets = sockets[1];
        this.affixes = affixes;
    }
}
