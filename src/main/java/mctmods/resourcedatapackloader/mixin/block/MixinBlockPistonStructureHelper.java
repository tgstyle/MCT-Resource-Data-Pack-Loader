package mctmods.resourcedatapackloader.mixin.block;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimGuard;

import java.util.List;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockPistonStructureHelper.class) public class MixinBlockPistonStructureHelper {
    @Shadow @Final private World world;
    @Shadow @Final private BlockPos pistonPos;
    @Shadow @Final private BlockPos blockToMove;
    @Shadow @Final private EnumFacing moveDirection;
    @Shadow @Final private List<BlockPos> toMove;
    @Shadow @Final private List<BlockPos> toDestroy;

    @Inject(method = "canMove()Z", at = @At("RETURN"), cancellable = true) private void rdpl$guardClaims(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && !ClaimGuard.pistonMayMove(world, pistonPos, blockToMove, moveDirection, toMove, toDestroy)) { cir.setReturnValue(false); }
    }
}
