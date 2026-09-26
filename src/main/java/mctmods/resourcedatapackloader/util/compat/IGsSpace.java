package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.content.def.GalaxySpaceDef;

import asmodeuscore.api.dimension.IAdvancedSpace;
import asmodeuscore.api.dimension.IProviderFreeze;
import javax.annotation.Nullable;

interface IGsSpace extends IAdvancedSpace, IProviderFreeze {
    @Nullable GalaxySpaceDef galaxySpace();

    @Override default double getSolarWindMultiplier() { return GsBodies.solarWind(galaxySpace(), getSolarSize()); }

    @Override default boolean isFreeze() {
        GalaxySpaceDef gs = galaxySpace();
        return gs == null || gs.freezeBlocks;
    }
}
