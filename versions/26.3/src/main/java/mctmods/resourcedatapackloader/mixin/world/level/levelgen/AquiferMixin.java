package mctmods.resourcedatapackloader.mixin.world.level.levelgen;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.CaveRegionDef;
import mctmods.resourcedatapackloader.common.worldgen.carver.ContentCaveRegions;
import mctmods.resourcedatapackloader.common.worldgen.surface.IRegionAquifer;

import javax.annotation.Nullable;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Aquifer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Aquifer.NoiseBasedAquifer.class) public abstract class AquiferMixin implements IRegionAquifer {
    @Shadow @Final private Aquifer.FluidPicker globalFluidPicker;
    @Unique @Nullable private BiomeSource rdpl$regions;
    @Unique private int rdpl$seaLevel;

    @Override public void rdpl$pinRegions(BiomeSource regions, int seaLevel) {
        rdpl$regions = regions;
        rdpl$seaLevel = seaLevel;
    }

    @Inject(method = "computeFluid", at = @At("HEAD"), cancellable = true) private void rdpl$regionWater(int x, int y, int z, CallbackInfoReturnable<Aquifer.FluidStatus> cir) {
        if (rdpl$regions == null) { return; }
        int pinned = ContentCaveRegions.waterLevel(rdpl$regions, x, y, z);
        if (pinned == CaveRegionDef.NO_WATER) { return; }
        Aquifer.FluidStatus deep = globalFluidPicker.computeFluid(x, DimensionType.WAY_BELOW_MIN_Y, z);
        int floor = deep.fluidType().is(Blocks.LAVA) ? deep.fluidLevel() + 2 : pinned;
        int level = Math.max(pinned, floor);
        cir.setReturnValue(new Aquifer.FluidStatus(Math.min(rdpl$seaLevel - 1, level), Blocks.WATER.defaultBlockState()));
    }
}
