package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.entity.ContentEntityTicks;
import mctmods.resourcedatapackloader.content.entity.ai.PathNodeMemo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.SwimNodeProcessor;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SwimNodeProcessor.class)
public abstract class MixinSwimNodeProcessor {
    @Unique private PathNodeMemo rdpl$memo;
    @Unique private boolean rdpl$located;
    @Unique private int rdpl$world;
    @Unique private int rdpl$owner;
    @Unique private BlockPos.MutableBlockPos rdpl$looking;

    @Inject(method = "getStart", at = @At("HEAD"))
    private void rdpl$readyToRemember(CallbackInfoReturnable<PathPoint> cir) {
        AccessorNodeProcessor inside = (AccessorNodeProcessor) this;
        EntityLiving mob = inside.rdpl$getEntity();
        rdpl$located = mob != null;
        if (!rdpl$located) { return; }

        rdpl$world = mob.world.provider.getDimension();
        rdpl$owner = System.identityHashCode(getClass()) * 31 + inside.rdpl$getSizeX() * 961 + inside.rdpl$getSizeY() * 31 + inside.rdpl$getSizeZ();
        rdpl$memo = PathNodeMemo.held();
    }

    @WrapOperation(method = "getWaterNode", at = @At(value = "INVOKE", target = "Lnet/minecraft/pathfinding/SwimNodeProcessor;isFree(III)Lnet/minecraft/pathfinding/PathNodeType;"))
    private PathNodeType rdpl$rememberWater(SwimNodeProcessor self, int p_186327_1_, int p_186327_2_, int p_186327_3_, Operation<PathNodeType> original) {
        if (!rdpl$located) { return original.call(self, p_186327_1_, p_186327_2_, p_186327_3_); }

        int tick = ContentEntityTicks.ticks();
        PathNodeType held = rdpl$memo.known(rdpl$owner, rdpl$world, tick, p_186327_1_, p_186327_2_, p_186327_3_);
        if (held != null) { return held; }

        PathNodeType found = original.call(self, p_186327_1_, p_186327_2_, p_186327_3_);
        rdpl$memo.remember(rdpl$owner, rdpl$world, tick, p_186327_1_, p_186327_2_, p_186327_3_, found);
        return found;
    }

    @Redirect(method = "isFree", at = @At(value = "NEW", target = "()Lnet/minecraft/util/math/BlockPos$MutableBlockPos;"))
    private BlockPos.MutableBlockPos rdpl$whereItLooks() {
        if (rdpl$looking == null) { rdpl$looking = new BlockPos.MutableBlockPos(); }
        return rdpl$looking;
    }
}
