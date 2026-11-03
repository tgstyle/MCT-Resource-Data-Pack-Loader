package mctmods.resourcedatapackloader.common.blocks.compress;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;

public final class CompressedFuel {
    private static final int FACTOR = 10;

    private CompressedFuel() {}

    public static void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        ItemStack fuel = event.getItemStack();
        if (fuel.isEmpty()) { return; }
        ResourceLocation name = BuiltInRegistries.ITEM.getKey(fuel.getItem());
        CompressedSet set = ContentCompressables.owner(name);
        if (set == null) { return; }
        int layer = set.layerOf(fuel.getItem());
        ItemStack base = set.stack(0);
        if (layer < 1 || base.isEmpty()) { return; }
        int burn = base.getBurnTime(event.getRecipeType());
        if (burn <= 0) { return; }
        long total = burn;
        for (int step = 0; step < layer && total < Integer.MAX_VALUE; step++) { total *= FACTOR; }
        event.setBurnTime((int) Math.min(total, Integer.MAX_VALUE));
    }
}
