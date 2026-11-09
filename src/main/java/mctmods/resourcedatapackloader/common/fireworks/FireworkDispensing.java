package mctmods.resourcedatapackloader.common.fireworks;

import mctmods.resourcedatapackloader.common.pack.definitions.items.FireworkDef;

import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;

public final class FireworkDispensing {
    private FireworkDispensing() {}

    public static void register() {
        for (Map.Entry<Item, FireworkDef> entry : ContentFireworks.dispensing().entrySet()) {
            DispenserBlock.registerBehavior(entry.getKey(), new Launch(entry.getKey(), entry.getValue()));
        }
    }

    private static final class Launch extends DefaultDispenseItemBehavior {
        private final Item item;
        private final FireworkDef def;

        Launch(Item item, FireworkDef def) {
            this.item = item;
            this.def = def;
        }

        @Override @Nonnull protected ItemStack execute(@Nonnull BlockSource source, @Nonnull ItemStack stack) {
            Direction facing = source.state().getValue(DispenserBlock.FACING);
            FireworkRockets.dispense(source.level(), source.pos(), facing, FireworkRockets.stack(item, def), FireworkDef.MODE_BURST.equals(def.mode()));
            stack.shrink(1);
            return stack;
        }

        @Override protected void playSound(@Nonnull BlockSource source) {}
    }
}
