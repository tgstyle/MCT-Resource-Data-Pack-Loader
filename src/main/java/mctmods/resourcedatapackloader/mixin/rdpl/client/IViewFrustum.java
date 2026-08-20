package mctmods.resourcedatapackloader.mixin.rdpl.client;

import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ViewFrustum.class) public interface IViewFrustum { @Invoker("getRenderChunk") RenderChunk getRenderChunkAt(BlockPos pos); }
