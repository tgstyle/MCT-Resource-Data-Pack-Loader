package mctmods.resourcedatapackloader.mixin.world.level.block;

import mctmods.resourcedatapackloader.core.config.Config;

import net.minecraft.world.level.block.SugarCaneBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(SugarCaneBlock.class) public abstract class SugarCaneBlockMixin {
    @ModifyConstant(method = "randomTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V", constant = @Constant(intValue = 3)) private int rdpl$growthLimit(int original) { return Config.content.caneMaxHeight(); }
}
