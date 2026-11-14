package mctmods.resourcedatapackloader.core.integration.adastra;

import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;

import earth.terrarium.adastra.api.events.AdAstraEvents;
import net.minecraft.world.level.block.state.BlockState;

final class AdAstraOxygen {
    private AdAstraOxygen() {}

    static void register() {
        AdAstraEvents.EnvironmentTickEvent.register((level, pos, state, temperature) -> !exempt(state));
    }

    private static boolean exempt(BlockState state) {
        ContentRegistry.BlockEntry entry = ContentRegistry.entry(state.getBlock());
        return entry != null && !entry.def().needsOxygen();
    }
}
