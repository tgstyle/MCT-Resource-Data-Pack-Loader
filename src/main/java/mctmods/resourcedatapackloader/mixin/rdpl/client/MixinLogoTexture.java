package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.SplashDark;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.io.IOException;
import java.io.InputStream;

@Mixin(targets = "net.minecraft.client.gui.screens.LoadingOverlay$LogoTexture") public abstract class MixinLogoTexture {
    @Redirect(method = "loadContents(Lnet/minecraft/server/packs/resources/ResourceManager;)Lnet/minecraft/client/renderer/texture/TextureContents;", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/ResourceProvider;open(Lnet/minecraft/resources/Identifier;)Ljava/io/InputStream;"))
    private InputStream rdpl$packLogo(ResourceProvider vanilla, Identifier location) throws IOException {
        IoSupplier<InputStream> logo = SplashDark.logo();
        return logo != null ? logo.get() : vanilla.open(location);
    }
}
