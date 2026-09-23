package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentSpawnChunks;
import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;
import mctmods.resourcedatapackloader.content.worldgen.SpawnProgress;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class) public abstract class MixinMinecraftServer {
    @Redirect(method = "synchronizeTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    private boolean rdpl$lockedTime(GameRules rules, GameRules.Key<GameRules.BooleanValue> key, ServerLevel level) { return rules.getBoolean(key) && ContentTerrain.lockedTime(level) < 0; }

    @Inject(method = "loadLevel", at = @At("HEAD")) private void rdpl$loadBegins(CallbackInfo ci) {
        SpawnProgress.begin();
        ContentSpawnChunks.begin();
    }

    @Redirect(method = "loadLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getInt(Lnet/minecraft/world/level/GameRules$Key;)I"))
    private int rdpl$spawnListener(GameRules rules, GameRules.Key<GameRules.IntegerValue> key) { return ContentSpawnChunks.leftToTheGame() ? rules.getInt(key) : ContentSpawnChunks.bootChunks(); }

    @Inject(method = "prepareLevels", at = @At("HEAD")) private void rdpl$spawnPreparing(CallbackInfo ci) {
        SpawnProgress.spawnPreparing();
        if (ContentSpawnChunks.leftToTheGame()) { return; }
        ServerLevel overworld = ((MinecraftServer) (Object) this).overworld();
        ContentSpawnChunks.boot(overworld.getChunkSource(), new ChunkPos(overworld.getSharedSpawnPos()));
    }

    @Redirect(method = "prepareLevels", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getInt(Lnet/minecraft/world/level/GameRules$Key;)I"))
    private int rdpl$spawnCount(GameRules rules, GameRules.Key<GameRules.IntegerValue> key) { return ContentSpawnChunks.leftToTheGame() ? rules.getInt(key) : ContentSpawnChunks.bootChunks(); }

    @Inject(method = "prepareLevels", at = @At("TAIL")) private void rdpl$spawnPrepared(CallbackInfo ci) {
        if (!ContentSpawnChunks.leftToTheGame()) { ContentSpawnChunks.booted(((MinecraftServer) (Object) this).overworld()); }
    }
}
