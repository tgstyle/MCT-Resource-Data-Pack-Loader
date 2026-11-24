package mctmods.resourcedatapackloader.mixin.tileentity;

import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;
import mctmods.resourcedatapackloader.core.integration.CompatHandler;

import java.util.List;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityLockable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityBeacon.class) public abstract class MixinTileEntityBeacon extends TileEntityLockable {
    @Shadow private boolean isComplete;
    @Shadow @Final private List<TileEntityBeacon.BeamSegment> beamSegments;
    @Unique private int rdpl$scanTop = CompatHandler.VANILLA_HEIGHT;

    @SuppressWarnings("DisallowedTargetInsn") @ModifyConstant(method = "updateSegmentColors", constant = @Constant(expandZeroConditions = Constant.Condition.LESS_THAN_ZERO)) private int rdpl$updateSegmentColorsYValue(int orig) { return GenHeights.floor(world, orig); }

    @Inject(method = "updateSegmentColors", at = @At("HEAD")) private void rdpl$figureScanTop(CallbackInfo ci) {
        if (!GenHeights.rubic(world)) {
            rdpl$scanTop = CompatHandler.VANILLA_HEIGHT;
            return;
        }
        rdpl$scanTop = Math.min(GenHeights.ceiling(world, 256), Math.max(rdpl$topStorageBlockY() + 1, pos.getY() + 1));
    }

    @ModifyConstant(method = "updateSegmentColors", constant = @Constant(intValue = 256)) private int rdpl$beamScanTop(int orig) { return rdpl$scanTop; }

    @Inject(method = "updateSegmentColors", at = @At("TAIL")) private void rdpl$extendBeam(CallbackInfo ci) {
        if (!GenHeights.rubic(world) || !isComplete || beamSegments.isEmpty()) { return; }
        int extend = GenHeights.ceiling(world, 256) - rdpl$scanTop;
        if (extend <= 0) { return; }
        TileEntityBeacon.BeamSegment last = beamSegments.get(beamSegments.size() - 1);
        last.height += extend;
    }

    @Unique private int rdpl$topStorageBlockY() {
        int top = GenHeights.topFilledCubeY((IColumn) world.getChunk(pos));
        return top == Integer.MIN_VALUE ? Integer.MIN_VALUE : (top << 4) + 15;
    }
}
