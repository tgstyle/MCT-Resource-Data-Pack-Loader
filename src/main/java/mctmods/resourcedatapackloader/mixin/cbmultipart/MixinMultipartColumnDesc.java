package mctmods.resourcedatapackloader.mixin.cbmultipart;

import mctmods.resourcedatapackloader.util.compat.CbmpRubicParts;

import codechicken.lib.packet.PacketCustom;
import codechicken.multipart.handler.MultipartSPH$;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Iterator;

@Mixin(value = MultipartSPH$.class, remap = false) public abstract class MixinMultipartColumnDesc {
    @Inject(method = "getDescPacket(Lnet/minecraft/world/chunk/Chunk;Ljava/util/Iterator;)Lcodechicken/lib/packet/PacketCustom;", at = @At("HEAD"), cancellable = true)
    private void rdpl$skipRubicDesc(Chunk chunk, Iterator<TileEntity> it, CallbackInfoReturnable<PacketCustom> cir) { if (CbmpRubicParts.rubic(chunk.getWorld())) { cir.setReturnValue(null); } }
}
