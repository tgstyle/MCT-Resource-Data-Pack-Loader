package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.util.ContentLog;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;

final class ContentBabyArmor {
    private static final int ADULT_SIDE = 64;
    private static final int BABY_SIDE = 64;
    private static final Box HEAD = new Box(0, 0, 8, 8, 8);
    private static final Box HAT = new Box(32, 0, 8, 8, 8);
    private static final Box BODY = new Box(16, 16, 8, 12, 4);
    private static final Box ARM = new Box(40, 16, 4, 12, 4);
    private static final Box LEG = new Box(0, 16, 4, 12, 4);
    private static final Box BABY_HEAD = new Box(0, 0, 9, 8, 8);
    private static final Box BABY_BODY = new Box(0, 17, 6, 5, 3);
    private static final Box BABY_RIGHT_ARM = new Box(30, 25, 2, 5, 3);
    private static final Box BABY_LEFT_ARM = new Box(30, 17, 2, 5, 3);
    private static final Box BABY_RIGHT_FOOT = new Box(0, 25, 3, 1, 3);
    private static final Box BABY_LEFT_FOOT = new Box(0, 29, 3, 1, 3);
    private static final Box BABY_WAIST = new Box(0, 36, 6, 2, 3);
    private static final Box BABY_RIGHT_LEG = new Box(18, 17, 3, 4, 3);
    private static final Box BABY_LEFT_LEG = new Box(18, 24, 3, 4, 3);
    private static final double BOOT_TOP = 0.75;
    private static final double BELT_TOP = 7.0 / 12.0;
    private ContentBabyArmor() {}

    @Nullable static byte[] draw(String asset, @Nullable byte[] outer, @Nullable byte[] inner) {
        BufferedImage worn = image(asset, outer);
        BufferedImage legs = image(asset, inner);
        BufferedImage sized = worn != null ? worn : legs;
        if (sized == null) { return null; }
        int scale = Math.max(1, Math.round(sized.getWidth() / (float) ADULT_SIDE));
        BufferedImage out = new BufferedImage(BABY_SIDE * scale, BABY_SIDE * scale, BufferedImage.TYPE_INT_ARGB);
        if (worn != null) {
            copy(worn, HEAD, out, BABY_HEAD, false, 0.0, false);
            copy(worn, HAT, out, BABY_HEAD, false, 0.0, true);
            copy(worn, BODY, out, BABY_BODY, false, 0.0, false);
            copy(worn, ARM, out, BABY_RIGHT_ARM, false, 0.0, false);
            copy(worn, ARM, out, BABY_LEFT_ARM, true, 0.0, false);
            copy(worn, LEG, out, BABY_RIGHT_FOOT, false, BOOT_TOP, false);
            copy(worn, LEG, out, BABY_LEFT_FOOT, false, BOOT_TOP, false);
        }
        if (legs != null) {
            copy(legs, BODY, out, BABY_WAIST, false, BELT_TOP, false);
            copy(legs, LEG, out, BABY_RIGHT_LEG, false, 0.0, false);
            copy(legs, LEG, out, BABY_LEFT_LEG, true, 0.0, false);
        }
        try {
            ByteArrayOutputStream written = new ByteArrayOutputStream();
            ImageIO.write(out, "png", written);
            return written.toByteArray();
        }
        catch (IOException ex) {
            ContentLog.LOGGER.error("The baby armor texture for {} could not be written out", asset, ex);
            return null;
        }
    }

    @Nullable private static BufferedImage image(String asset, @Nullable byte[] bytes) {
        if (bytes == null) { return null; }
        try { return ImageIO.read(new ByteArrayInputStream(bytes)); }
        catch (IOException ex) {
            ContentLog.LOGGER.error("An armor texture of {} could not be read to draw its baby texture", asset, ex);
            return null;
        }
    }

    private static void copy(BufferedImage from, Box source, BufferedImage to, Box target, boolean mirror, double top, boolean over) {
        double fromScale = from.getWidth() / (double) ADULT_SIDE;
        double toScale = to.getWidth() / (double) BABY_SIDE;
        double[][] sourceFaces = source.faces(top);
        double[][] targetFaces = target.faces(0.0);
        for (int face = 0; face < sourceFaces.length; face++) {
            double[] read = sourceFaces[mirror ? Box.MIRRORED[face] : face];
            double[] write = targetFaces[face];
            int x0 = (int) Math.round(write[0] * toScale);
            int y0 = (int) Math.round(write[1] * toScale);
            int wide = (int) Math.round(write[2] * toScale);
            int tall = (int) Math.round(write[3] * toScale);
            for (int y = 0; y < tall; y++) {
                for (int x = 0; x < wide; x++) {
                    double across = (x + 0.5) / wide;
                    double down = (y + 0.5) / tall;
                    int sx = (int) Math.floor((read[0] + (mirror ? 1.0 - across : across) * read[2]) * fromScale);
                    int sy = (int) Math.floor((read[1] + down * read[3]) * fromScale);
                    if (sx < 0 || sy < 0 || sx >= from.getWidth() || sy >= from.getHeight()) { continue; }
                    int pixel = from.getRGB(sx, sy);
                    if (over && pixel >>> 24 == 0) { continue; }
                    int tx = x0 + x;
                    int ty = y0 + y;
                    if (tx < to.getWidth() && ty < to.getHeight()) { to.setRGB(tx, ty, pixel); }
                }
            }
        }
    }

    private record Box(int u, int v, int wide, int tall, int deep) {
        private static final int[] MIRRORED = {0, 1, 4, 3, 2, 5};

        private double[][] faces(double top) {
            double sideTop = v + deep + tall * top;
            double sideTall = tall * (1.0 - top);
            return new double[][] {
                    {u + deep, v, wide, deep},
                    {u + deep + wide, v, wide, deep},
                    {u, sideTop, deep, sideTall},
                    {u + deep, sideTop, wide, sideTall},
                    {u + deep + wide, sideTop, deep, sideTall},
                    {u + deep + deep + wide, sideTop, wide, sideTall}
            };
        }
    }
}
