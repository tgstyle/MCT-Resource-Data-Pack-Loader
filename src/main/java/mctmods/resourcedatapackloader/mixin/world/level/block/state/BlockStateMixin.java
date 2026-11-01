package mctmods.resourcedatapackloader.mixin.world.level.block.state;

import mctmods.resourcedatapackloader.common.util.world.FaceCache;
import mctmods.resourcedatapackloader.common.util.world.IFaceHiding;

import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockState.class) public abstract class BlockStateMixin implements IFaceHiding {
    @Unique private int rdpl$faceEpoch;
    @Unique private boolean rdpl$faceHidden;

    @Override public boolean rdpl$faceHiding() {
        int epoch = FaceCache.epoch();
        if (rdpl$faceEpoch != epoch) {
            BlockState self = (BlockState) (Object) this;
            rdpl$faceHidden = self.getBlock().supportsExternalFaceHiding(self);
            rdpl$faceEpoch = epoch;
        }
        return rdpl$faceHidden;
    }
}
