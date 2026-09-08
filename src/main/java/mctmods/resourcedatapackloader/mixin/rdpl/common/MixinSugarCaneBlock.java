package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.Config;

import net.minecraft.world.level.block.SugarCaneBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(SugarCaneBlock.class) public abstract class MixinSugarCaneBlock {
    @ModifyConstant(method = "randomTick", constant = @Constant(intValue = 3)) private int rdpl$growthLimit(int original) { return Config.content.caneMaxHeight(); }
}
