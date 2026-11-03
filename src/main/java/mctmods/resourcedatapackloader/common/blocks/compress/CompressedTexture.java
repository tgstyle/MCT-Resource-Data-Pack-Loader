package mctmods.resourcedatapackloader.common.blocks.compress;

import mctmods.resourcedatapackloader.common.generated.ContentPixelMaps;

import java.awt.image.BufferedImage;
import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;

public final class CompressedTexture {
    private static final int GREY_SIDE = 16;
    private static final int GREY = 0xFF7F7F7F;
    private static final float DARKEST = 0.55F;
    private static final float RING = 0.6F;
    private static final float LIGHT = 1.2F;
    private static final float FRAME_SHARE = 0.25F;

    private CompressedTexture() {}

    @Nullable static byte[] draw(CompressedSet set, int layer) {
        int[][] base = CompressedBase.image(set);
        int side = base == null ? GREY_SIDE : base[0][0];
        int[] pixels = base == null ? null : base[1];
        float factor = 1.0F - DARKEST * layer / set.layers;
        int frame = Math.max(1, Math.round(layer * side * FRAME_SHARE / set.layers));
        BufferedImage image = new BufferedImage(side, side, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < side; y++) {
            for (int x = 0; x < side; x++) {
                int color = pixels == null ? GREY : pixels[y * side + x];
                int edge = Math.min(Math.min(x, y), Math.min(side - 1 - x, side - 1 - y));
                if (edge < frame) { color = scale(color | 0xFF000000, factor * RING); }
                else if (edge == frame) { color = scale(color, factor * LIGHT); }
                else { color = scale(color, factor); }
                image.setRGB(x, y, color);
            }
        }
        return ContentPixelMaps.written(set.def.key.getNamespace(), "textures/blocks/" + set.assetName(layer) + ContentPixelMaps.PNG, image);
    }

    private static int scale(int color, float factor) {
        int red = MathHelper.clamp(Math.round(((color >> 16) & 0xFF) * factor), 0, 255);
        int green = MathHelper.clamp(Math.round(((color >> 8) & 0xFF) * factor), 0, 255);
        int blue = MathHelper.clamp(Math.round((color & 0xFF) * factor), 0, 255);
        return (color & 0xFF000000) | (red << 16) | (green << 8) | blue;
    }
}
