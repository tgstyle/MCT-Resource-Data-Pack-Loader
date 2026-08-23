package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Objects;

@Mixin(value = ChunkCache.class, priority = 1100) public class MixinChunkCache {
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

    @Inject(method = "getBlockState", at = @At("HEAD"), cancellable = true) private void rubic$getBlockState(BlockPos pos, CallbackInfoReturnable<IBlockState> cir) {
        IRubicWorld rubic = (IRubicWorld) this.world;
        if (!rubic.rdpl$isRubicWorld()) { return; }
        int y = pos.getY();
        if (y >= rubic.rdpl$getMinHeight() && y < rubic.rdpl$getMaxHeight()) {
            int x = (pos.getX() >> 4) - this.chunkX;
            int z = (pos.getZ() >> 4) - this.chunkZ;
            if (x >= 0 && x < this.chunkArray.length && z >= 0 && z < this.chunkArray[x].length) {
                Chunk chunk = this.chunkArray[x][z];
                if (chunk != null) {
                    cir.setReturnValue(chunk.getBlockState(pos));
                    return;
                }
            }
        }
        cir.setReturnValue(Objects.requireNonNull(Blocks.AIR).getDefaultState());
    }
}
