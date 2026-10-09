package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.IPathKindMemo;

import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WalkNodeEvaluator.class) public abstract class MixinWalkNodeEvaluator {
    @Redirect(method = "getPathTypeWithinMobBB", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/pathfinder/WalkNodeEvaluator;getPathType(Lnet/minecraft/world/level/pathfinder/PathfindingContext;III)Lnet/minecraft/world/level/pathfinder/PathType;")) private PathType rdpl$sharedKind(WalkNodeEvaluator self, PathfindingContext context, int x, int y, int z) {
        IPathKindMemo memo = (IPathKindMemo) (Object) this;
        PathType held = memo.rdpl$known(x, y, z);
        if (held != null) { return held; }
        PathType found = self.getPathType(context, x, y, z);
        memo.rdpl$remember(x, y, z, found);
        return found;
    }
}
