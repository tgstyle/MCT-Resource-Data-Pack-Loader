package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ai.PathNodeMemo;
import mctmods.resourcedatapackloader.util.IPathKindMemo;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import javax.annotation.Nullable;

@Mixin(NodeEvaluator.class) public abstract class MixinNodeEvaluator implements IPathKindMemo {
    @Shadow protected int entityWidth;
    @Shadow protected int entityHeight;
    @Shadow protected int entityDepth;
    @Unique private int rdpl$owner;
    @Unique private int rdpl$world;
    @Unique private long rdpl$tick;
    @Unique private boolean rdpl$remembering;

    @Inject(method = "prepare", at = @At("TAIL")) private void rdpl$whenAndWhere(PathNavigationRegion level, Mob mob, CallbackInfo ci) {
        rdpl$owner = System.identityHashCode(getClass());
        if ((Object) this instanceof SwimNodeEvaluator) { rdpl$owner = rdpl$owner * 31 + entityWidth * 961 + entityHeight * 31 + entityDepth; }
        rdpl$world = mob.level().dimension().location().hashCode();
        rdpl$tick = mob.level().getGameTime();
        rdpl$remembering = true;
    }

    @Inject(method = "done", at = @At("TAIL")) private void rdpl$searchOver(CallbackInfo ci) { rdpl$remembering = false; }

    @Override @Nullable public BlockPathTypes rdpl$known(int x, int y, int z) { return rdpl$remembering ? PathNodeMemo.held().known(rdpl$owner, rdpl$world, rdpl$tick, x, y, z) : null; }

    @Override public void rdpl$remember(int x, int y, int z, BlockPathTypes kind) { if (rdpl$remembering) { PathNodeMemo.held().remember(rdpl$owner, rdpl$world, rdpl$tick, x, y, z, kind); } }
}
