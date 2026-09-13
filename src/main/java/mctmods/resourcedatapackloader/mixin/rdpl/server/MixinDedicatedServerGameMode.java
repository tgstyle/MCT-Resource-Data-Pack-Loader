package mctmods.resourcedatapackloader.mixin.rdpl.server;

import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Locale;
import java.util.Properties;

@Mixin(DedicatedServer.class) public abstract class MixinDedicatedServerGameMode {
    @Shadow @Final private DedicatedServerSettings settings;

    @Inject(method = "initServer", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/server/ServerLifecycleHooks;handleServerAboutToStart(Lnet/minecraft/server/MinecraftServer;)V", shift = At.Shift.AFTER))
    private void rdpl$writeThePackMode(CallbackInfoReturnable<Boolean> cir) {
        String mode = ContentTerrain.worldGameMode().trim().toLowerCase(Locale.ROOT);
        boolean hardcore = "hardcore".equals(mode);
        GameType asked = hardcore ? GameType.SURVIVAL : GameType.byName(mode, null);
        if (asked == null) { return; }
        DedicatedServer server = (DedicatedServer) (Object) this;
        DedicatedServerProperties held = settings.getProperties();
        if (held.gamemode == asked && held.hardcore == hardcore) { return; }
        ContentLog.LOGGER.info("server.properties plays {} and a pack asks for {}, so the pack's mode is written to server.properties and every world plays it", held.hardcore ? "hardcore" : held.gamemode.getName(), hardcore ? "hardcore" : asked.getName());
        settings.update(current -> {
            Properties written = ((ISettings) current).rdpl$cloneProperties();
            written.setProperty("gamemode", asked.getName());
            written.setProperty("hardcore", Boolean.toString(hardcore));
            return new DedicatedServerProperties(written);
        });
        server.getWorldData().setGameType(asked);
    }
}
