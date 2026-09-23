package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerList.class) public abstract class MixinPlayerList {
    @Redirect(method = "sendLevelInfo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    private boolean rdpl$lockedTime(GameRules rules, GameRules.Key<GameRules.BooleanValue> key, ServerPlayer player, ServerLevel level) { return rules.getBoolean(key) && ContentTerrain.lockedTime(level) < 0; }
}
