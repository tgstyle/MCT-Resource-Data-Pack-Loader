package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.util.Coords;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.function.BooleanSupplier;

@Mixin(ViewFrustum.class) public class MixinViewFrustum {
    @Shadow @Final protected World world;
    @Shadow public RenderChunk[] renderChunks;
    @Shadow protected int countChunksX;
    @Shadow protected int countChunksY;
    @Shadow protected int countChunksZ;
    @Unique private int rubic_oldViewX = Integer.MAX_VALUE;
    @Unique private int rubic_oldViewY = Integer.MAX_VALUE;
    @Unique private int rubic_oldViewZ = Integer.MAX_VALUE;

    @Inject(method = "updateChunkPositions", at = @At(value = "HEAD"), cancellable = true, require = 1) private void updateChunkPositionsInject(double viewEntityX, double viewEntityZ, CallbackInfo cbi) {
        if (!((IRubicWorld) world).rdpl$isRubicWorld()) { return; }
        Entity view = Minecraft.getMinecraft().getRenderViewEntity();
        if (view == null) {
            cbi.cancel();
            return;
        }
        int viewX = Coords.blockToCube(view.posX);
        int viewY = Coords.blockToCube(view.posY);
        int viewZ = Coords.blockToCube(view.posZ);
        int dx = countChunksX;
        int dy = countChunksY;
        int dz = countChunksZ;
        RenderChunk[] chunks = this.renderChunks;
        int minX = viewX - (dx >> 1);
        int minY = viewY - (dy >> 1);
        int minZ = viewZ - (dz >> 1);
        int px = MathHelper.intFloorDiv(minX, dx) * dx;
        int py = MathHelper.intFloorDiv(minY, dy) * dy;
        int pz = MathHelper.intFloorDiv(minZ, dz) * dz;
        long changeX = (long) viewX - this.rubic_oldViewX;
        long changeY = (long) viewY - this.rubic_oldViewY;
        long changeZ = (long) viewZ - this.rubic_oldViewZ;
        this.rubic_oldViewX = viewX;
        this.rubic_oldViewY = viewY;
        this.rubic_oldViewZ = viewZ;
        if (Math.abs(changeX) <= 1 && Math.abs(changeY) <= 1 && Math.abs(changeZ) <= 1) {
            if (changeX != 0) {
                int xIndex = Math.floorMod(changeX < 0 ? minX - px : minX - px - 1, dx);
                int blockX = rubic_getBlockCoord(xIndex, dx, px, minX);
                for (int zIndex = 0; zIndex < dz; zIndex++) {
                    int blockZ = rubic_getBlockCoord(zIndex, dz, pz, minZ);
                    int idxZ = zIndex * dy * dx;
                    for (int yIndex = 0; yIndex < dy; yIndex++) {
                        int blockY = rubic_getBlockCoord(yIndex, dy, py, minY);
                        int idxYZ = idxZ + yIndex * dx;
                        chunks[idxYZ + xIndex].setPosition(blockX, blockY, blockZ);
                    }
                }
            }
            if (changeY != 0) {
                int yIndex = Math.floorMod(changeY < 0 ? minY - py : minY - py - 1, dy);
                int blockY = rubic_getBlockCoord(yIndex, dy, py, minY);
                for (int zIndex = 0; zIndex < dz; zIndex++) {
                    int blockZ = rubic_getBlockCoord(zIndex, dz, pz, minZ);
                    int idxZ = zIndex * dy * dx;
                    int idxYZ = idxZ + yIndex * dx;
                    for (int xIndex = 0; xIndex < dx; xIndex++) {
                        int blockX = rubic_getBlockCoord(xIndex, dx, px, minX);
                        chunks[idxYZ + xIndex].setPosition(blockX, blockY, blockZ);
                    }
                }
            }
            if (changeZ != 0) {
                int zIndex = Math.floorMod(changeZ < 0 ? minZ - pz : minZ - pz - 1, dz);
                int blockZ = rubic_getBlockCoord(zIndex, dz, pz, minZ);
                int idxZ = zIndex * dy * dx;
                for (int yIndex = 0; yIndex < dy; yIndex++) {
                    int blockY = rubic_getBlockCoord(yIndex, dy, py, minY);
                    int idxYZ = idxZ + yIndex * dx;
                    for (int xIndex = 0; xIndex < dx; xIndex++) {
                        int blockX = rubic_getBlockCoord(xIndex, dx, px, minX);
                        chunks[idxYZ + xIndex].setPosition(blockX, blockY, blockZ);
                    }
                }
            }
            assert ((BooleanSupplier) () -> {
                for (int zIndex = 0; zIndex < dz; zIndex++) {
                    int blockZ = rubic_getBlockCoord(zIndex, dz, pz, minZ);
                    int idxZ = zIndex * dy * dx;
                    for (int yIndex = 0; yIndex < dy; yIndex++) {
                        int blockY = rubic_getBlockCoord(yIndex, dy, py, minY);
                        int idxYZ = idxZ + yIndex * dx;
                        for (int xIndex = 0; xIndex < dx; xIndex++) {
                            int blockX = rubic_getBlockCoord(xIndex, dx, px, minX);
                            BlockPos pos = chunks[idxYZ + xIndex].getPosition();
                            if (pos.getX() != blockX || pos.getY() != blockY || pos.getZ() != blockZ) { return false; }
                        }
                    }
                }
                return true;
            }).getAsBoolean() : "Not all RenderChunks are in the correct position!";
        }
        else {
            for (int zIndex = 0; zIndex < dz; zIndex++) {
                int blockZ = rubic_getBlockCoord(zIndex, dz, pz, minZ);
                int idxZ = zIndex * dy * dx;
                for (int yIndex = 0; yIndex < dy; yIndex++) {
                    int blockY = rubic_getBlockCoord(yIndex, dy, py, minY);
                    int idxYZ = idxZ + yIndex * dx;
                    for (int xIndex = 0; xIndex < dx; xIndex++) {
                        int blockX = rubic_getBlockCoord(xIndex, dx, px, minX);
                        chunks[idxYZ + xIndex].setPosition(blockX, blockY, blockZ);
                    }
                }
            }
        }
        cbi.cancel();
    }

    @Unique private static int rubic_getBlockCoord(int index, int d, int p, int min) {
        int coord = p + index;
        if (coord < min) { coord += d; }
        return coord << 4;
    }

    @Inject(method = "getRenderChunk", at = @At(value = "HEAD"), cancellable = true, require = 1) private void getRenderChunkInject(BlockPos pos, CallbackInfoReturnable<RenderChunk> cbi) {
        if (!((IRubicWorld) world).rdpl$isRubicWorld()) { return; }
        int x = Coords.blockToCube(pos.getX());
        int y = Coords.blockToCube(pos.getY());
        int z = Coords.blockToCube(pos.getZ());
        x %= this.countChunksX;
        if (x < 0) { x += this.countChunksX; }
        y %= this.countChunksY;
        if (y < 0) { y += this.countChunksY; }
        z %= this.countChunksZ;
        if (z < 0) { z += this.countChunksZ; }
        final int index = (z * this.countChunksY + y) * this.countChunksX + x;
        RenderChunk renderChunk = this.renderChunks[index];
        cbi.setReturnValue(renderChunk);
    }
}
