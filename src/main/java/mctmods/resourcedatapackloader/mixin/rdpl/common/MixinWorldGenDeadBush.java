package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.util.Coords;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenDeadBush;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Random;

@Mixin(WorldGenDeadBush.class) public class MixinWorldGenDeadBush {
    @Unique private int rdpl$minPos;

    @Inject(method = "generate", at = @At("HEAD")) private void onGenerate(World worldIn, Random rand, BlockPos position, CallbackInfoReturnable<Boolean> cbi) {
        this.rdpl$minPos = Coords.getMinCubePopulationPos(position.getY());
    }

    @ModifyConstant(method = "generate", constant = @Constant(intValue = 0, expandZeroConditions = Constant.Condition.GREATER_THAN_ZERO, ordinal = 0)) private int getReplaceMaterial_HeightCheckHack(int orig, World worldIn, Random rand, BlockPos position) {
        if (((IRubicWorld) worldIn).rdpl$isRubicWorld()) { return orig; }
        return rdpl$minPos;
    }
}
