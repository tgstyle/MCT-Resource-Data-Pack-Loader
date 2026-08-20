package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.server.RubicAnvilChunkLoader;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICubeProviderInternal;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.content.rubic.world.provider.IRubicWorldProvider;

import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.AnvilSaveHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.io.File;

@Mixin(AnvilSaveHandler.class) public class MixinAnvilSaveHandler {
    @Redirect(method = "getChunkLoader", at = @At(value = "NEW", target = "net/minecraft/world/chunk/storage/AnvilChunkLoader")) private AnvilChunkLoader getChunkLoader(File chunkSaveLocationIn, DataFixer dataFixerIn, WorldProvider provider) {
        IRubicWorld world = ((IRubicWorld) ((IRubicWorldProvider) provider).rdpl$getWorld());
        if (world.rdpl$isRubicWorld()) {
            return new RubicAnvilChunkLoader(chunkSaveLocationIn, dataFixerIn, () -> ((ICubeProviderInternal.Server) world.rdpl$getCubeCache()).getCubeIO());
        }
        else { return new AnvilChunkLoader(chunkSaveLocationIn, dataFixerIn); }
    }
}
