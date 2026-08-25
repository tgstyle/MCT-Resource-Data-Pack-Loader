package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityEndGateway.class) public class MixinTileEntityEndGateway {
    @Redirect(method = "findExitPortal", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/chunk/Chunk;getTopFilledSegment()I"))
    private int getChunkTopFilledSegmentExitFromPortal(Chunk chunk) { return rdpl$topFilled(chunk); }

    @Redirect(method = "findSpawnpointInChunk", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/chunk/Chunk;getTopFilledSegment()I"))
    private static int getChunkTopFilledSegmentFindSpawnpoint(Chunk chunk) { return rdpl$topFilled(chunk); }

    @Unique private static int rdpl$topFilled(Chunk chunk) {
        int top = chunk.getTopFilledSegment();
        if (!((IRubicWorld) chunk.getWorld()).rdpl$isRubicWorld()) { return top; }
        for (ICube cube : ((IColumn) chunk).getLoadedCubes()) {
            if (!cube.isEmpty()) { return Math.max(top, 0); }
        }
        return 0;
    }

    /**
     * @author tgstyle
     * @reason Force the column's cubes loaded before the gateway searches it, since a rubic column holds none by default.
     */
    @Overwrite private static Chunk getChunk(World world, Vec3d pos) {
        Chunk chunk = world.getChunk(MathHelper.floor(pos.x / Cube.SIZE_D), MathHelper.floor(pos.z / Cube.SIZE_D));
        if (((IRubicWorld) chunk.getWorld()).rdpl$isRubicWorld()){
            for (int cubeY = 0; cubeY < 16; cubeY++) { ((IColumn) chunk).getCube(cubeY); }
        }
        return chunk;
    }
}
