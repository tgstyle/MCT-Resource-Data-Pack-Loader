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
    @Inject(method = "canPassThroughWall", at = @At("HEAD"), cancellable = true) private void rdpl$washesThrough(Direction direction, BlockGetter level, BlockPos pos, BlockState state, BlockPos spreadPos, BlockState spreadState, CallbackInfoReturnable<Boolean> cir) {
        boolean here = ContentTypes.washedAway(state);
        boolean there = ContentTypes.washedAway(spreadState);
        if (!here && !there) { return; }
        VoxelShape from = here ? Shapes.empty() : state.getCollisionShape(level, pos);
        VoxelShape to = there ? Shapes.empty() : spreadState.getCollisionShape(level, spreadPos);
        cir.setReturnValue(!Shapes.mergedFaceOccludes(from, to, direction));
    }
}
