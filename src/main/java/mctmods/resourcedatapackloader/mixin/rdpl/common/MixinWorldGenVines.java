package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenVines;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.Random;

@Mixin(WorldGenVines.class) public class MixinWorldGenVines {
    @ModifyConstant(method = "generate", constant = @Constant(intValue = 128), require = 1) private int rdpl$maxVineY(int orig, World worldIn, Random rand, BlockPos position) { return ((IRubicWorld) worldIn).rdpl$getMaxHeight(); }

    @Redirect(method = "generate", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/Block;canPlaceBlockOnSide(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z"), require = 1)
    private boolean rdpl$vineFacesItsWall(Block vine, World worldIn, BlockPos pos, EnumFacing side) { return vine.canPlaceBlockOnSide(worldIn, pos, side.getOpposite()); }
}
