package mctmods.resourcedatapackloader;

import mctmods.resourcedatapackloader.command.ClientCommands;
import mctmods.resourcedatapackloader.command.ServerCommands;
import mctmods.resourcedatapackloader.pack.PackFinder;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.nio.file.Path;

@Mod(ResourceDataPackLoader.MOD_ID) public class ResourceDataPackLoader {
    public static final String MOD_ID = "resourcedatapackloader";
    public static final Logger LOGGER = LogManager.getLogger("RDPL");

    public ResourceDataPackLoader(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modBus.addListener(this::onConfig);
        modBus.addListener(this::onAddPackFinders);
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(this::beforeServerStart);
        NeoForge.EVENT_BUS.addListener(this::onServerStopped);
        if (FMLEnvironment.dist == Dist.CLIENT) { NeoForge.EVENT_BUS.addListener(ClientCommands::register); }
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
