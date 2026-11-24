package mctmods.resourcedatapackloader.common.fireworks;

import mctmods.resourcedatapackloader.common.pack.definitions.items.FireworkDef;

import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public final class FireworkDispensing {
    private FireworkDispensing() {}

    public static void register() {
        for (Map.Entry<Item, FireworkDef> entry : ContentFireworks.dispensing().entrySet()) {
            BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(entry.getKey(), new Launch(entry.getKey(), entry.getValue()));
        }
    }

    private static final class Launch extends BehaviorDefaultDispenseItem {
        private final Item item;
        private final FireworkDef def;

        Launch(Item item, FireworkDef def) {
            this.item = item;
            this.def = def;
        }

        @Override @Nonnull protected ItemStack dispenseStack(@Nonnull IBlockSource source, @Nonnull ItemStack stack) {
            EnumFacing facing = source.getBlockState().getValue(BlockDispenser.FACING);
            FireworkRockets.dispense(source.getWorld(), source.getBlockPos(), facing, FireworkRockets.stack(item, def), def.bursts());
            stack.shrink(1);
            return stack;
        }

        @Override protected void playDispenseSound(@Nonnull IBlockSource source) {}
    }
}
