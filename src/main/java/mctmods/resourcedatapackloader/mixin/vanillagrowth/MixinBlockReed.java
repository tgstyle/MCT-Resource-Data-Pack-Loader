package mctmods.resourcedatapackloader.mixin.vanillagrowth;

import mctmods.resourcedatapackloader.util.Config;

import net.minecraft.block.BlockReed;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BlockReed.class)
public abstract class MixinBlockReed {
    @ModifyConstant(method = "updateTick", constant = @Constant(intValue = 3))
    private int rdpl$growthLimit(int original) { return Math.max(1, Config.content.caneMaxHeight); }
}
