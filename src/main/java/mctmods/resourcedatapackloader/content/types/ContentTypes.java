package mctmods.resourcedatapackloader.content.types;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumRarity;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class ContentTypes {
    public static final int NO_COLOR = -1;
    private static final Map<String, Material> MATERIALS = new HashMap<>();
    private static final Map<String, MapColor> MAP_COLORS = new HashMap<>();
    private static final Map<String, SoundType> SOUND_TYPES = new HashMap<>();

    private ContentTypes() {}

    static {
        MATERIALS.put("air", Material.AIR);
        MATERIALS.put("grass", Material.GRASS);
        MATERIALS.put("ground", Material.GROUND);
        MATERIALS.put("wood", Material.WOOD);
        MATERIALS.put("rock", Material.ROCK);
        MATERIALS.put("iron", Material.IRON);
        MATERIALS.put("anvil", Material.ANVIL);
        MATERIALS.put("water", Material.WATER);
        MATERIALS.put("lava", Material.LAVA);
        MATERIALS.put("leaves", Material.LEAVES);
        MATERIALS.put("plants", Material.PLANTS);
        MATERIALS.put("vine", Material.VINE);
        MATERIALS.put("sponge", Material.SPONGE);
        MATERIALS.put("cloth", Material.CLOTH);
        MATERIALS.put("fire", Material.FIRE);
        MATERIALS.put("sand", Material.SAND);
        MATERIALS.put("circuits", Material.CIRCUITS);
        MATERIALS.put("carpet", Material.CARPET);
        MATERIALS.put("glass", Material.GLASS);
        MATERIALS.put("redstone_light", Material.REDSTONE_LIGHT);
        MATERIALS.put("tnt", Material.TNT);
        MATERIALS.put("coral", Material.CORAL);
        MATERIALS.put("ice", Material.ICE);
        MATERIALS.put("packed_ice", Material.PACKED_ICE);
        MATERIALS.put("snow", Material.SNOW);
        MATERIALS.put("crafted_snow", Material.CRAFTED_SNOW);
        MATERIALS.put("cactus", Material.CACTUS);
        MATERIALS.put("clay", Material.CLAY);
        MATERIALS.put("gourd", Material.GOURD);
        MATERIALS.put("dragon_egg", Material.DRAGON_EGG);
        MATERIALS.put("portal", Material.PORTAL);
        MATERIALS.put("cake", Material.CAKE);
        MATERIALS.put("web", Material.WEB);
        MATERIALS.put("piston", Material.PISTON);
        MATERIALS.put("barrier", Material.BARRIER);
        MATERIALS.put("structure_void", Material.STRUCTURE_VOID);

        MAP_COLORS.put("air", MapColor.AIR);
        MAP_COLORS.put("grass", MapColor.GRASS);
        MAP_COLORS.put("sand", MapColor.SAND);
        MAP_COLORS.put("cloth", MapColor.CLOTH);
        MAP_COLORS.put("tnt", MapColor.TNT);
        MAP_COLORS.put("ice", MapColor.ICE);
        MAP_COLORS.put("iron", MapColor.IRON);
        MAP_COLORS.put("foliage", MapColor.FOLIAGE);
        MAP_COLORS.put("snow", MapColor.SNOW);
        MAP_COLORS.put("clay", MapColor.CLAY);
        MAP_COLORS.put("dirt", MapColor.DIRT);
        MAP_COLORS.put("stone", MapColor.STONE);
        MAP_COLORS.put("water", MapColor.WATER);
        MAP_COLORS.put("wood", MapColor.WOOD);
        MAP_COLORS.put("quartz", MapColor.QUARTZ);
        MAP_COLORS.put("adobe", MapColor.ADOBE);
        MAP_COLORS.put("magenta", MapColor.MAGENTA);
        MAP_COLORS.put("light_blue", MapColor.LIGHT_BLUE);
        MAP_COLORS.put("yellow", MapColor.YELLOW);
        MAP_COLORS.put("lime", MapColor.LIME);
        MAP_COLORS.put("pink", MapColor.PINK);
        MAP_COLORS.put("gray", MapColor.GRAY);
        MAP_COLORS.put("silver", MapColor.SILVER);
        MAP_COLORS.put("cyan", MapColor.CYAN);
        MAP_COLORS.put("purple", MapColor.PURPLE);
        MAP_COLORS.put("blue", MapColor.BLUE);
        MAP_COLORS.put("brown", MapColor.BROWN);
        MAP_COLORS.put("green", MapColor.GREEN);
        MAP_COLORS.put("red", MapColor.RED);
        MAP_COLORS.put("black", MapColor.BLACK);
        MAP_COLORS.put("gold", MapColor.GOLD);
        MAP_COLORS.put("diamond", MapColor.DIAMOND);
        MAP_COLORS.put("lapis", MapColor.LAPIS);
        MAP_COLORS.put("emerald", MapColor.EMERALD);
        MAP_COLORS.put("obsidian", MapColor.OBSIDIAN);
        MAP_COLORS.put("netherrack", MapColor.NETHERRACK);

        SOUND_TYPES.put("wood", SoundType.WOOD);
        SOUND_TYPES.put("ground", SoundType.GROUND);
        SOUND_TYPES.put("plant", SoundType.PLANT);
        SOUND_TYPES.put("stone", SoundType.STONE);
        SOUND_TYPES.put("metal", SoundType.METAL);
        SOUND_TYPES.put("glass", SoundType.GLASS);
        SOUND_TYPES.put("cloth", SoundType.CLOTH);
        SOUND_TYPES.put("sand", SoundType.SAND);
        SOUND_TYPES.put("snow", SoundType.SNOW);
        SOUND_TYPES.put("ladder", SoundType.LADDER);
        SOUND_TYPES.put("anvil", SoundType.ANVIL);
        SOUND_TYPES.put("slime", SoundType.SLIME);
    }

    public static double red(int color) { return (color >> 16 & 0xFF) / 255.0; }

    public static double green(int color) { return (color >> 8 & 0xFF) / 255.0; }

    public static double blue(int color) { return (color & 0xFF) / 255.0; }

    public static Material material(String name, String context) {
        Material material = MATERIALS.get(key(name));
        if (material != null) { return material; }
        ContentLog.LOGGER.error("Unknown material '{}' in {}, using rock", name, context);
        return Material.ROCK;
    }

    public static MapColor mapColor(String name, MapColor fallback, String context) {
        if (name == null || name.isEmpty()) { return fallback; }
        MapColor color = MAP_COLORS.get(key(name));
        if (color != null) { return color; }
        ContentLog.LOGGER.error("Unknown mapColor '{}' in {}, using the material default", name, context);
        return fallback;
    }

    public static SoundType soundType(String name, SoundType fallback, String context) {
        if (name == null || name.isEmpty()) { return fallback; }
        SoundType sound = SOUND_TYPES.get(key(name));
        if (sound != null) { return sound; }
        ContentLog.LOGGER.error("Unknown soundType '{}' in {}, keeping the default", name, context);
        return fallback;
    }

    public static int color(String value, String context) {
        if (value == null || value.isEmpty()) { return 0xFFFFFFFF; }

        String cleaned = value.startsWith("#") ? value.substring(1) : value;
        if (cleaned.startsWith("0x") || cleaned.startsWith("0X")) { cleaned = cleaned.substring(2); }

        try { return (int) Long.parseLong(cleaned, 16); }
        catch (NumberFormatException ex) {
            ContentLog.LOGGER.error("Colour '{}' in {} is not hexadecimal, using white", value, context);
            return 0xFFFFFFFF;
        }
    }

    public static EnumRarity rarity(String name, String context) {
        if (name == null || name.isEmpty()) { return EnumRarity.COMMON; }
        try { return EnumRarity.valueOf(name.toUpperCase(Locale.ROOT)); }
        catch (IllegalArgumentException ex) {
            ContentLog.LOGGER.error("Unknown rarity '{}' in {}, using COMMON", name, context);
            return EnumRarity.COMMON;
        }
    }

    private static String key(String name) { return name == null ? "" : name.toLowerCase(Locale.ROOT); }
}
