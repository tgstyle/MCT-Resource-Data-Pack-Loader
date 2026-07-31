package mctmods.resourcedatapackloader.mixin.vanillagrowth;

import mctmods.resourcedatapackloader.util.Config;

import net.minecraft.block.BlockCactus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BlockCactus.class)
public abstract class MixinBlockCactus {
    @ModifyConstant(method = "updateTick", constant = @Constant(intValue = 3))
    private int rdpl$growthLimit(int original) { return Math.max(1, Config.content.cactusMaxHeight); }
}
