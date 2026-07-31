package mctmods.resourcedatapackloader.mixin.quark;

import mctmods.resourcedatapackloader.content.worldgen.ContentCascade;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.quark.world.feature.VariedDungeons;

@Mixin(value = VariedDungeons.class, remap = false)
public abstract class MixinVariedDungeons {
    @Redirect(method = "placeDungeonAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", ordinal = 0, remap = true))
    private IBlockState rdpl$guardSweep(WorldServer world, BlockPos pos) { return ContentCascade.stateOrUnloaded(world, pos); }
}
