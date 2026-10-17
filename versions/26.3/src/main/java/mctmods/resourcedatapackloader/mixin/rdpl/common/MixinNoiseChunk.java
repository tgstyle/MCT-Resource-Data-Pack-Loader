package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.interfaces.IMarkedNoise;
import mctmods.resourcedatapackloader.content.worldgen.CityDeckBeard;

import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.NoiseChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import javax.annotation.Nullable;

@Mixin(NoiseChunk.class) public abstract class MixinNoiseChunk implements IMarkedNoise {
    @Unique private boolean rdpl$voided;
    @Unique @Nullable private CityDeckBeard rdpl$city;
    @Unique @Nullable private WorldGenLevel rdpl$level;

    @Override public void rdpl$mark(boolean voided, @Nullable CityDeckBeard city, @Nullable WorldGenLevel level) {
        rdpl$voided = voided;
        rdpl$city = city;
        rdpl$level = level;
    }

    @Override public boolean rdpl$voided() { return rdpl$voided; }

    @Override @Nullable public CityDeckBeard rdpl$city() { return rdpl$city; }

    @Override @Nullable public WorldGenLevel rdpl$level() { return rdpl$level; }
}
