package mctmods.resourcedatapackloader.mixin.quark;

import mctmods.resourcedatapackloader.content.worldgen.ContentCascade;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.quark.world.world.CrystalCaveGenerator;
import java.util.Random;

@Mixin(value = CrystalCaveGenerator.class, remap = false) public abstract class MixinCrystalCaveGenerator {
    @Unique private static final int rdpl$SAFE_OFFSET = 8;

    @Redirect(method = "generate", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I", ordinal = 0), remap = false)
    private int rdpl$offsetX(Random random, int bound) { return random.nextInt(bound) + rdpl$SAFE_OFFSET; }

    @Redirect(method = "generate", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I", ordinal = 1), remap = false)
    private int rdpl$offsetZ(Random random, int bound) { return random.nextInt(bound) + rdpl$SAFE_OFFSET; }

    @Redirect(method = "hollowOut", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", remap = true))
    private IBlockState rdpl$readHollow(World world, BlockPos pos) { return ContentCascade.stateOrUnloaded(world, pos); }

    @Redirect(method = "makeCrystal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", remap = true))
    private IBlockState rdpl$readCrystal(World world, BlockPos pos) { return ContentCascade.stateOrUnloaded(world, pos); }

    @Redirect(method = "hollowOut", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockToAir(Lnet/minecraft/util/math/BlockPos;)Z", remap = true))
    private boolean rdpl$carve(World world, BlockPos pos) {
        if (!ContentCascade.loaded(world, pos)) { return false; }
        return world.setBlockToAir(pos);
    }

    @Redirect(method = "hollowOut", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Z", remap = true))
    private boolean rdpl$placeOre(World world, BlockPos pos, IBlockState state) {
        if (!ContentCascade.loaded(world, pos)) { return false; }
        return world.setBlockState(pos, state);
    }

    @Redirect(method = "makeCrystal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Z", remap = true))
    private boolean rdpl$placeCrystal(World world, BlockPos pos, IBlockState state) {
        if (!ContentCascade.loaded(world, pos)) { return false; }
        return world.setBlockState(pos, state);
    }
}
