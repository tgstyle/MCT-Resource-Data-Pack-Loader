package mctmods.resourcedatapackloader.mixin.rubiclight.client;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkCache.class) public abstract class MixinChunkCacheLightRubic {
    @Shadow protected World world;
    @Shadow protected int chunkX;
    @Shadow protected int chunkZ;
    @Shadow protected Chunk[][] chunkArray;

    @Shadow public abstract IBlockState getBlockState(BlockPos pos);

    @Shadow public abstract int getLightFor(EnumSkyBlock type, BlockPos pos);

    @Inject(method = "getLightForExt", at = @At("HEAD"), cancellable = true)
    private void rdpl$lightForExtRubic(EnumSkyBlock type, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        IRubicWorld rubic = (IRubicWorld) world;
        if (!rubic.rdpl$isRubicWorld()) { return; }
        cir.setReturnValue(rdpl$lightForExt(rubic, type, pos));
    }

    @Unique private int rdpl$lightForExt(IRubicWorld rubic, EnumSkyBlock type, BlockPos pos) {
        if (type == EnumSkyBlock.SKY && !world.provider.hasSkyLight()) { return 0; }
        if (pos.getY() < rubic.rdpl$getMinHeight() || pos.getY() >= rubic.rdpl$getMaxHeight()) { return type.defaultLightValue; }
        if (getBlockState(pos).useNeighborBrightness()) {
            int most = 0;
            for (EnumFacing facing : EnumFacing.values()) {
                most = Math.max(most, getLightFor(type, pos.offset(facing)));
                if (most >= 15) { return most; }
            }
            return most;
        }
        int x = (pos.getX() >> 4) - chunkX;
        int z = (pos.getZ() >> 4) - chunkZ;
        if (x < 0 || x >= chunkArray.length || z < 0 || z >= chunkArray[x].length || chunkArray[x][z] == null) { return type.defaultLightValue; }
        return chunkArray[x][z].getLightFor(type, pos);
    }
}
