package mctmods.resourcedatapackloader.mixin.dungeons2;

import mctmods.resourcedatapackloader.util.ContentLog;

import com.someguyssoftware.dungeons2.style.StyleSheetLoader;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mixin(value = StyleSheetLoader.class, remap = false)
public abstract class MixinStyleSheetLoader {
    @Inject(method = "exposeStyleSheet", at = @At("HEAD"), remap = false)
    private static void rdpl$makeFolder(String filePath, CallbackInfo ci) {
        Path parent = Paths.get(filePath).toAbsolutePath().getParent();
        if (parent == null || Files.exists(parent)) { return; }

        try {
            Files.createDirectories(parent);
            ContentLog.LOGGER.info("Created {} for Dungeons2, which does not create it itself and would crash world generation without it", parent);
        }
        catch (IOException ex) { ContentLog.LOGGER.error("Could not create {} for Dungeons2, so its world generation may still crash: {}", parent, ex.toString()); }
    }
}