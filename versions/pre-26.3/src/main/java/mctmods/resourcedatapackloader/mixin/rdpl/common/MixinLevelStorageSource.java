package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.nbt.NbtAccounter;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LevelStorageSource.class) public abstract class MixinLevelStorageSource {
    @ModifyArg(method = "readExistingSavedData", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtIo;readCompressed(Ljava/nio/file/Path;Lnet/minecraft/nbt/NbtAccounter;)Lnet/minecraft/nbt/CompoundTag;"), index = 1) private static NbtAccounter rdpl$unlimitedSettings(NbtAccounter accounter) { return NbtAccounter.unlimitedHeap(); }
}
