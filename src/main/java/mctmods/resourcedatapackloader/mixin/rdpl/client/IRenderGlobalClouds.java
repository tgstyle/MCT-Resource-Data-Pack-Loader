package mctmods.resourcedatapackloader.mixin.rdpl.client;

import net.minecraft.client.renderer.RenderGlobal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderGlobal.class) public interface IRenderGlobalClouds {
    @Accessor int getCloudTickCounter();

    @Accessor void setCloudTickCounter(int ticks);
}
