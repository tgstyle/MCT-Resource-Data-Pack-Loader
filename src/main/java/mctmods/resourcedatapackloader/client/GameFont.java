package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.client.gui.font.providers.GlyphProviderDefinition;
import net.minecraft.client.gui.font.providers.ProviderReferenceDefinition;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class GameFont {
    private static final ResourceLocation ASCII = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/font/ascii.png");
    private static final ResourceLocation RDPL = ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "rdpl");
    private static final ResourceLocation ITALIC = ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "rdpl_italic");
    private static final ResourceLocation BOLD = ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "rdpl_bold");
    private static final String VANILLA = "vanilla";
    private static final Map<ResourceLocation, ResourceLocation> SLANTS = new ConcurrentHashMap<>();
    private static volatile boolean yields;

    private GameFont() {}

    @Nullable public static ResourceLocation styled(Style style) {
        ResourceLocation font = style.getFont();
        if (!Style.DEFAULT_FONT.equals(font)) { return style.isItalic() && !style.isBold() ? SLANTS.get(font) : null; }
        if (yields) { return null; }
        if (style.isBold()) { return BOLD; }
        return style.isItalic() ? ITALIC : null;
    }

    public static void slants(ResourceLocation plain, ResourceLocation italic) { SLANTS.put(plain, italic); }

    public static void pick(ResourceManager resources) {
        SLANTS.clear();
        Optional<Resource> sheet = resources.getResource(ASCII);
        String from = sheet.map(Resource::sourcePackId).orElse(VANILLA);
        yields = !VANILLA.equals(from);
        if (yields) { ContentLog.LOGGER.info("The game's font is {} from {}, so the RDPL font stands aside", ASCII, from); }
        else { ContentLog.LOGGER.info("The game's font is {} from {}", RDPL, ResourceDataPackLoader.MOD_ID); }
    }

    public static List<GlyphProviderDefinition> providers(List<GlyphProviderDefinition> providers) {
        if (!yields) { return providers; }
        List<GlyphProviderDefinition> kept = new ArrayList<>(providers.size());
        for (GlyphProviderDefinition provider : providers) {
            if (!(provider instanceof ProviderReferenceDefinition reference && RDPL.equals(reference.id()))) { kept.add(provider); }
        }
        return kept;
    }
}
