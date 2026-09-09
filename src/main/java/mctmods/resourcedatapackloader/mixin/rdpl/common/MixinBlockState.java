package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.FaceCache;
import mctmods.resourcedatapackloader.util.FaceHiding;

import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockState.class) public abstract class MixinBlockState implements FaceHiding {
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

