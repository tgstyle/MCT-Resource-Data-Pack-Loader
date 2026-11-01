package mctmods.resourcedatapackloader.mixin.world.level.chunk.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.chunk.storage.IOWorker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@SuppressWarnings("target") @Mixin(IOWorker.class) public abstract class IOWorkerMixin {
    @ModifyArg(method = "lambda$loadAsync$4(Lnet/minecraft/world/level/ChunkPos;)Lcom/mojang/datafixers/util/Either;", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ofNullable(Ljava/lang/Object;)Ljava/util/Optional;", ordinal = 0)) private Object rdpl$pendingCopy(Object pending) { return pending instanceof CompoundTag tag ? tag.copy() : pending; }
}
