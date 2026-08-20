package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumnInternal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.chunk.Chunk;

@Mixin(Chunk.class) public abstract class MixinChunk implements IColumn {
    @Inject(method = "generateHeightMap", at = @At(value = "HEAD"), cancellable = true) private void generateHeightMap_Rubic_Cancel(CallbackInfo cbi) {
        if (((IColumnInternal) this).isRubicColumn()) { cbi.cancel(); }
    }

    @Inject(method = "read", at = @At(value = "HEAD")) private void fillChunk_Rubic_NotSupported(PacketBuffer buf, int availableSections, boolean groundUpContinuous, CallbackInfo cbi) {
        if (((IColumnInternal) this).isRubicColumn()) { throw new UnsupportedOperationException("setting storage arrays it not supported with rubic"); }
    }
}
