package mctmods.resourcedatapackloader.common.rubic.world.interfaces;

import mctmods.resourcedatapackloader.common.rubic.worldgen.interfaces.ICubeGenerator;

import javax.annotation.Nullable;
import net.minecraft.world.World;

public interface IRubicWorldProvider {
    @Nullable ICubeGenerator rdpl$createCubeGenerator();

    int rdpl$getOriginalActualHeight();

    World rdpl$getWorld();
}
