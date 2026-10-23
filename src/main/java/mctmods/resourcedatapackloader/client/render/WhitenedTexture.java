package mctmods.resourcedatapackloader.client.render;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import javax.annotation.Nonnull;
import java.io.IOException;

public final class WhitenedTexture extends SimpleTexture {
    private WhitenedTexture(ResourceLocation source) { super(source); }

    @Override @Nonnull protected TextureImage getTextureImage(@Nonnull ResourceManager manager) {
        TextureImage loaded = super.getTextureImage(manager);
        try { loaded.getImage().applyToAllPixels(pixel -> pixel | 0xFFFFFF); }
        catch (IOException ignored) { }
        return loaded;
    }

    public static ResourceLocation bound(TextureManager manager, ResourceLocation source) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "whitened/" + source.getPath());
        AbstractTexture none = MissingTextureAtlasSprite.getTexture();
        if (manager.getTexture(id, none) == none) { manager.register(id, new WhitenedTexture(source)); }
        return id;
    }
}
