package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.core.optifine.interfaces.IOptifineRenderChunk;

import net.minecraft.client.renderer.ChunkRenderContainer;
import net.minecraft.client.renderer.RenderList;
import net.minecraft.client.renderer.VboRenderList;
import net.minecraft.client.renderer.chunk.RenderChunk;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("target") @Mixin({RenderList.class, VboRenderList.class}) public abstract class MixinRenderList extends ChunkRenderContainer {
    @Dynamic @Shadow(remap = false) private double viewEntityY;

    @Unique private int renderChunkLayer_regionY = Integer.MIN_VALUE;

    @Dynamic @ModifyConstant(method = "renderChunkLayer", constant = @Constant(intValue = Integer.MIN_VALUE, ordinal = 0)) private int initRegionX(int orig) {
        renderChunkLayer_regionY = orig;
        return orig;
    }

    @Dynamic @Redirect(method = "renderChunkLayer",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/chunk/RenderChunk;regionX:I",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 0
            ), remap = false)
    private int getHackedRegionX(RenderChunk rc) {
        int regionY = renderChunkLayer_regionY;
        int rcRegY = ((IOptifineRenderChunk) rc).getRegionY();
        if (regionY != rcRegY) { return 1; }
        return ((IOptifineRenderChunk) rc).getRegionX();
    }

    @Group(name = "preRenderRegion", min = 1, max = 2) @Dynamic @ModifyArg(method = "preRenderRegion",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V"),
            index = 1,
            remap = false,
            require = 0
    )
    private float drawRegionRedirect_deobf(float zero) { return (float) (renderChunkLayer_regionY - this.viewEntityY); }

    @Group(name = "preRenderRegion", min = 1, max = 2) @Dynamic @ModifyArg(method = "preRenderRegion",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;func_179109_b(FFF)V"),
            index = 1,
            remap = false,
            require = 0
    )
    private float drawRegionRedirect_obf(float zero) { return (float) (renderChunkLayer_regionY - this.viewEntityY); }

    @Dynamic @Redirect(method = "renderChunkLayer",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/chunk/RenderChunk;regionX:I",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 1
            ), remap = false)
    private int updateRegionY(RenderChunk rc) {
        renderChunkLayer_regionY = ((IOptifineRenderChunk) rc).getRegionY();
        return ((IOptifineRenderChunk) rc).getRegionX();
    }
}
