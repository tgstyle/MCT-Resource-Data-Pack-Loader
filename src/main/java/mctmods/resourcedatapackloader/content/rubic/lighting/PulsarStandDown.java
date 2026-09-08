package mctmods.resourcedatapackloader.content.rubic.lighting;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.World;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class PulsarStandDown {
    private static final Set<Integer> TOLD = Collections.synchronizedSet(new HashSet<>());

    private PulsarStandDown() {}

    public static boolean holds(World world) {
        if (!(world instanceof IRubicWorld) || !((IRubicWorld) world).rdpl$isRubicWorld()) { return false; }
        int dimension = world.provider == null ? Integer.MIN_VALUE : world.provider.getDimension();
        if (TOLD.add(dimension)) {
            ContentLog.LOGGER.info("Pulsar is left out of the rubic dimension {}, so rubic's own light engine keeps it", dimension);
        }
        return true;
    }
}
