package mctmods.resourcedatapackloader.mixin.server.dedicated;

import mctmods.resourcedatapackloader.common.rubic.Rubic;

import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(DedicatedServer.class) public class MixinDedicatedServerHeightLimits {
    @ModifyConstant(method = "init", constant = @Constant(intValue = 256), require = 2) private int rdpl$getDefaultBuildHeight(int oldValue) { return Rubic.MAX_SUPPORTED_BLOCK_Y + 1; }
}
