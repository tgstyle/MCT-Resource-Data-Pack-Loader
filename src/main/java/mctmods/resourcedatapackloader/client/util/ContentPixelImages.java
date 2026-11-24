package mctmods.resourcedatapackloader.client.util;

import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class ContentPixelImages {
    private ContentPixelImages() {}

    public static BufferedImage load(IResourceManager resources, ResourceLocation where) throws IOException {
        try (IResource held = resources.getResource(where)) { return TextureUtil.readBufferedImage(held.getInputStream()); }
    }

    public static BufferedImage load(ResourceLocation where) throws IOException { return load(Minecraft.getMinecraft().getResourceManager(), where); }

    @Nullable public static int[][] read(String namespace, String path) {
        try (IResource held = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(namespace, path))) {
            BufferedImage image = ImageIO.read(held.getInputStream());
            if (image == null) { return null; }
            int wide = image.getWidth();
            int tall = image.getHeight();
            int[] pixels = new int[wide * tall];
            image.getRGB(0, 0, wide, tall, pixels, 0, wide);
            return new int[][] { { wide, tall }, pixels };
        }
        catch (FileNotFoundException ex) { return null; }
        catch (IOException ex) {
            ContentLog.LOGGER.error("The image {}:{} could not be read to build on", namespace, path, ex);
            return null;
        }
    }
}
