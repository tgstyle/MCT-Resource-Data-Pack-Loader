package mctmods.resourcedatapackloader.client.render;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.tile.TileEntityPackContainer;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = ResourceDataPackLoader.MOD_ID) public final class PackRenderBinder {
    private PackRenderBinder() {}

    @SubscribeEvent public static void bind(ModelRegistryEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPackContainer.class, new PackContainerRenderer());
    }
}
