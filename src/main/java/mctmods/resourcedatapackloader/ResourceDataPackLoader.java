package mctmods.resourcedatapackloader;

import mctmods.resourcedatapackloader.command.Commands;
import mctmods.resourcedatapackloader.pack.PackManager;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;

import java.nio.file.Path;

@Mod(modid = ResourceDataPackLoader.MOD_ID, name = "Resource Data Pack Loader", version = "@VERSION@", acceptedMinecraftVersions = "[1.12.2]", acceptableRemoteVersions = "*")
public class ResourceDataPackLoader {
    public static final String MOD_ID = "resourcedatapackloader";

    @Mod.EventHandler public void beforeServerStart(FMLServerAboutToStartEvent event) {
        Path root = PackManager.get().getRoot();
        if (root == null) { return; }
        PackManager.get().scan(root);
        PackManager.get().report();
    }

    @Mod.EventHandler public void onServerStarting(FMLServerStartingEvent event) { event.registerServerCommand(new Commands()); }

    @Mod.EventHandler public void onServerStopped(FMLServerStoppedEvent event) { PackManager.get().close(); }
}
