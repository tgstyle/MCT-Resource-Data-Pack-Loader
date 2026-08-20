package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.util.Coords;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenSwamp;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Random;
import javax.annotation.Nullable;

@Mixin(WorldGenSwamp.class) public class MixinWorldGenSwamp {
    @Unique private int rdpl$minPos;

    @Inject(method = "generate", at = @At("HEAD")) private void onGenerate(World worldIn, Random rand, BlockPos position, CallbackInfoReturnable<Boolean> cbi) {
        this.rdpl$minPos = Coords.getMinCubePopulationPos(position.getY());
    }

    @Redirect(method = "generate",
            at = @At(value = "FIELD", target = "Lnet/minecraft/block/material/Material;WATER:Lnet/minecraft/block/material/Material;", ordinal = 0, opcode = Opcodes.GETSTATIC))
    @Nullable private Material getReplaceMaterial_HeightCheckHack(World worldIn, Random rand, BlockPos position) {
        if (((IRubicWorld) worldIn).rdpl$isRubicWorld() && position.getY() < rdpl$minPos) { return null; }
        return Material.WATER;
    }

    @ModifyConstant(method = "generate", constant = @Constant(intValue = 1, ordinal = 1)) private int getMinGenHeight(int orig, World worldIn, Random rand, BlockPos position) {
        return ((IRubicWorld) worldIn).rdpl$getMinHeight() + 1;
    }

    @ModifyConstant(method = "generate",
            constant = @Constant(intValue = 0, ordinal = 1,
                    expandZeroConditions = Constant.Condition.GREATER_THAN_OR_EQUAL_TO_ZERO))
    private int getMinGenHeightCompareZero(int orig, World worldIn, Random rand, BlockPos position) { return ((IRubicWorld) worldIn).rdpl$getMinHeight(); }

    @ModifyConstant(method = "generate", constant = @Constant(intValue = 256)) private int getMaxGenHeight(int orig, World worldIn, Random rand, BlockPos position) {
        return ((IRubicWorld) worldIn).rdpl$getMaxHeight();
    }
}
