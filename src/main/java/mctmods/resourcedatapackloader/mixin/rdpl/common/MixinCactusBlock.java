package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.Config;

import net.minecraft.world.level.block.CactusBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(CactusBlock.class) public abstract class MixinCactusBlock {
    @ModifyConstant(method = "randomTick", constant = @Constant(intValue = 3)) private int rdpl$growthLimit(int original) { return Config.content.cactusMaxHeight(); }
}
