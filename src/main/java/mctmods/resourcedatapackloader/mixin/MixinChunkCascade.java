package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.HashSet;
import java.util.Set;

@Mixin(Chunk.class)
public abstract class MixinChunkCascade {
    @Unique private static final Set<String> rdpl$told = new HashSet<>();

    @Inject(method = "logCascadingWorldGeneration", at = @At("HEAD"), remap = false)
    private void rdpl$whoAsked(CallbackInfo ci) {
        if (!ContentLog.LOGGER.debugEnabled() || rdpl$told.size() >= 12) { return; }

        Throwable trace = new Throwable("who reached for land that was not there");
        StringBuilder key = new StringBuilder();
        int named = 0;
        for (StackTraceElement frame : trace.getStackTrace()) {
            String owner = frame.getClassName();
            if (owner.startsWith("net.minecraft.") || owner.startsWith("java.") || owner.startsWith("mctmods.")) { continue; }

            key.append(owner).append('.').append(frame.getMethodName()).append(' ');
            if (++named >= 4) { break; }
        }
        if (named == 0) { key.append("nothing outside the game itself"); }
        if (!rdpl$told.add(key.toString())) { return; }

        ContentLog.LOGGER.debug("Land was made in the middle of making other land, by a caller not seen before. This is number {} of the different ones", rdpl$told.size(), trace);
    }
}
