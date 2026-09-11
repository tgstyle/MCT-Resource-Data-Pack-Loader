package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.SplashDark;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.io.InputStream;

@Mixin(targets = "net.minecraft.client.gui.screens.LoadingOverlay$LogoTexture") public abstract class MixinLogoTexture {
    @Redirect(method = "getTextureImage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/VanillaPackResources;getResource(Lnet/minecraft/server/packs/PackType;Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/server/packs/resources/IoSupplier;"))
    private IoSupplier<InputStream> rdpl$packLogo(VanillaPackResources vanilla, PackType type, ResourceLocation location) {
        IoSupplier<InputStream> logo = SplashDark.logo();
        return logo != null ? logo : vanilla.getResource(type, location);
    }
}
