package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ai.PathNodeMemo;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SwimNodeEvaluator.class) public abstract class MixinSwimNodeEvaluator extends NodeEvaluator {
    @Unique private int rdpl$owner;
    @Unique private int rdpl$world;
    @Unique private long rdpl$tick;
    @Unique private boolean rdpl$remembering;

    @Inject(method = "prepare", at = @At("TAIL")) private void rdpl$whenAndWhere(PathNavigationRegion level, Mob mob, CallbackInfo ci) {
        rdpl$owner = System.identityHashCode(getClass()) * 31 + entityWidth * 961 + entityHeight * 31 + entityDepth;
        rdpl$world = mob.level().dimension().location().hashCode();
        rdpl$tick = mob.level().getGameTime();
        rdpl$remembering = true;
    }

    @Inject(method = "done", at = @At("TAIL")) private void rdpl$searchOver(CallbackInfo ci) { rdpl$remembering = false; }

    @Inject(method = "getCachedBlockType", at = @At("HEAD"), cancellable = true) private void rdpl$sharedKind(int x, int y, int z, CallbackInfoReturnable<BlockPathTypes> cir) {
        if (!rdpl$remembering) { return; }
        BlockPathTypes held = PathNodeMemo.held().known(rdpl$owner, rdpl$world, rdpl$tick, x, y, z);
        if (held != null) { cir.setReturnValue(held); }
    }

    @Inject(method = "getCachedBlockType", at = @At("RETURN")) private void rdpl$rememberKind(int x, int y, int z, CallbackInfoReturnable<BlockPathTypes> cir) {
        if (rdpl$remembering) { PathNodeMemo.held().remember(rdpl$owner, rdpl$world, rdpl$tick, x, y, z, cir.getReturnValue()); }
    }
}
