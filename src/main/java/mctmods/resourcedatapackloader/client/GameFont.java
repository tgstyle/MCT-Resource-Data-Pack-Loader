package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT) public final class GameFont {
    public static final int CELLS = 256;
    private static final int CYRILLIC = 0x400;
    private static final int RUNES = 0x1600;
    private static final ResourceLocation ASCII = new ResourceLocation("textures/font/ascii.png");
    private static final ResourceLocation RDPL = new ResourceLocation(ResourceDataPackLoader.MOD_ID, "textures/font/rdpl.png");
    private static final String CIPHER = "textures/font/rdpl_runic";
    private static final String RDPL_PATH = "textures/font/rdpl";
    private static final List<String> VANILLA = Arrays.asList("Default", "FMLFileResourcePack:Minecraft Forge");
    private static final String PNG = ".png";

    private GameFont() {}

    public static ResourceLocation sheet(@Nullable IResourceManager resources, ResourceLocation asked) {
        if (resources == null || !ASCII.equals(asked)) { return asked; }
        String from;
        try (IResource vanilla = resources.getResource(ASCII)) { from = vanilla.getResourcePackName(); }
        catch (IOException e) { return asked; }
        if (!VANILLA.contains(from)) {
            ContentLog.LOGGER.info("The game's font is {} from {}, so the RDPL font stands aside", ASCII, from);
            return asked;
        }
        try (IResource ours = resources.getResource(RDPL)) {
            ContentLog.LOGGER.info("The game's font is {} from {}", RDPL, ours.getResourcePackName());
            return RDPL;
        }
        catch (IOException e) { return asked; }
    }

    public static ResourceLocation base(ResourceLocation sheet) {
        String path = sheet.getPath();
        if (!ResourceDataPackLoader.MOD_ID.equals(sheet.getNamespace()) || !path.startsWith(CIPHER)) { return sheet; }
        return new ResourceLocation(sheet.getNamespace(), RDPL_PATH + path.substring(CIPHER.length()));
    }

    public static Page[] pages(@Nullable IResourceManager resources, ResourceLocation base) { return new Page[] {page(resources, base, CYRILLIC, "_cyrillic"), page(resources, base, RUNES, "_runes")}; }

    private static Page page(@Nullable IResourceManager resources, ResourceLocation base, int first, String suffix) { return new Page(first, face(resources, base, suffix), styled(resources, base, "_bold" + suffix), styled(resources, base, "_italic" + suffix)); }

    @Nullable public static Face styled(@Nullable IResourceManager resources, ResourceLocation sheet, String suffix) { return ASCII.equals(sheet) ? null : face(resources, sheet, suffix); }

    @Nullable public static Face face(@Nullable IResourceManager resources, ResourceLocation sheet, String suffix) {
        String path = sheet.getPath();
        if (resources == null || !path.endsWith(PNG)) { return null; }
        ResourceLocation where = new ResourceLocation(sheet.getNamespace(), path.substring(0, path.length() - PNG.length()) + suffix + PNG);
        try (IResource resource = resources.getResource(where)) { return new Face(where, measure(TextureUtil.readBufferedImage(resource.getInputStream()))); }
        catch (IOException | RuntimeException e) { return null; }
    }

    private static int[] measure(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);
        int cellWidth = width / 16;
        int cellHeight = height / 16;
        float scale = 8.0F / cellWidth;
        int[] widths = new int[CELLS];
        for (int cell = 0; cell < CELLS; cell++) {
            int used = used(pixels, width, cell % 16 * cellWidth, cell / 16 * cellHeight, cellWidth, cellHeight);
            widths[cell] = used == 0 ? 0 : (int) (0.5F + used * scale) + 1;
        }
        return widths;
    }

    private static int used(int[] pixels, int width, int left, int top, int cellWidth, int cellHeight) {
        for (int x = cellWidth - 1; x >= 0; x--) {
            for (int y = 0; y < cellHeight; y++) {
                if ((pixels[(top + y) * width + left + x] >> 24 & 0xFF) != 0) { return x + 1; }
            }
        }
        return 0;
    }

    public static final class Face {
        public final ResourceLocation sheet;
        private final int[] widths;

        private Face(ResourceLocation sheet, int[] widths) {
            this.sheet = sheet;
            this.widths = widths;
        }

        public int width(int cell) { return cell >= 0 && cell < widths.length ? widths[cell] : 0; }
    }

    public static final class Page {
        public final int first;
        @Nullable public final Face plain;
        @Nullable public final Face bold;
        @Nullable public final Face italic;

        private Page(int first, @Nullable Face plain, @Nullable Face bold, @Nullable Face italic) {
            this.first = first;
            this.plain = plain;
            this.bold = bold;
            this.italic = italic;
        }

        public int width(int ch) { return plain == null ? 0 : plain.width(ch - first); }
    }
}
