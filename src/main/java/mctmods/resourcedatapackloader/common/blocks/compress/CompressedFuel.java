package mctmods.resourcedatapackloader.common.blocks.compress;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public final class CompressedFuel {
    private static final int FACTOR = 10;

    private CompressedFuel() {}

    public static void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        ItemStack fuel = event.getItemStack();
        if (fuel.isEmpty()) { return; }
        ResourceLocation name = ForgeRegistries.ITEMS.getKey(fuel.getItem());
        CompressedSet set = ContentCompressables.owner(name);
        if (set == null) { return; }
        int layer = set.layerOf(fuel.getItem());
        ItemStack base = set.source();
        if (layer < set.low() || base.isEmpty()) { return; }
        int burn = ForgeHooks.getBurnTime(base, event.getRecipeType());
        if (burn <= 0) { return; }
        long total = burn;
        for (int step = 0; step < set.oreLevel(layer) && total < Integer.MAX_VALUE; step++) { total *= FACTOR; }
        event.setBurnTime((int) Math.min(total, Integer.MAX_VALUE));
    }
}
