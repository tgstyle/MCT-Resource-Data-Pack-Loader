package mctmods.resourcedatapackloader;

import mctmods.resourcedatapackloader.core.registration.ModSetup;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = ResourceDataPackLoader.MOD_ID, name = "Resource Data Pack Loader", acceptedMinecraftVersions = "[1.12.2]", acceptableRemoteVersions = "*", dependencies = "required-after:blastplaster@[1.0.14,);after:baubles;before:jei;") public class ResourceDataPackLoader {
    public static final String MOD_ID = "resourcedatapackloader";
    public static final String ICON = "textures/gui/icon.png";
    @Mod.Instance(MOD_ID) public static ResourceDataPackLoader INSTANCE;

    public static ResourceLocation id(String path) { return new ResourceLocation(MOD_ID, path); }

    static { ModSetup.buckets(); }

    @Mod.EventHandler public void preInit(FMLPreInitializationEvent event) { ModSetup.preInit(); }

    @Mod.EventHandler public void init(FMLInitializationEvent event) { ModSetup.init(); }

    @Mod.EventHandler public void interModComms(FMLInterModComms.IMCEvent event) { ModSetup.interModComms(); }

    @Mod.EventHandler public void postInit(FMLPostInitializationEvent event) { ModSetup.postInit(); }

    @Mod.EventHandler public void loadComplete(FMLLoadCompleteEvent event) { ModSetup.loadComplete(); }

    @Mod.EventHandler public void beforeServerStart(FMLServerAboutToStartEvent event) { ModSetup.beforeServerStart(event.getServer()); }

    @Mod.EventHandler public void onServerStarting(FMLServerStartingEvent event) { ModSetup.serverCommands(event); }

    @Mod.EventHandler public void onServerStarted(FMLServerStartedEvent event) { ModSetup.onServerStarted(); }

    @Mod.EventHandler public void onServerStopping(FMLServerStoppingEvent event) { ModSetup.onServerStopping(); }

    @Mod.EventHandler public void onServerStopped(FMLServerStoppedEvent event) { ModSetup.onServerStopped(); }
}
