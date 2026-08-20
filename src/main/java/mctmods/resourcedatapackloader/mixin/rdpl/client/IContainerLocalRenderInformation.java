package mctmods.resourcedatapackloader.mixin.rdpl.client;

import net.minecraft.client.renderer.chunk.RenderChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.renderer.RenderGlobal$ContainerLocalRenderInformation") public interface IContainerLocalRenderInformation {
    @Accessor RenderChunk getRenderChunk();
}
