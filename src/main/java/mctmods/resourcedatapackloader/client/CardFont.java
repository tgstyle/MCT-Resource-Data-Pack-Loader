package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT) final class CardFont {
    static final String RDPL = "resourcedatapackloader:rdpl";
    static final String RUNIC = "resourcedatapackloader:rdpl_runic";
    private static final String GAME = "minecraft:default";
    private static final String OURS = "resourcedatapackloader:";
    private static final Set<String> SHORT = new HashSet<>(Arrays.asList("rdpl", "rdpl_bold", "rdpl_italic", "rdpl_runic", "rdpl_runic_bold", "rdpl_runic_italic"));
    private static final Map<String, FontRenderer> FONTS = new HashMap<>();
    private static final Set<String> MISSING = new HashSet<>();
    private static final Set<String> WARNED = new HashSet<>();
    private static boolean listening;

    private CardFont() {}

    static String named(String name) {
        if (name.isEmpty()) { return RDPL; }
        return SHORT.contains(name) ? OURS + name : name;
    }

    static FontRenderer of(String name) {
        Minecraft mc = Minecraft.getMinecraft();
        String asked = named(name);
        if (GAME.equals(asked)) { return mc.fontRenderer; }
        FontRenderer font = find(asked);
        if (font != null) { return font; }
        if (WARNED.add(asked)) { ContentLog.LOGGER.warn("Card font {} has no {} in any pack, so the game's font is used", asked, sheet(new ResourceLocation(asked))); }
        return mc.fontRenderer;
    }

    @Nullable static FontRenderer bold(String name) {
        String asked = named(name);
        return GAME.equals(asked) ? null : find(asked + "_bold");
    }

    @Nullable private static FontRenderer find(String name) {
        Minecraft mc = Minecraft.getMinecraft();
        listen(mc);
        if (MISSING.contains(name)) { return null; }
        FontRenderer font = FONTS.get(name);
        if (font != null) { return font; }
        ResourceLocation texture = sheet(new ResourceLocation(name));
        try {
            mc.getResourceManager().getResource(texture).close();
            font = new FontRenderer(mc.gameSettings, texture, mc.getTextureManager(), false);
            font.onResourceManagerReload(mc.getResourceManager());
            FONTS.put(name, font);
            return font;
        }
        catch (IOException | RuntimeException e) {
            MISSING.add(name);
            return null;
        }
    }

    private static ResourceLocation sheet(ResourceLocation id) { return new ResourceLocation(id.getNamespace(), "textures/font/" + id.getPath() + ".png"); }

    private static void listen(Minecraft mc) {
        if (listening) { return; }
        listening = true;
        IResourceManager resources = mc.getResourceManager();
        if (resources instanceof IReloadableResourceManager) {
            ((IReloadableResourceManager) resources).registerReloadListener(reloaded -> {
                FONTS.clear();
                MISSING.clear();
            });
        }
    }
}
