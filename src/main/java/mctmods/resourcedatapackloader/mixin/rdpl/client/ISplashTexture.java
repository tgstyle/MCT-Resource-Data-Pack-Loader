package mctmods.resourcedatapackloader.mixin.rdpl.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@SuppressWarnings("unresolvable-target") @Mixin(targets = "net.minecraftforge.fml.client.SplashProgress$Texture", remap = false) public interface ISplashTexture {
    @Invoker(value = "getWidth", remap = false) int rdpl$width();
    @Invoker(value = "getHeight", remap = false) int rdpl$height();
}
