package mctmods.resourcedatapackloader.mixin.rdpl.server;

import mctmods.resourcedatapackloader.content.ContentServer;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.PropertyManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(DedicatedServer.class) public abstract class MixinDedicatedServerProperties {
    @Shadow private PropertyManager settings;

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/FMLCommonHandler;onServerStarted()V", shift = At.Shift.AFTER, remap = false)) private void rdpl$writeThePackProperties(CallbackInfoReturnable<Boolean> cir) {
        List<String> written = new ArrayList<>();
        for (Map.Entry<String, String> asked : ContentServer.properties(settings.getStringProperty("level-type", "DEFAULT")).entrySet()) {
            if (settings.hasProperty(asked.getKey()) && asked.getValue().equals(settings.getStringProperty(asked.getKey(), asked.getValue()))) { continue; }
            settings.setProperty(asked.getKey(), asked.getValue());
            written.add(asked.getKey() + "=" + asked.getValue());
        }
        if (!written.isEmpty()) {
            settings.saveProperties();
            ContentLog.LOGGER.info("A pack sets {} line(s) of server.properties, so they are written there: {}", written.size(), String.join(", ", written));
        }
        ContentServer.applyTo((DedicatedServer) (Object) this);
    }
}
