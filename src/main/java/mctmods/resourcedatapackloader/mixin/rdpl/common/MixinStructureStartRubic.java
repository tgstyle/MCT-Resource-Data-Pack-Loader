package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.worldgen.interfaces.IRubicFeatureStart;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureStart;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructureStart.class) @Implements(@Interface(iface = IRubicFeatureStart.class, prefix = "start$")) public abstract class MixinStructureStartRubic {
    @Shadow public abstract int getChunkPosX();

    @Shadow public abstract int getChunkPosZ();

    @Unique private int rdpl$cubeY;

    @Unique private int rdpl$getChunkPosY() { return this.rdpl$cubeY; }

    @Inject(method = "writeStructureComponentsToNBT",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setInteger(Ljava/lang/String;I)V", ordinal = 0)
    )
    private void writeYToNbt(int chunkX, int chunkZ, CallbackInfoReturnable<NBTTagCompound> cir, @Local(name = "nbttagcompound") NBTTagCompound nbttagcompound) { nbttagcompound.setInteger("ChunkY", this.rdpl$cubeY); }

    @Inject(method = "readStructureComponentsFromNBT", at = @At("HEAD")) private void readYFromNBT(World worldIn, NBTTagCompound tagCompound, CallbackInfo cbi) {
        if (tagCompound.hasKey("ChunkY")) { this.rdpl$cubeY = tagCompound.getInteger("ChunkY"); }
    }

    public int start$getX() { return getChunkPosX(); }

    public int start$getY() { return rdpl$getChunkPosY(); }

    public int start$getZ() { return getChunkPosZ(); }

}
