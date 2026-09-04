package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkCache.class) public abstract class MixinChunkCache {
    @Shadow protected World world;
    @Shadow protected int chunkX;
    @Shadow protected int chunkZ;
    @Shadow protected Chunk[][] chunkArray;

    @Inject(method = "getBiome", at = @At("HEAD"), cancellable = true) private void rubic$getBiome(BlockPos pos, CallbackInfoReturnable<Biome> cir) {
        IRubicWorld rubic = (IRubicWorld) this.world;
        if (!rubic.rdpl$isRubicWorld()) { return; }
        int x = (pos.getX() >> 4) - this.chunkX;
        int z = (pos.getZ() >> 4) - this.chunkZ;
        if (x < 0 || x >= this.chunkArray.length || z < 0 || z >= this.chunkArray[x].length || this.chunkArray[x][z] == null) { cir.setReturnValue(Biomes.PLAINS); return; }
        Chunk chunk = this.chunkArray[x][z];
        ICube cube = ((IColumn) chunk).getLoadedCube(pos.getY() >> 4);
        cir.setReturnValue(cube != null ? cube.getBiome(pos) : chunk.getBiome(pos, this.world.getBiomeProvider()));
    }

    @ModifyConstant(method = "getLightFor",
            constant = @Constant(intValue = 0, expandZeroConditions = Constant.Condition.GREATER_THAN_OR_EQUAL_TO_ZERO))
    private int getLightFor_getMinHeight(int orig) { return ((IRubicWorld) world).rdpl$getMinHeight(); }

    @ModifyConstant(method = "getLightFor", constant = @Constant(intValue = 256)) private int getLightFor_getMaxHeight(int orig) {
        return ((IRubicWorld) world).rdpl$getMaxHeight();
    }
}
