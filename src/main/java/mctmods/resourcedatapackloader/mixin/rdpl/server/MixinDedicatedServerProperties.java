package mctmods.resourcedatapackloader.mixin.rdpl.server;

import mctmods.resourcedatapackloader.content.ContentServer;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Mixin(DedicatedServer.class) public abstract class MixinDedicatedServerProperties {
    @Shadow @Final private DedicatedServerSettings settings;

    @Inject(method = "initServer", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/server/ServerLifecycleHooks;handleServerAboutToStart(Lnet/minecraft/server/MinecraftServer;)Z", shift = At.Shift.AFTER, remap = false)) private void rdpl$writeThePackProperties(CallbackInfoReturnable<Boolean> cir) {
        Properties held = ((ISettings) settings.getProperties()).rdpl$cloneProperties();
        Map<String, String> asked = ContentServer.properties(held.getProperty("level-type", ""));
        List<String> written = new ArrayList<>();
        for (Map.Entry<String, String> line : asked.entrySet()) {
            if (!line.getValue().equals(held.getProperty(line.getKey()))) { written.add(line.getKey() + "=" + line.getValue()); }
        }
        if (!written.isEmpty()) {
            settings.update(current -> {
                Properties changed = ((ISettings) current).rdpl$cloneProperties();
                asked.forEach(changed::setProperty);
                return new DedicatedServerProperties(changed);
            });
            ContentLog.LOGGER.info("A pack sets {} line(s) of server.properties, so they are written there: {}", written.size(), String.join(", ", written));
        }
        ContentServer.applyTo((DedicatedServer) (Object) this);
    }
}
