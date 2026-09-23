package mctmods.resourcedatapackloader.mixin.vanillatweaks;

import mctmods.resourcedatapackloader.content.block.ContentChests;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockChest.class) public abstract class MixinBlockChest {
    @WrapOperation(method = "getBoundingBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/IBlockAccess;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;")) private IBlockState rdpl$boxBeside(IBlockAccess access, BlockPos pos, Operation<IBlockState> original, @Local(argsOnly = true, ordinal = 0) BlockPos origin) { return ContentChests.seen(access, origin, pos, original.call(access, pos)); }

    @WrapOperation(method = {"onBlockAdded", "checkForSurroundingChests", "correctFacing", "canPlaceBlockAt", "isDoubleChest", "getContainer"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;")) private IBlockState rdpl$chestBeside(World world, BlockPos pos, Operation<IBlockState> original, @Local(argsOnly = true, ordinal = 0) BlockPos origin) { return ContentChests.seen(world, origin, pos, original.call(world, pos)); }

    @Inject(method = "onBlockPlacedBy", at = @At("HEAD"), cancellable = true) private void rdpl$placeModern(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack, CallbackInfo ci) {
        if (ContentChests.placing(pos)) {
            ContentChests.placed(worldIn, pos, state, stack);
            ci.cancel();
        }
    }

    @Inject(method = "checkForSurroundingChests", at = @At("HEAD"), cancellable = true) private void rdpl$keepFacing(World worldIn, BlockPos pos, IBlockState state, CallbackInfoReturnable<IBlockState> cir) {
        if (ContentChests.keyed(worldIn, pos)) { cir.setReturnValue(state); }
    }
}
