package mctmods.resourcedatapackloader;

import mctmods.resourcedatapackloader.command.ClientCommands;
import mctmods.resourcedatapackloader.command.ServerCommands;
import mctmods.resourcedatapackloader.pack.PackFinder;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.nio.file.Path;

@Mod(ResourceDataPackLoader.MOD_ID) public class ResourceDataPackLoader {
    public static final String MOD_ID = "resourcedatapackloader";
    public static final Logger LOGGER = LogManager.getLogger("RDPL");

    public ResourceDataPackLoader(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        IEventBus modBus = context.getModEventBus();
        modBus.addListener(this::onConfig);
        modBus.addListener(this::onAddPackFinders);
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
        MinecraftForge.EVENT_BUS.addListener(this::beforeServerStart);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStopped);
        if (FMLEnvironment.dist == Dist.CLIENT) { MinecraftForge.EVENT_BUS.addListener(ClientCommands::register); }
    }

    private void onConfig(ModConfigEvent event) {
        if (event.getConfig().getSpec() != Config.SPEC) { return; }
        ContentLog.LOGGER.setDebug(Config.worldgen.worldgenDebug());
        LOGGER.info("Config packs: rootDirectory={} overrideResourcePacks={} warnOnCaseMismatch={} logContents={} traceUnresolvedVariables={}",
                Config.packs.rootDirectory(), Config.packs.overrideResourcePacks(), Config.packs.warnOnCaseMismatch(), Config.packs.logContents(), Config.packs.traceUnresolvedVariables());
        LOGGER.info("Config worldgen: worldgenDebug={}", Config.worldgen.worldgenDebug());
    }

    private void onAddPackFinders(AddPackFindersEvent event) { event.addRepositorySource(new PackFinder(event.getPackType())); }

    private void onRegisterCommands(RegisterCommandsEvent event) { ServerCommands.register(event.getDispatcher()); }

    private void beforeServerStart(ServerAboutToStartEvent event) {
        Path root = PackManager.get().getRoot();
        if (root == null) { return; }
        PackManager.get().scan(root);
        PackManager.get().report();
    }

    private void onServerStopped(ServerStoppedEvent event) {
        if (FMLEnvironment.dist == Dist.CLIENT) { return; }
        PackManager.get().close();
    }
}
