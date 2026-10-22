package mctmods.resourcedatapackloader.util.compat.interfaces;

import mctmods.resourcedatapackloader.content.def.GalaxySpaceDef;

import asmodeuscore.api.dimension.IAdvancedSpace;
import asmodeuscore.api.dimension.IProviderFreeze;
import javax.annotation.Nullable;

public interface IGsSpace extends IAdvancedSpace, IProviderFreeze {
    @Nullable GalaxySpaceDef galaxySpace();

    @Override default double getSolarWindMultiplier() {
        GalaxySpaceDef gs = galaxySpace();
        if (gs != null && gs.solarWind >= 0.0D) { return gs.solarWind; }
        return getSolarSize() * getSolarSize();
    }

    @Override default boolean isFreeze() {
        GalaxySpaceDef gs = galaxySpace();
        return gs == null || gs.freezeBlocks;
    }
}
