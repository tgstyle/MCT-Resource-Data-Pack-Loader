package mctmods.resourcedatapackloader.common.rubic.lighting;

import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import net.minecraft.world.World;

public final class PulsarStandDown {
    private PulsarStandDown() {}

    public static boolean holds(World world) {
        if (!GenHeights.rubic(world)) { return false; }
        int dimension = world.provider == null ? Integer.MIN_VALUE : world.provider.getDimension();
        ContentLog.LOGGER.once("Pulsar is left out of the rubic dimension {}, so rubic's own light engine keeps it", dimension);
        return true;
    }
}
