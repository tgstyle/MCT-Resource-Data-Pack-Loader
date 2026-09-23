package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.village.RailPiece;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRails;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Mixin(WorldGenMinable.class) public abstract class MixinWorldGenMinable {
    @Shadow @Final private IBlockState oreBlock;
    @Shadow @Final private int numberOfBlocks;
    @Unique private List<RailPiece> rdpl$lines = Collections.emptyList();
    @Unique private int rdpl$spared;

    @Inject(method = "generate", at = @At("HEAD")) private void rdpl$findRailways(World worldIn, Random rand, BlockPos position, CallbackInfoReturnable<Boolean> cir) {
        rdpl$spared = 0;
        if (!(oreBlock.getBlock() instanceof BlockFalling)) {
            rdpl$lines = Collections.emptyList();
            return;
        }
        int reach = numberOfBlocks / 8 + numberOfBlocks / 16 + 2;
        int x = position.getX() + 8;
        int z = position.getZ() + 8;
        rdpl$lines = BeardRails.railways(worldIn, new StructureBoundingBox(x - reach, 0, z - reach, x + reach, 0, z + reach));
    }

    @WrapOperation(method = "generate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z")) private boolean rdpl$notUnderARailway(World world, BlockPos pos, IBlockState newState, int flags, Operation<Boolean> original) {
        if (!rdpl$lines.isEmpty() && BeardRails.underBed(world, rdpl$lines, pos.getX(), pos.getY(), pos.getZ())) {
            rdpl$spared++;
            return false;
        }
        return original.call(world, pos, newState, flags);
    }

    @Inject(method = "generate", at = @At("RETURN")) private void rdpl$sayWhatWasSpared(World worldIn, Random rand, BlockPos position, CallbackInfoReturnable<Boolean> cir) {
        if (rdpl$spared > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("A {} vein at {}, {}, {} was kept out of the ground under a railway bed {} time(s), where it would have fallen away", oreBlock.getBlock().getRegistryName(), position.getX() + 8, position.getY(), position.getZ() + 8, rdpl$spared); }
    }
}
