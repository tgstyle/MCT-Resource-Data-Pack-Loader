package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.util.Coords;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenDesertWells;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Random;

@Mixin(WorldGenDesertWells.class) public class MixinWorldGenDesertWells {
    @Unique private int rdpl$minY;

    @Inject(method = "generate", at = @At("HEAD")) private void onGenerate(World worldIn, Random rand, BlockPos position, CallbackInfoReturnable<Boolean> cbi) {
        this.rdpl$minY = Coords.getMinCubePopulationPos(position.getY());
    }

    @ModifyConstant(method = "generate", constant = @Constant(intValue = 2, ordinal = 0)) private int getMinGenHeight(int orig, World worldIn, Random rand, BlockPos position) {
        if (((IRubicWorld) worldIn).rdpl$isRubicWorld()) { return rdpl$minY; }
        return orig;
    }
}
