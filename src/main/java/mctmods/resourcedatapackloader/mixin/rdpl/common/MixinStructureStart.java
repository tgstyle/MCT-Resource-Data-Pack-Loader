package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.worldgen.interfaces.IRubicFeatureStart;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardKeep;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructureStart.class) @Implements(@Interface(iface = IRubicFeatureStart.class, prefix = "start$")) public abstract class MixinStructureStart {
    @Redirect(method = "generateStructure", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/structure/StructureComponent;addComponentParts(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/world/gen/structure/StructureBoundingBox;)Z"))
    private boolean rdpl$openFrontage(StructureComponent piece, World world, Random rand, StructureBoundingBox clip) {
        StructureStart self = StructureStart.class.cast(this);
        ContentBeard.building(self);
        boolean built;
        try {
            if (ContentBeard.wanted()) {
                ContentBeard.fellFor(self, piece, world, clip);
                BeardKeep.watch(world, piece, clip);
            }
            built = piece.addComponentParts(world, rand, clip);
            if (!built && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("{} at {}, {} told the game it is done while chunk {}, {} was being built, so the game now drops it and no later chunk will build or dress it again", piece.getClass().getSimpleName(), piece.getBoundingBox().minX, piece.getBoundingBox().minZ, clip.minX >> 4, clip.minZ >> 4); }
            if (ContentBeard.wanted()) { BeardKeep.learn(world); }
            if (built && ContentBeard.wanted()) { ContentBeard.openAround(self, piece, world, clip); }
        }
        finally { ContentBeard.building(null); }
        return built;
    }

    @Inject(method = "generateStructure", at = @At("RETURN")) private void rdpl$plaza(World worldIn, Random rand, StructureBoundingBox structurebb, CallbackInfo ci) {
        StructureStart self = StructureStart.class.cast(this);
        if (!ContentBeard.wanted() || self.getComponents().isEmpty()) { return; }
        if (!(self.getComponents().get(0) instanceof StructureVillagePieces.Start)) { return; }
        ContentBeard.building(self);
        try {
            for (StructureComponent well : self.getComponents()) {
                if (well instanceof StructureVillagePieces.Well) { ContentBeard.wellPlaza(self, well, worldIn, structurebb); }
            }
        }
        finally { ContentBeard.building(null); }
    }

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
