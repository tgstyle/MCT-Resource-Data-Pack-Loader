package mctmods.resourcedatapackloader.common.pack.definitions.entities;

import mctmods.resourcedatapackloader.common.pack.definitions.items.AmountDef;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.common.util.ItemSelection;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record MobDropsDef(List<Identifier> ids, List<String> kinds, ItemSelection strip, boolean stripTagged, List<Add> adds) {
    public static final class Add {
        private final Identifier owner;
        private final String item;
        private final AmountDef count;
        private final float chance;
        private final ItemSelection killerHolds;
        private final List<Identifier> damageTypes;
        private final boolean requireAll;
        private final ItemSelection unlessDropped;
        @Nullable private Item found;
        private boolean looked;

        public Add(Identifier owner, String item, AmountDef count, float chance, ItemSelection killerHolds, List<Identifier> damageTypes, boolean requireAll, ItemSelection unlessDropped) {
            this.owner = owner;
            this.item = item;
            this.count = count;
            this.chance = chance;
            this.killerHolds = killerHolds;
            this.damageTypes = damageTypes;
            this.requireAll = requireAll;
            this.unlessDropped = unlessDropped;
        }

        public float chance() { return chance; }

        public AmountDef count() { return count; }

        public ItemSelection unlessDropped() { return unlessDropped; }

        public boolean holds(ItemStack killerStack, @Nullable Identifier damageType) {
            int given = 0;
            int met = 0;
            if (!killerHolds.unnamed()) {
                given++;
                if (killerHolds.matches(killerStack)) { met++; }
            }
            if (!damageTypes.isEmpty()) {
                given++;
                if (damageType != null && damageTypes.contains(damageType)) { met++; }
            }
            return given == 0 || (requireAll ? met == given : met > 0);
        }

        @Nullable public Item item() {
            if (!looked) {
                looked = true;
                found = ContentStacks.find(owner, item);
            }
            return found;
        }
    }
}
