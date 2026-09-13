package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ai.PathNodeMemo;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WalkNodeEvaluator.class) public abstract class MixinWalkNodeEvaluator {
    @Unique private int rdpl$world;
    @Unique private long rdpl$tick;
    @Unique private boolean rdpl$remembering;

    @Inject(method = "prepare", at = @At("TAIL"))
    private void rdpl$whenAndWhere(PathNavigationRegion region, Mob mob, CallbackInfo ci) {
        rdpl$world = mob.level().dimension().location().hashCode();
        rdpl$tick = mob.level().getGameTime();
        rdpl$remembering = true;
    }

    @Inject(method = "done", at = @At("TAIL"))
    private void rdpl$searchOver(CallbackInfo ci) { rdpl$remembering = false; }

    @Redirect(method = "getPathTypeWithinMobBB", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/pathfinder/WalkNodeEvaluator;getPathType(Lnet/minecraft/world/level/pathfinder/PathfindingContext;III)Lnet/minecraft/world/level/pathfinder/PathType;"))
    private PathType rdpl$sharedKind(WalkNodeEvaluator self, PathfindingContext context, int x, int y, int z) {
        if (!rdpl$remembering) { return self.getPathType(context, x, y, z); }
        PathNodeMemo memo = PathNodeMemo.held();
        PathType held = memo.known(rdpl$world, rdpl$tick, x, y, z);
        if (held != null) { return held; }
        PathType found = self.getPathType(context, x, y, z);
        memo.remember(rdpl$world, rdpl$tick, x, y, z, found);
        return found;
    }
}
