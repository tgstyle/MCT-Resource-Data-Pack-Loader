package mctmods.resourcedatapackloader.mixin.client.world.chunk;

import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IColumnInternal;

import net.minecraft.network.PacketBuffer;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chunk.class) public abstract class MixinChunk implements IColumn {
    @Inject(method = "generateHeightMap", at = @At(value = "HEAD"), cancellable = true) private void rdpl$generateHeightMap_Rubic_Cancel(CallbackInfo cbi) {
        if (((IColumnInternal) this).isRubicColumn()) { cbi.cancel(); }
    }

    @Inject(method = "read", at = @At(value = "HEAD")) private void rdpl$fillChunk_Rubic_NotSupported(PacketBuffer buf, int availableSections, boolean groundUpContinuous, CallbackInfo cbi) {
        if (((IColumnInternal) this).isRubicColumn()) { throw new UnsupportedOperationException("setting storage arrays is not supported with rubic"); }
    }
}
