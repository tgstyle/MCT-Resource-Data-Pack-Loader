package mctmods.resourcedatapackloader.mixin.world.level.material;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimGuard;
import mctmods.resourcedatapackloader.common.types.ContentTypes;

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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowingFluid.class) public abstract class FlowingFluidMixin {
    @Shadow protected abstract void spreadTo(LevelAccessor level, BlockPos pos, BlockState state, Direction direction, FluidState target);

    @Redirect(method = {"spread(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V", "spreadToSides(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/world/level/block/state/BlockState;)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FlowingFluid;spreadTo(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/material/FluidState;)V")) private void rdpl$guardFlow(FlowingFluid self, LevelAccessor level, BlockPos target, BlockState targetState, Direction direction, FluidState newFluid) {
        if (ClaimGuard.fluidMayFlow((Level) level, target.relative(direction.getOpposite()), target)) { spreadTo(level, target, targetState, direction, newFluid); }
    }

    @Inject(method = "canPassThroughWall", at = @At("HEAD"), cancellable = true) private static void rdpl$washesThrough(Direction direction, BlockGetter level, BlockPos sourcePos, BlockState sourceState, BlockPos targetPos, BlockState targetState, CallbackInfoReturnable<Boolean> cir) {
        boolean here = ContentTypes.washedAway(sourceState);
        boolean there = ContentTypes.washedAway(targetState);
        if (!here && !there) { return; }
        VoxelShape from = here ? Shapes.empty() : sourceState.getCollisionShape(level, sourcePos);
        VoxelShape to = there ? Shapes.empty() : targetState.getCollisionShape(level, targetPos);
        cir.setReturnValue(!Shapes.mergedFaceOccludes(from, to, direction));
    }
}
