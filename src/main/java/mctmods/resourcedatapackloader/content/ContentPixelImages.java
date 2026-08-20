package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;

@SideOnly(Side.CLIENT) public final class ContentPixelImages {
    private ContentPixelImages() {}

    public static boolean exists(String namespace, String path) {
        try (IResource ignored = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(namespace, path))) { return true; }
        catch (IOException ex) { return false; }
    }

    @Nullable public static int[][] read(String namespace, String path) {
        try (IResource held = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(namespace, path))) {
            InputStream stream = held.getInputStream();
            BufferedImage image = ImageIO.read(stream);
            if (image == null) { return null; }
            int wide = image.getWidth();
            int tall = image.getHeight();
            int[] pixels = new int[wide * tall];
            image.getRGB(0, 0, wide, tall, pixels, 0, wide);
            return new int[][] { { wide, tall }, pixels };
        }
        catch (IOException ex) {
            ContentLog.LOGGER.error("The image {}:{} could not be read to build on", namespace, path, ex);
            return null;
        }
    }
}
