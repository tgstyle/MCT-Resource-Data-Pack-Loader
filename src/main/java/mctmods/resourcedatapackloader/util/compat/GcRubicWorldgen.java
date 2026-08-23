package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.util.ContentLog;

import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.Loader;
import java.util.ArrayList;
import java.util.List;

public final class GcRubicWorldgen {
    private static Handler handler;

    private GcRubicWorldgen() {}

    public static void register() {
        if (!Loader.isModLoaded("galacticraftcore")) { return; }
        handler = new Handler();
        ContentLog.LOGGER.info("Galacticraft keeps other mods' worldgen off its planets unless whitelisted, and rubic runs those generators itself, so on rubic worlds the same gate is asked before each one runs");
    }

    public static boolean allows(World world, IWorldGenerator generator) { return handler == null || handler.allowed(world, generator); }

    static final class Handler {
        private List<Class<?>> whitelist;

        boolean allowed(World world, IWorldGenerator generator) {
            if (world.provider instanceof WorldProviderSpaceStation) { return false; }
            if (!(world.provider instanceof IGalacticraftWorldProvider) || ConfigManagerCore.enableOtherModsFeatures) { return true; }
            if (whitelist == null) { whitelist = gather(); }
            for (Class<?> held : whitelist) {
                if (held.isInstance(generator)) { return true; }
            }
            return false;
        }

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
