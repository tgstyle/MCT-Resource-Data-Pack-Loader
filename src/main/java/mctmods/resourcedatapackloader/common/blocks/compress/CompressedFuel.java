package mctmods.resourcedatapackloader.common.blocks.compress;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = ResourceDataPackLoader.MOD_ID) public final class CompressedFuel {
    private static final int FACTOR = 10;

    private CompressedFuel() {}

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        ItemStack fuel = event.getItemStack();
        if (fuel.isEmpty()) { return; }
        ResourceLocation name = fuel.getItem().getRegistryName();
        CompressedSet set = name == null ? null : ContentCompressables.owner(name);
        if (set == null) { return; }
        int layer = set.layerOf(name, fuel.getMetadata());
        ItemStack base = set.stack(0);
        if (layer < 1 || base.isEmpty()) { return; }
        int burn = TileEntityFurnace.getItemBurnTime(base);
        if (burn <= 0) { return; }
        long total = burn;
        for (int step = 0; step < layer && total < Integer.MAX_VALUE; step++) { total *= FACTOR; }
        event.setBurnTime((int) Math.min(total, Integer.MAX_VALUE));
    }
}
