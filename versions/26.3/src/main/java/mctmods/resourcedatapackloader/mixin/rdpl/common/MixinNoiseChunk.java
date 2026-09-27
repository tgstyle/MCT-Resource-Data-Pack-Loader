package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.interfaces.IMarkedNoise;
import mctmods.resourcedatapackloader.content.worldgen.CityDeckBeard;

import net.minecraft.world.level.levelgen.NoiseChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import javax.annotation.Nullable;

@Mixin(NoiseChunk.class) public abstract class MixinNoiseChunk implements IMarkedNoise {
    @Unique private boolean rdpl$voided;
    @Unique @Nullable private CityDeckBeard rdpl$city;

    @Override public void rdpl$mark(boolean voided, @Nullable CityDeckBeard city) {
        rdpl$voided = voided;
        rdpl$city = city;
    }

    @Override public boolean rdpl$voided() { return rdpl$voided; }

    @Override @Nullable public CityDeckBeard rdpl$city() { return rdpl$city; }
}
