package mctmods.resourcedatapackloader.mixin.integration.customspawner;

import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IColumnInternal;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.common.util.world.Coords;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;

import drzhark.customspawner.CustomSpawner;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CustomSpawner.class) public abstract class MixinCustomSpawner {
    @Inject(method = "getRandomSpawningPointInChunk(Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/world/World;)Lnet/minecraft/util/math/BlockPos;", at = @At("HEAD"), cancellable = true, remap = false) private static void rdpl$pickLoadedCube(Chunk chunk, World world, CallbackInfoReturnable<BlockPos> cir) {
        if (!((IColumnInternal) chunk).isRubicColumn()) { return; }
        int x = Coords.cubeToMinBlock(chunk.x) + world.rand.nextInt(ICube.SIZE);
        int z = Coords.cubeToMinBlock(chunk.z) + world.rand.nextInt(ICube.SIZE);
        int top = chunk.getHeight(new BlockPos(x, 0, z));
        List<ICube> reachable = new ArrayList<>();
        for (ICube cube : ((IColumn) chunk).getLoadedCubes()) {
            if (cube.getCoords().getMinBlockY() <= top) { reachable.add(cube); }
        }
        if (reachable.isEmpty()) {
            cir.setReturnValue(new BlockPos(x, GenHeights.floor(world, -1), z));
            return;
        }
        ICube picked = reachable.get(world.rand.nextInt(reachable.size()));
        cir.setReturnValue(new BlockPos(x, picked.getCoords().getMinBlockY() + world.rand.nextInt(ICube.SIZE), z));
    }

    @Redirect(method = "doCustomSpawning(Lnet/minecraft/world/WorldServer;Ldrzhark/customspawner/type/EntitySpawnType;I)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", remap = true), remap = false) private static IBlockState rdpl$guardSpawnState(Chunk chunk, BlockPos pos) { return rdpl$stateOrBedrock(chunk, pos); }

    @Redirect(method = "canCreatureTypeSpawnAtLocation(Lnet/minecraft/world/chunk/Chunk;Ldrzhark/customspawner/type/EntitySpawnType;Lnet/minecraft/world/biome/Biome$SpawnListEntry;Lnet/minecraft/world/WorldServer;Lnet/minecraft/util/math/BlockPos;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", remap = true), remap = false) private static IBlockState rdpl$guardCanSpawnState(Chunk chunk, BlockPos pos) { return rdpl$stateOrBedrock(chunk, pos); }

    @Unique private static IBlockState rdpl$stateOrBedrock(Chunk chunk, BlockPos pos) {
        if (((IColumnInternal) chunk).isRubicColumn() && ((IColumn) chunk).getLoadedCube(Coords.blockToCube(pos.getY())) == null) { return Objects.requireNonNull(Blocks.BEDROCK).getDefaultState(); }
        return chunk.getBlockState(pos);
    }
}
