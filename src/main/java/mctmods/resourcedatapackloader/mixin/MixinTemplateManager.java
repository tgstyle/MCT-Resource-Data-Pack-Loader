package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentCofhWorld;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;

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
        try (InputStream converted = ContentCofhWorld.openTemplate(id)) {
            if (converted != null) {
                readTemplateFromStream(id.getPath(), converted);
                ContentLog.LOGGER.info("Serving structure {} from a converted CoFH World file", id);
                cir.setReturnValue(Boolean.TRUE);
                return;
            }
        }
        catch (IOException ex) { ContentLog.LOGGER.error("Could not read converted structure {}", id, ex); }

        PackManager manager = PackManager.get();
        if (manager.isEmpty()) { return; }
        String path = "structures/" + id.getPath() + ".nbt";
        try (InputStream stream = manager.openRaw(id.getNamespace(), path)) {
            if (stream == null) { return; }
            readTemplateFromStream(id.getPath(), stream);
            ContentLog.LOGGER.info("Serving structure {} from pack '{}'", id, manager.getPackName(id.getNamespace(), path));
            cir.setReturnValue(Boolean.TRUE);
        }
        catch (IOException ex) {
            ContentLog.LOGGER.error("Could not read structure {} from pack, falling back to the jar", id, ex);
        }
    }
}
