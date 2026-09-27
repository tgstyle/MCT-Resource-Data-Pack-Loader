package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.types.ContentTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowingFluid.class) public abstract class MixinFlowingFluid {
    @Inject(method = "canPassThroughWall", at = @At("HEAD"), cancellable = true) private static void rdpl$washesThrough(Direction direction, BlockGetter level, BlockPos sourcePos, BlockState sourceState, BlockPos targetPos, BlockState targetState, CallbackInfoReturnable<Boolean> cir) {
        boolean here = ContentTypes.washedAway(sourceState);
        boolean there = ContentTypes.washedAway(targetState);
        if (!here && !there) { return; }
        VoxelShape from = here ? Shapes.empty() : sourceState.getCollisionShape(level, sourcePos);
        VoxelShape to = there ? Shapes.empty() : targetState.getCollisionShape(level, targetPos);
        cir.setReturnValue(!Shapes.mergedFaceOccludes(from, to, direction));
    }
}
