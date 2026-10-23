package mctmods.resourcedatapackloader.client.render;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@SideOnly(Side.CLIENT) public final class WhitenedTexture extends AbstractTexture {
    private static final Set<ResourceLocation> REGISTERED = new HashSet<>();
    private final ResourceLocation source;

    private WhitenedTexture(ResourceLocation source) { this.source = source; }

    @Override public void loadTexture(IResourceManager manager) throws IOException {
        deleteGlTexture();
        IResource resource = null;
        try {
            resource = manager.getResource(source);
            BufferedImage image = TextureUtil.readBufferedImage(resource.getInputStream());
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) { image.setRGB(x, y, image.getRGB(x, y) | 0xFFFFFF); }
            }
            TextureUtil.uploadTextureImageAllocate(getGlTextureId(), image, false, false);
        }
        finally { IOUtils.closeQuietly(resource); }
    }

    public static ResourceLocation bound(TextureManager manager, ResourceLocation source) {
        ResourceLocation id = new ResourceLocation(ResourceDataPackLoader.MOD_ID, "whitened/" + source.getPath());
        if (REGISTERED.add(id)) { manager.loadTexture(id, new WhitenedTexture(source)); }
        return id;
    }
}
