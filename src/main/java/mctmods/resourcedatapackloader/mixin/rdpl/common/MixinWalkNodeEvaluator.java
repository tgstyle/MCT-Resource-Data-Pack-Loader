package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.IPathKindMemo;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WalkNodeEvaluator.class) public abstract class MixinWalkNodeEvaluator {
    @Redirect(method = "getBlockPathTypes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/pathfinder/WalkNodeEvaluator;getBlockPathType(Lnet/minecraft/world/level/BlockGetter;III)Lnet/minecraft/world/level/pathfinder/BlockPathTypes;")) private BlockPathTypes rdpl$sharedKind(WalkNodeEvaluator self, BlockGetter level, int x, int y, int z) {
        IPathKindMemo memo = (IPathKindMemo) (Object) this;
        BlockPathTypes held = memo.rdpl$known(x, y, z);
        if (held != null) { return held; }
        BlockPathTypes found = self.getBlockPathType(level, x, y, z);
        memo.rdpl$remember(x, y, z, found);
        return found;
    }
}
