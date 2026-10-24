package mctmods.resourcedatapackloader.content.def;

import net.minecraft.world.item.ItemStack;
import java.util.List;

public record AffixDef(ItemStack item, int minRolls, int maxRolls, int minSockets, int maxSockets, List<AffixEntry> affixes) {}
