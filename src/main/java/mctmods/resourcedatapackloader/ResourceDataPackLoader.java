package mctmods.resourcedatapackloader;

import mctmods.resourcedatapackloader.command.ClientCommands;
import mctmods.resourcedatapackloader.command.ServerCommands;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.registry.RegistryRemaps;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.nio.file.Path;

@Mod(modid = ResourceDataPackLoader.MOD_ID, name = "Resource Data Pack Loader", version = "@VERSION@", acceptedMinecraftVersions = "[1.12.2]", acceptableRemoteVersions = "*")
public class ResourceDataPackLoader {
    public static final String MOD_ID = "resourcedatapackloader";

    @Mod.EventHandler public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(RegistryRemaps.class);
        RegistryRemaps.reload();
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) { ClientCommands.register(); }
    }

    @Mod.EventHandler public void beforeServerStart(FMLServerAboutToStartEvent event) {
        Path root = PackManager.get().getRoot();
        if (root == null) { return; }
        PackManager.get().scan(root);
        PackManager.get().report();
        RegistryRemaps.reload();
    }

    @Mod.EventHandler public void onServerStarting(FMLServerStartingEvent event) { event.registerServerCommand(new ServerCommands()); }

    @Mod.EventHandler public void onServerStopped(FMLServerStoppedEvent event) { PackManager.get().close(); }
}
