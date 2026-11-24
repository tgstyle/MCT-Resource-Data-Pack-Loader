package mctmods.resourcedatapackloader.mixin.client.world;

import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.common.util.world.Coords;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;

import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkCache.class) public abstract class MixinChunkCache {
    @Shadow public World world;
    @Shadow protected int chunkX;
    @Shadow protected int chunkZ;
    @Shadow protected Chunk[][] chunkArray;

    @Inject(method = "getBiome", at = @At("HEAD"), cancellable = true) private void rdpl$getBiome(BlockPos pos, CallbackInfoReturnable<Biome> cir) {
        if (!GenHeights.rubic(this.world)) { return; }
        int x = Coords.blockToCube(pos.getX()) - this.chunkX;
        int z = Coords.blockToCube(pos.getZ()) - this.chunkZ;
        if (x < 0 || x >= this.chunkArray.length || z < 0 || z >= this.chunkArray[x].length || this.chunkArray[x][z] == null) { cir.setReturnValue(Biomes.PLAINS); return; }
        Chunk chunk = this.chunkArray[x][z];
        ICube cube = ((IColumn) chunk).getLoadedCube(Coords.blockToCube(pos.getY()));
        cir.setReturnValue(cube != null ? cube.getBiome(pos) : chunk.getBiome(pos, this.world.getBiomeProvider()));
    }

    @ModifyConstant(method = "getLightFor", constant = @Constant(intValue = 0, expandZeroConditions = Constant.Condition.GREATER_THAN_OR_EQUAL_TO_ZERO)) private int rdpl$getLightFor_getMinHeight(int orig) { return GenHeights.floor(world, orig); }

    @ModifyConstant(method = "getLightFor", constant = @Constant(intValue = 256)) private int rdpl$getLightFor_getMaxHeight(int orig) { return GenHeights.ceiling(world, orig); }
}
