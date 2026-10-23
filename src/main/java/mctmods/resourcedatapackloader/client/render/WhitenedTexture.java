package mctmods.resourcedatapackloader.client.render;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.ReloadableTexture;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public final class WhitenedTexture extends ReloadableTexture {
    private static final Set<Identifier> REGISTERED = new HashSet<>();

    private WhitenedTexture(Identifier source) { super(source); }

    @Override @Nonnull public TextureContents loadContents(@Nonnull ResourceManager manager) throws IOException {
        TextureContents contents = TextureContents.load(manager, resourceId());
        NativeImage image = contents.image();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) { image.setPixel(x, y, image.getPixel(x, y) | 0xFFFFFF); }
        }
        return contents;
    }

    public static Identifier bound(TextureManager manager, Identifier source) {
        Identifier id = Identifier.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "whitened/" + source.getPath());
        if (REGISTERED.add(id)) { manager.registerAndLoad(id, new WhitenedTexture(source)); }
        return id;
    }
}
