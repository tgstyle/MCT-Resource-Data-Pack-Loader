package mctmods.resourcedatapackloader.mixin.world.chunk.storage;

import mctmods.resourcedatapackloader.common.rubic.server.RubicAnvilChunkLoader;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.ICubeProviderInternal;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IRubicWorldProvider;

import java.io.File;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.AnvilSaveHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilSaveHandler.class) public class MixinAnvilSaveHandler {
    @Redirect(method = "getChunkLoader", at = @At(value = "NEW", target = "net/minecraft/world/chunk/storage/AnvilChunkLoader")) private AnvilChunkLoader rdpl$getChunkLoader(File chunkSaveLocationIn, DataFixer dataFixerIn, WorldProvider provider) {
        IRubicWorld world = ((IRubicWorld) ((IRubicWorldProvider) provider).rdpl$getWorld());
        if (world.rdpl$isRubicWorld()) { return new RubicAnvilChunkLoader(chunkSaveLocationIn, dataFixerIn, () -> ((ICubeProviderInternal.IServer) world.rdpl$getCubeCache()).getCubeIO()); }
        else { return new AnvilChunkLoader(chunkSaveLocationIn, dataFixerIn); }
    }
}
