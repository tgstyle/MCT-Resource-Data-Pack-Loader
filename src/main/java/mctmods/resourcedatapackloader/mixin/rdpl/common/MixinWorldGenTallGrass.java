package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.Coords;
import mctmods.resourcedatapackloader.util.world.GenHeights;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Random;

@Mixin(WorldGenTallGrass.class) public class MixinWorldGenTallGrass {
    @Unique private int rdpl$minY;

    @Inject(method = "generate", at = @At("HEAD")) private void rdpl$onGenerate(World worldIn, Random rand, BlockPos position, CallbackInfoReturnable<Boolean> cbi) {
        this.rdpl$minY = Coords.getMinCubePopulationPos(position.getY());
    }

    @ModifyConstant(method = "generate", constant = @Constant(intValue = 0, ordinal = 0, expandZeroConditions = Constant.Condition.GREATER_THAN_ZERO)) private int rdpl$getMinGenHeight(int orig, World worldIn, Random rand, BlockPos position) {
        if (GenHeights.rubic(worldIn)) { return rdpl$minY; }
        return orig;
    }
}
