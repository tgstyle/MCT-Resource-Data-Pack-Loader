package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentSpawnChunks;
import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;
import mctmods.resourcedatapackloader.content.worldgen.SpawnProgress;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class) public abstract class MixinMinecraftServer {
    @Redirect(method = "synchronizeTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    private boolean rdpl$lockedTime(GameRules rules, GameRules.Key<GameRules.BooleanValue> key, ServerLevel level) { return rules.getBoolean(key) && ContentTerrain.lockedTime(level) < 0; }

    @ModifyConstant(method = "loadLevel", constant = @Constant(intValue = 11)) private int rdpl$spawnListener(int was) { return ContentSpawnChunks.leftToTheGame() ? was : ContentSpawnChunks.bootChunks(); }

    @Redirect(method = "prepareLevels", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerChunkCache;addRegionTicket(Lnet/minecraft/server/level/TicketType;Lnet/minecraft/world/level/ChunkPos;ILjava/lang/Object;)V"))
    private <T> void rdpl$spawnBootTicket(ServerChunkCache chunks, TicketType<T> type, ChunkPos pos, int distance, T value) {
        if (ContentSpawnChunks.leftToTheGame()) { chunks.addRegionTicket(type, pos, distance, value); }
        else { ContentSpawnChunks.boot(chunks, pos); }
    }

    @Inject(method = "loadLevel", at = @At("HEAD")) private void rdpl$loadBegins(CallbackInfo ci) {
        SpawnProgress.begin();
        ContentSpawnChunks.begin();
    }

    @Inject(method = "prepareLevels", at = @At("HEAD")) private void rdpl$spawnPreparing(CallbackInfo ci) { SpawnProgress.spawnPreparing(); }

    @Inject(method = "prepareLevels", at = @At("TAIL")) private void rdpl$spawnPrepared(CallbackInfo ci) {
        if (!ContentSpawnChunks.leftToTheGame()) { ContentSpawnChunks.booted(((MinecraftServer) (Object) this).overworld()); }
    }
}
