package mctmods.resourcedatapackloader.content.rubic.world.interfaces;

import mctmods.resourcedatapackloader.content.rubic.worldgen.interfaces.ICubeGenerator;

import net.minecraft.world.World;
import javax.annotation.Nullable;

public interface IRubicWorldProvider {
    @Nullable ICubeGenerator rdpl$createCubeGenerator();

    int rdpl$getOriginalActualHeight();

    World rdpl$getWorld();
}
