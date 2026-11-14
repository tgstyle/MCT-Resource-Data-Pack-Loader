package mctmods.resourcedatapackloader.core.integration.adastra;

import mctmods.resourcedatapackloader.common.pack.definitions.sky.RocketDef;

import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.common.fluid.base.ItemFluidContainer;
import earth.terrarium.botarium.common.item.ItemStackHolder;
import javax.annotation.Nonnull;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class RocketSlot extends Slot {
    private final RocketDef def;
    private ItemStack offered = ItemStack.EMPTY;

    private RocketSlot(RocketDef def, Container rocket, int index, int x, int y) {
        super(rocket, index, x, y);
        this.def = def;
    }

    public static Slot made(Entity rocket, Container container, int index, int x, int y) {
        RocketDef def = AdAstraRockets.def(rocket);
        return def == null ? new Slot(container, index, x, y) : new RocketSlot(def, container, index, x, y);
    }

    public static Slot fuel(Entity rocket, Container container, int index, int x, int y) {
        if (AdAstraRockets.def(rocket) == null) { return new Slot(container, index, x, y); }
        return new Slot(container, index, x, y) {
            @Override public boolean mayPlace(@Nonnull ItemStack stack) { return holdsFluid(stack); }
        };
    }

    private static boolean holdsFluid(ItemStack stack) {
        ItemFluidContainer holder = FluidContainer.of(new ItemStackHolder(stack));
        return holder != null && !holder.getFirstFluid().isEmpty();
    }

    @Override public boolean mayPlace(@Nonnull ItemStack stack) {
        offered = stack;
        return AdAstraRockets.room(def, container, stack) > 0;
    }

    @Override public int getMaxStackSize() {
        ItemStack here = getItem();
        return cap(here.isEmpty() ? offered : here);
    }

    @Override public int getMaxStackSize(@Nonnull ItemStack stack) { return cap(stack); }

    private int cap(ItemStack stack) {
        if (stack.isEmpty()) { return container.getMaxStackSize(); }
        ItemStack here = getItem();
        long most = (long) AdAstraRockets.room(def, container, stack) + (ItemStack.isSameItem(here, stack) ? here.getCount() : 0);
        return (int) Math.min(Math.min(container.getMaxStackSize(), stack.getMaxStackSize()), most);
    }
}
