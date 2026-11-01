package mctmods.resourcedatapackloader.mixin.client.gui.screens;

import mctmods.resourcedatapackloader.client.util.SplashDark;

import java.io.InputStream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.client.gui.screens.LoadingOverlay$LogoTexture") public abstract class LogoTextureMixin {
    @Redirect(method = "getTextureImage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/VanillaPackResources;getResource(Lnet/minecraft/server/packs/PackType;Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/server/packs/resources/IoSupplier;")) private IoSupplier<InputStream> rdpl$packLogo(VanillaPackResources vanilla, PackType packType, ResourceLocation location) {
        IoSupplier<InputStream> logo = SplashDark.logo();
        return logo != null ? logo : vanilla.getResource(packType, location);
    }
}
