package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentSpawnChunks;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class) public abstract class MixinMinecraftServerPaperBoot {
    @Shadow @Final private static Logger LOGGER;

    @Shadow public abstract void setUserMessage(String message);

    @Shadow protected abstract void outputPercentRemaining(String message, int percent);

    @Shadow protected abstract void clearCurrentTask();

    @Shadow public abstract boolean isServerRunning();

    @Inject(method = "initialWorldChunkLoad", at = @At("HEAD"), cancellable = true) private void rdpl$bootOwn(CallbackInfo ci) {
        if (!ContentSpawnChunks.overridden(0)) { return; }
        int reach = ContentSpawnChunks.bootReach();
        int total = ContentSpawnChunks.bootPrepared(reach);
        setUserMessage("menu.generatingTerrain");
        LOGGER.info("Preparing start region for level 0");
        WorldServer world = DimensionManager.getWorld(0);
        BlockPos spawn = world.getSpawnPoint();
        long last = MinecraftServer.getCurrentTimeMillis();
        int done = 0;
        for (int x = -reach; x <= reach && isServerRunning(); x++) {
            for (int z = -reach; z <= reach && isServerRunning(); z++) {
                long now = MinecraftServer.getCurrentTimeMillis();
                if (now - last > 1000L) {
                    outputPercentRemaining("Preparing spawn area", done * 100 / total);
                    last = now;
                }
                done++;
                world.getChunkProvider().provideChunk((spawn.getX() >> 4) + x, (spawn.getZ() >> 4) + z);
            }
        }
        clearCurrentTask();
        ContentSpawnChunks.logBoot(reach);
        ci.cancel();
    }
}
