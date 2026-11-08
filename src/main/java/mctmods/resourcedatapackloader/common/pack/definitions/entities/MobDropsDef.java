package mctmods.resourcedatapackloader.common.pack.definitions.entities;

import mctmods.resourcedatapackloader.common.pack.definitions.containers.FilterDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.AmountDef;

import java.util.List;
import net.minecraft.item.ItemStack;

public final class MobDropsDef {
    public final List<String> entities;
    public final List<FilterDef.Entry> strip;
    public final boolean stripTagged;
    public final List<Add> adds;

    public MobDropsDef(List<String> entities, List<FilterDef.Entry> strip, boolean stripTagged, List<Add> adds) {
        this.entities = entities;
        this.strip = strip;
        this.stripTagged = stripTagged;
        this.adds = adds;
    }

    public static final class Add {
        public final ItemStack item;
        public final AmountDef count;
        public final float chance;
        public final List<FilterDef.Entry> killerHolds;
        public final List<String> damageTypes;
        public final boolean requireAll;
        public final List<FilterDef.Entry> unlessDropped;

        public Add(ItemStack item, AmountDef count, float chance, List<FilterDef.Entry> killerHolds, List<String> damageTypes, boolean requireAll, List<FilterDef.Entry> unlessDropped) {
            this.item = item;
            this.count = count;
            this.chance = chance;
            this.killerHolds = killerHolds;
            this.damageTypes = damageTypes;
            this.requireAll = requireAll;
            this.unlessDropped = unlessDropped;
        }
    }
}
