package mctmods.resourcedatapackloader.mixin.galacticraft;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IMinMaxHeight;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.util.Coords;

import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BlockVec3.class, remap = false) public class MixinBlockVec3 {
    @Shadow public int x;
    @Shadow public int z;

    @Unique private static boolean rdpl$rubic(World world) { return world instanceof IRubicWorld && ((IRubicWorld) world).rdpl$isRubicWorld(); }

    @ModifyConstant(method = "getBlockStateSafe_noChunkLoad", constant = @Constant(intValue = 0, expandZeroConditions = Constant.Condition.LESS_THAN_ZERO))
    private int rdpl$floor(int zero, World world) { return rdpl$rubic(world) ? ((IMinMaxHeight) world).rdpl$getMinHeight() : zero; }

    @ModifyConstant(method = "getBlockStateSafe_noChunkLoad", constant = @Constant(intValue = 256))
    private int rdpl$ceiling(int limit, World world) { return rdpl$rubic(world) ? ((IMinMaxHeight) world).rdpl$getMaxHeight() : limit; }

    @Redirect(method = "getBlockStateSafe_noChunkLoad",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;getBlockState(III)Lnet/minecraft/block/state/IBlockState;", remap = true))
    private IBlockState rdpl$loadedCubesOnly(Chunk chunk, int x, int y, int z, World world) {
        if (!rdpl$rubic(world)) { return chunk.getBlockState(x, y, z); }
        ICube cube = ((IRubicWorld) world).rdpl$getCubeCache().getLoadedCube(Coords.blockToCube(this.x), Coords.blockToCube(y), Coords.blockToCube(this.z));
        if (cube == null || cube.getStorage() == null) { return null; }
        return cube.getStorage().get(x & 15, y & 15, z & 15);
    }
}
