package mctmods.resourcedatapackloader.mixin.server;

import mctmods.resourcedatapackloader.common.pack.loading.ContentServer;
import mctmods.resourcedatapackloader.common.pregen.SpawnProgress;
import mctmods.resourcedatapackloader.common.rules.ContentSpawnChunks;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentTerrain;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class) public abstract class MinecraftServerMixin {
    @Redirect(method = "synchronizeTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z")) private boolean rdpl$lockedTime(GameRules rules, GameRules.Key<GameRules.BooleanValue> key, ServerLevel level) { return ContentTerrain.daylightCycles(rules, key, level); }

    @Inject(method = "loadLevel", at = @At("HEAD")) private void rdpl$loadBegins(CallbackInfo ci) {
        SpawnProgress.begin();
        ContentSpawnChunks.begin();
    }

    @Redirect(method = {"loadLevel", "prepareLevels"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getInt(Lnet/minecraft/world/level/GameRules$Key;)I")) private int rdpl$spawnCount(GameRules rules, GameRules.Key<GameRules.IntegerValue> key) { return ContentSpawnChunks.leftToTheGame() ? rules.getInt(key) : ContentSpawnChunks.bootChunks(); }

    @Inject(method = "prepareLevels", at = @At("HEAD")) private void rdpl$spawnPreparing(CallbackInfo ci) {
        SpawnProgress.spawnPreparing();
        if (ContentSpawnChunks.leftToTheGame()) { return; }
        ServerLevel overworld = ((MinecraftServer) (Object) this).overworld();
        ContentSpawnChunks.boot(overworld.getChunkSource(), new ChunkPos(overworld.getSharedSpawnPos()));
    }

    @Inject(method = "prepareLevels", at = @At("TAIL")) private void rdpl$spawnPrepared(CallbackInfo ci) { if (!ContentSpawnChunks.leftToTheGame()) { ContentSpawnChunks.booted(((MinecraftServer) (Object) this).overworld()); } }

    @Inject(method = "isLevelEnabled", at = @At("HEAD"), cancellable = true) private void rdpl$nether(Level level, CallbackInfoReturnable<Boolean> cir) { if (level.dimension() == Level.NETHER && ContentServer.off(ContentServer.nether())) { cir.setReturnValue(false); } }

    @Inject(method = "isSpawningMonsters", at = @At("HEAD"), cancellable = true) private void rdpl$spawnMonsters(CallbackInfoReturnable<Boolean> cir) { if (ContentServer.off(ContentServer.spawnMonsters())) { cir.setReturnValue(false); } }

    @Inject(method = "isSpawningAnimals", at = @At("HEAD"), cancellable = true) private void rdpl$spawnAnimals(CallbackInfoReturnable<Boolean> cir) { if (ContentServer.off(ContentServer.spawnAnimals())) { cir.setReturnValue(false); } }

    @Inject(method = "areNpcsEnabled", at = @At("HEAD"), cancellable = true) private void rdpl$spawnNpcs(CallbackInfoReturnable<Boolean> cir) { if (ContentServer.off(ContentServer.spawnNpcs())) { cir.setReturnValue(false); } }

    @Inject(method = "getAbsoluteMaxWorldSize", at = @At("HEAD"), cancellable = true) private void rdpl$maxSize(CallbackInfoReturnable<Integer> cir) {
        int asked = ContentServer.maxSize();
        if (asked > 0) { cir.setReturnValue(Math.min(asked, MinecraftServer.ABSOLUTE_MAX_WORLD_SIZE)); }
    }
}
