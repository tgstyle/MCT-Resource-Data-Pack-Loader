package mctmods.resourcedatapackloader.client.renderer;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.blocks.bell.logic.TileEntityPackBell;
import mctmods.resourcedatapackloader.common.blocks.container.logic.TileEntityPackContainer;
import mctmods.resourcedatapackloader.common.entities.ReturningThrow;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = ResourceDataPackLoader.MOD_ID) public final class PackRenderBinder {
    private PackRenderBinder() {}

    @SubscribeEvent public static void bind(ModelRegistryEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPackContainer.class, new PackContainerRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPackBell.class, new PackBellRenderer());
        RenderingRegistry.registerEntityRenderingHandler(ReturningThrow.class, RenderReturningThrow::new);
    }
}
