package mctmods.resourcedatapackloader.mixin.rdpl.server;

import mctmods.resourcedatapackloader.content.ContentServer;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Settings;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Mixin(DedicatedServer.class) public abstract class MixinDedicatedServerProperties {
    @Shadow @Final private DedicatedServerSettings settings;

    @Inject(method = "initServer", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/server/ServerLifecycleHooks;handleServerAboutToStart(Lnet/minecraft/server/MinecraftServer;)Z", shift = At.Shift.AFTER, remap = false)) private void rdpl$writeThePackSettings(CallbackInfoReturnable<Boolean> cir) {
        DedicatedServer server = (DedicatedServer) (Object) this;
        rdpl$writeGameMode(server);
        rdpl$writeProperties();
        ContentServer.applyTo(server);
    }

    @Unique private void rdpl$writeGameMode(DedicatedServer server) {
        String mode = Settings.lower(ContentServer.worldGameMode());
        boolean hardcore = "hardcore".equals(mode);
        GameType asked = hardcore ? GameType.SURVIVAL : GameType.byName(mode, null);
        if (asked == null) { return; }
        DedicatedServerProperties held = settings.getProperties();
        if (held.gamemode == asked && held.hardcore == hardcore) { return; }
        ContentLog.LOGGER.info("server.properties plays {} and a pack asks for {}, so the pack's mode is written to server.properties and every world plays it", held.hardcore ? "hardcore" : held.gamemode.getName(), hardcore ? "hardcore" : asked.getName());
        rdpl$write(Map.of("gamemode", asked.getName(), "hardcore", Boolean.toString(hardcore)));
        server.getWorldData().setGameType(asked);
    }

    @Unique private void rdpl$writeProperties() {
        Properties held = ((ISettings) settings.getProperties()).rdpl$cloneProperties();
        Map<String, String> asked = ContentServer.properties(held.getProperty("level-type", ""));
        List<String> written = new ArrayList<>();
        for (Map.Entry<String, String> line : asked.entrySet()) {
            if (!line.getValue().equals(held.getProperty(line.getKey()))) { written.add(line.getKey() + "=" + line.getValue()); }
        }
        if (written.isEmpty()) { return; }
        rdpl$write(asked);
        ContentLog.LOGGER.info("A pack sets {} line(s) of server.properties, so they are written there: {}", written.size(), String.join(", ", written));
    }

    @Unique private void rdpl$write(Map<String, String> lines) {
        settings.update(current -> {
            Properties changed = ((ISettings) current).rdpl$cloneProperties();
            lines.forEach(changed::setProperty);
            return new DedicatedServerProperties(changed);
        });
    }
}
