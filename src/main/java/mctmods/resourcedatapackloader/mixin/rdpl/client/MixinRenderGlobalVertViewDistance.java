package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.core.optifine.interfaces.IVerticalRenderDistance;
import mctmods.resourcedatapackloader.util.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderGlobal.class, priority = 1999) public abstract class MixinRenderGlobalVertViewDistance implements IVerticalRenderDistance {
    @Shadow @Final private Minecraft mc;
    @Shadow private int renderDistanceChunks;

    @Shadow public abstract void loadRenderers();

    @Shadow private WorldClient world;
    @Unique private int rdpl$verticalRenderDistanceCubes;

    @Override public int rdpl$getVerticalRenderDistanceCubes() { return rdpl$verticalRenderDistanceCubes; }

    @Inject(
            method = "loadRenderers",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/RenderGlobal;renderDistanceChunks:I",
                    opcode = Opcodes.PUTFIELD)
    )
    private void onUpdateRenderDistance(CallbackInfo cbi) { this.rdpl$verticalRenderDistanceCubes = Config.client.verticalCubeLoadDistance; }

    @Inject(method = "setupTerrain", at = @At("HEAD")) private void onSetupTerrain(CallbackInfo cbi) {
        if (!((IRubicWorld) world).rdpl$isRubicWorld()) { return; }
        if (mc.gameSettings.renderDistanceChunks == renderDistanceChunks
                && Config.client.verticalCubeLoadDistance != rdpl$verticalRenderDistanceCubes) { this.loadRenderers(); }
    }

    @Redirect(
            method = "setupTerrain",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/RenderGlobal;renderDistanceChunks:I",
                    opcode = Opcodes.GETFIELD),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;loadRenderers()V")
            )
    )
    private int onGetRenderDistance(RenderGlobal _this) {
        if (!((IRubicWorld) world).rdpl$isRubicWorld()) { return mc.gameSettings.renderDistanceChunks; }
        return Math.max(mc.gameSettings.renderDistanceChunks, Config.client.verticalCubeLoadDistance);
    }
}
