package mctmods.resourcedatapackloader.mixin.cbmultipart;

import mctmods.resourcedatapackloader.util.compat.CbmpRubicParts;

import codechicken.lib.packet.PacketCustom;
import codechicken.multipart.handler.MultipartSaveLoad;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MultipartSaveLoad.TileNBTContainer.class) public abstract class MixinMultipartPlaceholder {
    @Redirect(method = "update()V", at = @At(value = "INVOKE", target = "Lcodechicken/lib/packet/PacketCustom;sendToChunk(Lnet/minecraft/world/World;II)V", remap = false))
    private void rdpl$skipSelfConvertDesc(PacketCustom packet, World world, int chunkX, int chunkZ) { if (!CbmpRubicParts.rubic(world)) { packet.sendToChunk(world, chunkX, chunkZ); } }
}
