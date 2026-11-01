package mctmods.resourcedatapackloader.mixin.world.level.block;

import mctmods.resourcedatapackloader.core.config.Config;

import net.minecraft.world.level.block.CactusBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(CactusBlock.class) public abstract class CactusBlockMixin {
    @ModifyConstant(method = "randomTick", constant = @Constant(intValue = 3)) private int rdpl$growthLimit(int original) { return Config.content.cactusMaxHeight(); }
}
