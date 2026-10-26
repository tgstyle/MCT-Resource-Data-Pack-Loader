package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentSpawnChunks;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class) public abstract class MixinMinecraftServerBoot {
    @Unique private int rdpl$bootReach;

    @Inject(method = "initialWorldChunkLoad", at = @At("HEAD")) private void rdpl$bootReach(CallbackInfo ci) { rdpl$bootReach = ContentSpawnChunks.bootReach(); }

    @ModifyConstant(method = "initialWorldChunkLoad", constant = @Constant(intValue = -192)) private int rdpl$bootFrom(int was) { return -16 * rdpl$bootReach; }

    @ModifyConstant(method = "initialWorldChunkLoad", constant = @Constant(intValue = 192)) private int rdpl$bootTo(int was) { return 16 * rdpl$bootReach; }

    @ModifyConstant(method = "initialWorldChunkLoad", constant = @Constant(intValue = 625)) private int rdpl$bootCount(int was) { return ContentSpawnChunks.bootPrepared(rdpl$bootReach); }

    @Inject(method = "initialWorldChunkLoad", at = @At("TAIL")) private void rdpl$bootDone(CallbackInfo ci) { ContentSpawnChunks.logBoot(rdpl$bootReach); }
}
