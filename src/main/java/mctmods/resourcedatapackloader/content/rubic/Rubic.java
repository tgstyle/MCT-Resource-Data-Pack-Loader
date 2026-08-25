package mctmods.resourcedatapackloader.content.rubic;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.client.RubicClientEvents;
import mctmods.resourcedatapackloader.content.rubic.server.chunkio.RegionCubeStorage;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicStorage;
import mctmods.resourcedatapackloader.content.rubic.world.storage.StorageFormatProviderBase;
import mctmods.resourcedatapackloader.content.rubic.worldgen.VanillaCompatibilityGeneratorProviderBase;
import mctmods.resourcedatapackloader.content.rubic.worldgen.WorldgenHangWatchdog;
import mctmods.resourcedatapackloader.content.rubic.worldgen.generator.VanillaCompatibilityGenerator;
import mctmods.resourcedatapackloader.util.SideUtils;

import net.minecraft.world.World;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ICrashCallable;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.io.IOException;
import java.nio.file.Path;
import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(modid = Rubic.MODID) public class Rubic {
    public static final int MAX_RENDER_DISTANCE = 64;
    public static final int MIN_SUPPORTED_BLOCK_Y = Integer.MIN_VALUE + 4096;
    public static final int MAX_SUPPORTED_BLOCK_Y = Integer.MAX_VALUE - 4095;
    public static final String MODID = "resourcedatapackloader";
    @Nonnull public static final ContentLog LOGGER = ContentLog.LOGGER;

    public static void preInit() {
        FMLCommonHandler.instance().registerCrashCallable(new ICrashCallable() {
            @Override public String getLabel() { return "Rubic WorldGen Hang Watchdog samples"; }
            @Override public String call() {
                String message = WorldgenHangWatchdog.getCrashInfo();
                if (message == null) { return "(no data)"; }
                return message;
            }
        });
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new RubicEvents());
        SideUtils.runForClient(() -> () -> MinecraftForge.EVENT_BUS.register(new RubicClientEvents()));
    }

    @SubscribeEvent public static void registerRegistries(RegistryEvent.NewRegistry evt) {
        VanillaCompatibilityGeneratorProviderBase.init();
        StorageFormatProviderBase.init();
    }

    @SubscribeEvent public static void registerVanillaCompatibilityGeneratorProvider(RegistryEvent.Register<VanillaCompatibilityGeneratorProviderBase> event) {
        event.getRegistry().register(new VanillaCompatibilityGeneratorProviderBase() {
            @Override public VanillaCompatibilityGenerator provideGenerator(IChunkGenerator vanillaChunkGenerator, World world) {
                return new VanillaCompatibilityGenerator(vanillaChunkGenerator, world);
            }
        }.setRegistryName(VanillaCompatibilityGeneratorProviderBase.DEFAULT));
    }

    @SubscribeEvent public static void registerAnvil3dStorageFormatProvider(RegistryEvent.Register<StorageFormatProviderBase> event) {
        event.getRegistry().register(new StorageFormatProviderBase() {
            @Override public IRubicStorage provideStorage(World world, Path path) throws IOException { return new RegionCubeStorage(path); }
        }.setRegistryName(StorageFormatProviderBase.DEFAULT));
    }

    public static void bigWarning(String format, Object... data)
    {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        LOGGER.warn("****************************************");
        LOGGER.warn("* {}", String.format(format, data));
        for (int i = 2; i < 10 && i < trace.length; i++)
        {
            LOGGER.warn("*  at {}{}", trace[i].toString(), i == 9 ? "..." : "");
        }
        LOGGER.warn("****************************************");
    }

    public static boolean hasOptifine() {
        return SideUtils.getForSide(
                () -> () -> FMLClientHandler.instance().hasOptifine(),
                () -> () -> false
        );
    }
}
