package mctmods.resourcedatapackloader.mixin.integration.cbmultipart;

import mctmods.resourcedatapackloader.core.integration.forgemultipartcbe.CbmpRubicParts;

import codechicken.lib.packet.PacketCustom;
import codechicken.multipart.handler.MultipartSPH$;
import java.util.Iterator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MultipartSPH$.class, remap = false) public abstract class MixinMultipartColumnDesc {
    @Inject(method = "getDescPacket(Lnet/minecraft/world/chunk/Chunk;Ljava/util/Iterator;)Lcodechicken/lib/packet/PacketCustom;", at = @At("HEAD"), cancellable = true) private void rdpl$skipRubicDesc(Chunk chunk, Iterator<TileEntity> it, CallbackInfoReturnable<PacketCustom> cir) { if (CbmpRubicParts.rubic(chunk.getWorld())) { cir.setReturnValue(null); } }
}
