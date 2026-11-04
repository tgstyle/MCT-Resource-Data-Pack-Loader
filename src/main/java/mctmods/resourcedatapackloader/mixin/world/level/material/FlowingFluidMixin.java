package mctmods.resourcedatapackloader.mixin.world.level.material;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimGuard;
import mctmods.resourcedatapackloader.common.types.ContentTypes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowingFluid.class) public abstract class FlowingFluidMixin {
    @WrapOperation(method = "spread(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/FluidState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FlowingFluid;spreadTo(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/material/FluidState;)V")) private void rdpl$guardDrop(FlowingFluid fluid, LevelAccessor access, BlockPos target, BlockState targetState, Direction direction, FluidState flowing, Operation<Void> original, Level level, BlockPos pos, FluidState state) { if (ClaimGuard.fluidMayFlow(level, pos, target)) { original.call(fluid, access, target, targetState, direction, flowing); } }

    @WrapOperation(method = "spreadToSides(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/world/level/block/state/BlockState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FlowingFluid;spreadTo(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/material/FluidState;)V")) private void rdpl$guardSide(FlowingFluid fluid, LevelAccessor access, BlockPos target, BlockState targetState, Direction direction, FluidState flowing, Operation<Void> original, Level level, BlockPos pos, FluidState fluidState, BlockState blockState) { if (ClaimGuard.fluidMayFlow(level, pos, target)) { original.call(fluid, access, target, targetState, direction, flowing); } }

    @Inject(method = "canPassThroughWall", at = @At("HEAD"), cancellable = true) private void rdpl$washesThrough(Direction direction, BlockGetter level, BlockPos pos, BlockState state, BlockPos spreadPos, BlockState spreadState, CallbackInfoReturnable<Boolean> cir) {
        boolean here = ContentTypes.washedAway(state);
        boolean there = ContentTypes.washedAway(spreadState);
        if (!here && !there) { return; }
        VoxelShape from = here ? Shapes.empty() : state.getCollisionShape(level, pos);
        VoxelShape to = there ? Shapes.empty() : spreadState.getCollisionShape(level, spreadPos);
        cir.setReturnValue(!Shapes.mergedFaceOccludes(from, to, direction));
    }
}
