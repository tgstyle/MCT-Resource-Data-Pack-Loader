package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;

public final class ContentPixelImages {
    private ContentPixelImages() {}

    private static Optional<Resource> find(String namespace, String path) {
        ResourceLocation location = ResourceLocation.tryBuild(namespace, path);
        return location == null ? Optional.empty() : Minecraft.getInstance().getResourceManager().getResource(location);
    }

    public static boolean exists(String namespace, String path) { return find(namespace, path).isPresent(); }

    @Nullable public static int[][] read(String namespace, String path) {
        Optional<Resource> held = find(namespace, path);
        if (held.isEmpty()) { return null; }
        try (InputStream stream = held.get().open()) {
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
