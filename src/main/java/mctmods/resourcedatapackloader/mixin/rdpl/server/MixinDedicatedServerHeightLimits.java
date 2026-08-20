package mctmods.resourcedatapackloader.mixin.rdpl.server;

import mctmods.resourcedatapackloader.content.rubic.Rubic;

import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(DedicatedServer.class) public class MixinDedicatedServerHeightLimits {
    @ModifyConstant(method = "init", constant = @Constant(intValue = 256), require = 2) private int getDefaultBuildHeight(int oldValue) {
        return Rubic.MAX_SUPPORTED_BLOCK_Y + 1;
    }
}
