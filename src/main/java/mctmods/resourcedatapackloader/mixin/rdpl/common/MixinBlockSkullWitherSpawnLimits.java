package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.block.BlockSkull;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BlockSkull.class) public class MixinBlockSkullWitherSpawnLimits {
    @ModifyConstant(method = "checkWitherSpawn", constant = @Constant(intValue = 2, ordinal = 0)) private int checkWitherSpawnHeightLimit(int originalHeight, World worldIn, BlockPos pos, TileEntitySkull te) {
        return ((IRubicWorld) worldIn).rdpl$getMinHeight() + originalHeight;
    }
}
