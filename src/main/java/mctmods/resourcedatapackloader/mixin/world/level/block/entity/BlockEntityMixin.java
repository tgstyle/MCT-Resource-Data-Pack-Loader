package mctmods.resourcedatapackloader.mixin.world.level.block.entity;

import mctmods.resourcedatapackloader.common.enchantments.TableLapis;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class) public abstract class BlockEntityMixin {
    @Inject(method = "preRemoveSideEffects", at = @At("HEAD")) private void rdpl$tableLapis(BlockPos pos, BlockState state, CallbackInfo ci) { TableLapis.broken((BlockEntity) (Object) this, pos); }
}
