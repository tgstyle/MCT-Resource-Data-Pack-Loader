package mctmods.resourcedatapackloader.mixin.world.level.chunk;

import mctmods.resourcedatapackloader.common.worldgen.placement.ContentChunkTokens;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkAccess.class) public abstract class ChunkAccessMixin {
    @Inject(method = "readAttachmentsFromNBT(Lnet/minecraft/core/HolderLookup$Provider;Lnet/minecraft/nbt/CompoundTag;)V", at = @At("HEAD")) private void rdpl$oldTokens(HolderLookup.Provider provider, CompoundTag tag, CallbackInfo ci) { ContentChunkTokens.upgrade(tag); }
}
