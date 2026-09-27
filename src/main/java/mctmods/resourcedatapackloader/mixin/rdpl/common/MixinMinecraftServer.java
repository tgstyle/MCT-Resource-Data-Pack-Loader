package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.content.worldgen.ContentSpawnChunks;
import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;
import mctmods.resourcedatapackloader.content.worldgen.SpawnProgress;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkLoadCounter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

@Mixin(MinecraftServer.class) public abstract class MixinMinecraftServer {
    @Unique private List<Long> rdpl$sentLocks = List.of();

    @ModifyExpressionValue(method = "forceGameTimeSynchronization()V", at = @At(value = "NEW", target = "(JLjava/util/Map;)Lnet/minecraft/network/protocol/game/ClientboundSetTimePacket;"))
    private ClientboundSetTimePacket rdpl$lockedTime(ClientboundSetTimePacket packet) {
        MinecraftServer self = (MinecraftServer) (Object) this;
        List<Long> locks = self.registryAccess().lookupOrThrow(Registries.WORLD_CLOCK).listElements().map(ContentTerrain::lockedTime).toList();
        if (locks.equals(rdpl$sentLocks)) { return packet; }
        rdpl$sentLocks = locks;
        return self.clockManager().createFullSyncPacket();
    }

    @ModifyExpressionValue(method = "tickServer(Ljava/util/function/BooleanSupplier;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ServerTickRateManager;isSprinting()Z")) private boolean rdpl$awakeWhilePregen(boolean sprinting) { return sprinting || ContentPregen.busy(); }

    @Inject(method = "loadLevel()V", at = @At("HEAD")) private void rdpl$loadBegins(CallbackInfo ci) {
        SpawnProgress.begin();
        ContentSpawnChunks.begin();
    }

    @Inject(method = "prepareLevels()V", at = @At("HEAD")) private void rdpl$spawnPreparing(CallbackInfo ci) { SpawnProgress.spawnPreparing(); }

    @WrapOperation(method = "prepareLevels()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkLoadCounter;track(Lnet/minecraft/server/level/ServerLevel;Ljava/lang/Runnable;)V"))
    private void rdpl$spawnBoot(ChunkLoadCounter counter, ServerLevel level, Runnable scheduler, Operation<Void> original) {
        if (ContentSpawnChunks.leftToTheGame() || level.dimension() != Level.OVERWORLD) {
            original.call(counter, level, scheduler);
            return;
        }
        original.call(counter, level, (Runnable) () -> {
            scheduler.run();
            ContentSpawnChunks.boot(level.getChunkSource(), ChunkPos.containing(((MinecraftServer) (Object) this).getWorldData().overworldData().getRespawnData().pos()));
        });
    }

    @Inject(method = "prepareLevels()V", at = @At("TAIL")) private void rdpl$spawnPrepared(CallbackInfo ci) {
        if (!ContentSpawnChunks.leftToTheGame()) { ContentSpawnChunks.booted(((MinecraftServer) (Object) this).overworld()); }
    }

    @Inject(method = "setRespawnData(Lnet/minecraft/world/level/storage/LevelData$RespawnData;)V", at = @At("TAIL")) private void rdpl$spawnHeld(LevelData.RespawnData respawnData, CallbackInfo ci) {
        if (ContentSpawnChunks.leftToTheGame() || respawnData.dimension() != Level.OVERWORLD) { return; }
        ServerLevel overworld = ((MinecraftServer) (Object) this).overworld();
        if (overworld != null) { ContentSpawnChunks.hold(overworld, ChunkPos.containing(respawnData.pos())); }
    }
}
