package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentSpawnChunks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class) public abstract class MixinServerLevelSpawn {
    @Redirect(method = "setDefaultSpawnPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getInt(Lnet/minecraft/world/level/GameRules$Key;)I"))
    private int rdpl$spawnRuleTicket(GameRules rules, GameRules.Key<GameRules.IntegerValue> key) { return ContentSpawnChunks.leftToTheGame() ? rules.getInt(key) : 0; }

    @Inject(method = "setDefaultSpawnPos", at = @At("TAIL")) private void rdpl$spawnHeld(BlockPos pos, float angle, CallbackInfo ci) {
        ServerLevel level = (ServerLevel) (Object) this;
        if (!ContentSpawnChunks.leftToTheGame() && level.dimension() == Level.OVERWORLD) { ContentSpawnChunks.hold(level, new ChunkPos(pos)); }
    }
}
