package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.content.ContentHardness;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldgen;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.FmlWorldgen;
import mctmods.resourcedatapackloader.util.world.GenHeights;

import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.Loader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class GcPlanetWorldgen {
    private static Handler handler;

    private GcPlanetWorldgen() {}

    public static void register() {
        if (!Loader.isModLoaded("galacticraftcore")) { return; }
        handler = new Handler();
        ContentLog.LOGGER.info("Galacticraft keeps other mods' worldgen off its planets unless whitelisted; the pack's own ores and plants still run on the pack's own planets, and rubic asks the same gate before each generator it runs");
    }

    public static boolean blocks(World world, IWorldGenerator generator) { return handler != null && handler.blocks(world, generator); }

    public static void generateOwn(int chunkX, int chunkZ, World world, IChunkGenerator generator, IChunkProvider provider) {
        if (handler != null) { handler.generateOwn(chunkX, chunkZ, world, generator, provider); }
    }

    static final class Handler {
        private List<Class<?>> whitelist;

        void generateOwn(int chunkX, int chunkZ, World world, IChunkGenerator generator, IChunkProvider provider) {
            if (!(world.provider instanceof GcWorldProvider) || ConfigManagerCore.enableOtherModsFeatures || GenHeights.rubic(world)) { return; }
            long seed = FmlWorldgen.chunkSeed(world, chunkX, chunkZ);
            Random random = new Random();
            for (IWorldGenerator each : FmlWorldgen.sorted()) {
                if (!own(each)) { continue; }
                random.setSeed(seed);
                each.generate(random, chunkX, chunkZ, world, generator, provider);
            }
        }

        boolean blocks(World world, IWorldGenerator generator) {
            if (world.provider instanceof WorldProviderSpaceStation) { return true; }
            if (!(world.provider instanceof IGalacticraftWorldProvider) || ConfigManagerCore.enableOtherModsFeatures) { return false; }
            if (world.provider instanceof GcWorldProvider && own(generator)) { return false; }
            if (whitelist == null) { whitelist = gather(); }
            for (Class<?> held : whitelist) {
                if (held.isInstance(generator)) { return false; }
            }
            return true;
        }

        private static boolean own(IWorldGenerator generator) { return generator instanceof ContentWorldgen || generator instanceof ContentHardness.Swaps; }

        private static List<Class<?>> gather() {
            List<Class<?>> found = new ArrayList<>();
            if (ConfigManagerCore.whitelistCoFHCoreGen) { add(found, "cofh.cofhworld.init.WorldHandler"); }
            add(found, "bloodasp.galacticgreg.GT_Worldgenerator_Space");
            add(found, "com.rwtema.denseores.WorldGenOres");
            add(found, "appeng.worldgen.MeteoriteWorldGen");
            if (ConfigManagerCore.enableThaumCraftNodes) { add(found, "thaumcraft.common.lib.world.ThaumcraftWorldGenerator"); }
            return found;
        }

        private static void add(List<Class<?>> list, String name) {
            try { list.add(Class.forName(name)); }
            catch (ClassNotFoundException ignored) { }
        }
    }
}
