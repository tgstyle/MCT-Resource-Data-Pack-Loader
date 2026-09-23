package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentSpawnChunks;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = MinecraftServer.class, priority = 100) public abstract class MixinMinecraftServerSpawnWait {
    @ModifyConstant(method = "prepareLevels", constant = @Constant(intValue = 441), require = 0) private int rdpl$spawnCount(int was) { return ContentSpawnChunks.leftToTheGame() ? was : ContentSpawnChunks.bootCount(); }
}
