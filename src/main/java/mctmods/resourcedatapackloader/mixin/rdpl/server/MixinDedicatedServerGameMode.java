package mctmods.resourcedatapackloader.mixin.rdpl.server;

import mctmods.resourcedatapackloader.content.ContentServer;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class) public abstract class MixinDedicatedServerGameMode {
    @Shadow private GameType gameType;

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/DedicatedServer;loadAllWorlds(Ljava/lang/String;Ljava/lang/String;JLnet/minecraft/world/WorldType;Ljava/lang/String;)V")) private void rdpl$writeThePackMode(CallbackInfoReturnable<Boolean> cir) {
        GameType asked = ContentServer.gameModeFrom(ContentServer.worldGameMode());
        if (asked == GameType.NOT_SET) { return; }
        DedicatedServer server = (DedicatedServer) (Object) this;
        boolean hardcore = ContentServer.hardcoreAsked();
        if (gameType == asked && server.isHardcore() == hardcore) { return; }
        ContentLog.LOGGER.info("server.properties plays {} and a pack asks for {}, so the pack's mode is written to server.properties and every world plays it", server.isHardcore() ? "hardcore" : gameType.getName(), hardcore ? "hardcore" : asked.getName());
        gameType = asked;
        server.setProperty("gamemode", asked.getID());
        server.setProperty("hardcore", hardcore);
        server.saveProperties();
    }
}
