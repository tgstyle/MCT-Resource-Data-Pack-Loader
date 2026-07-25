package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.core.MCTMixin;
import mctmods.resourcedatapackloader.pack.PackManager;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.template.TemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.io.InputStream;

@Mixin(TemplateManager.class)
public abstract class MixinTemplateManager {
    @Shadow protected abstract void readTemplateFromStream(String id, InputStream stream) throws IOException;

    @Inject(method = "readTemplateFromJar", at = @At("HEAD"), cancellable = true)
    private void rdpl$serveFromPack(ResourceLocation id, CallbackInfoReturnable<Boolean> cir) {
        PackManager manager = PackManager.get();
        if (manager.isEmpty()) { return; }
        String path = "structures/" + id.getPath() + ".nbt";
        try (InputStream stream = manager.openRaw(id.getNamespace(), path)) {
            if (stream == null) { return; }
            readTemplateFromStream(id.getPath(), stream);
            MCTMixin.LOGGER.info("Serving structure {} from pack '{}'", id, manager.getPackName(id.getNamespace(), path));
            cir.setReturnValue(Boolean.TRUE);
        }
        catch (IOException ex) {
            MCTMixin.LOGGER.error("Could not read structure {} from pack, falling back to the jar", id, ex);
        }
    }
}
