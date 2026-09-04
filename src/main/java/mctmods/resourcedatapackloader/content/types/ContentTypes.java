package mctmods.resourcedatapackloader.content.types;

import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class ContentTypes {
    private static final float UNBREAKABLE = 3600000.0F;
    private static final Map<String, Preset> MATERIALS = new HashMap<>();
    private static final Map<String, MapColor> MAP_COLORS = new HashMap<>();
    private static final Map<String, SoundType> SOUND_TYPES = new HashMap<>();

    private ContentTypes() {}

    static {
        material("air", MapColor.NONE, SoundType.EMPTY, false);
        material("grass", MapColor.GRASS, SoundType.GRASS, true);
        material("ground", MapColor.DIRT, SoundType.GRAVEL, true);
        material("wood", MapColor.WOOD, SoundType.WOOD, true);
        material("rock", MapColor.STONE, SoundType.STONE, true);
        material("iron", MapColor.METAL, SoundType.METAL, true);
        material("anvil", MapColor.METAL, SoundType.ANVIL, true);
        material("water", MapColor.WATER, SoundType.EMPTY, false);
        material("lava", MapColor.FIRE, SoundType.EMPTY, false);
        material("leaves", MapColor.PLANT, SoundType.GRASS, true);
        material("plants", MapColor.PLANT, SoundType.GRASS, false);
        material("vine", MapColor.PLANT, SoundType.VINE, false);
        material("sponge", MapColor.COLOR_YELLOW, SoundType.GRASS, true);
        material("cloth", MapColor.WOOL, SoundType.WOOL, true);
        material("fire", MapColor.NONE, SoundType.WOOL, false);
        material("sand", MapColor.SAND, SoundType.SAND, true);
        material("circuits", MapColor.NONE, SoundType.STONE, false);
        material("carpet", MapColor.WOOL, SoundType.WOOL, true);
        material("glass", MapColor.NONE, SoundType.GLASS, true);
        material("redstone_light", MapColor.NONE, SoundType.GLASS, true);
        material("tnt", MapColor.FIRE, SoundType.GRASS, true);
        material("coral", MapColor.PLANT, SoundType.CORAL_BLOCK, true);
        material("ice", MapColor.ICE, SoundType.GLASS, true);
        material("packed_ice", MapColor.ICE, SoundType.GLASS, true);
        material("snow", MapColor.SNOW, SoundType.SNOW, true);
        material("crafted_snow", MapColor.SNOW, SoundType.SNOW, true);
        material("cactus", MapColor.PLANT, SoundType.WOOL, true);
        material("clay", MapColor.CLAY, SoundType.GRAVEL, true);
        material("gourd", MapColor.PLANT, SoundType.WOOD, true);
        material("dragon_egg", MapColor.PLANT, SoundType.STONE, true);
        material("portal", MapColor.NONE, SoundType.GLASS, false);
        material("cake", MapColor.NONE, SoundType.WOOL, true);
        material("web", MapColor.WOOL, SoundType.WOOL, false);
        material("piston", MapColor.STONE, SoundType.STONE, true);
        material("barrier", MapColor.NONE, SoundType.STONE, true);
        material("structure_void", MapColor.NONE, SoundType.STONE, false);
        MAP_COLORS.put("air", MapColor.NONE);
        MAP_COLORS.put("grass", MapColor.GRASS);
        MAP_COLORS.put("sand", MapColor.SAND);
        MAP_COLORS.put("cloth", MapColor.WOOL);
        MAP_COLORS.put("tnt", MapColor.FIRE);
        MAP_COLORS.put("ice", MapColor.ICE);
        MAP_COLORS.put("iron", MapColor.METAL);
        MAP_COLORS.put("foliage", MapColor.PLANT);
        MAP_COLORS.put("snow", MapColor.SNOW);
        MAP_COLORS.put("clay", MapColor.CLAY);
        MAP_COLORS.put("dirt", MapColor.DIRT);
        MAP_COLORS.put("stone", MapColor.STONE);
        MAP_COLORS.put("water", MapColor.WATER);
        MAP_COLORS.put("wood", MapColor.WOOD);
        MAP_COLORS.put("quartz", MapColor.QUARTZ);
        MAP_COLORS.put("adobe", MapColor.COLOR_ORANGE);
        MAP_COLORS.put("magenta", MapColor.COLOR_MAGENTA);
        MAP_COLORS.put("light_blue", MapColor.COLOR_LIGHT_BLUE);
        MAP_COLORS.put("yellow", MapColor.COLOR_YELLOW);
        MAP_COLORS.put("lime", MapColor.COLOR_LIGHT_GREEN);
        MAP_COLORS.put("pink", MapColor.COLOR_PINK);
        MAP_COLORS.put("gray", MapColor.COLOR_GRAY);
        MAP_COLORS.put("silver", MapColor.COLOR_LIGHT_GRAY);
        MAP_COLORS.put("cyan", MapColor.COLOR_CYAN);
        MAP_COLORS.put("purple", MapColor.COLOR_PURPLE);
        MAP_COLORS.put("blue", MapColor.COLOR_BLUE);
        MAP_COLORS.put("brown", MapColor.COLOR_BROWN);
        MAP_COLORS.put("green", MapColor.COLOR_GREEN);
        MAP_COLORS.put("red", MapColor.COLOR_RED);
        MAP_COLORS.put("black", MapColor.COLOR_BLACK);
        MAP_COLORS.put("gold", MapColor.GOLD);
        MAP_COLORS.put("diamond", MapColor.DIAMOND);
        MAP_COLORS.put("lapis", MapColor.LAPIS);
        MAP_COLORS.put("emerald", MapColor.EMERALD);
        MAP_COLORS.put("obsidian", MapColor.COLOR_BLACK);
        MAP_COLORS.put("netherrack", MapColor.NETHER);
        SOUND_TYPES.put("wood", SoundType.WOOD);
        SOUND_TYPES.put("ground", SoundType.GRAVEL);
        SOUND_TYPES.put("plant", SoundType.GRASS);
        SOUND_TYPES.put("stone", SoundType.STONE);
        SOUND_TYPES.put("metal", SoundType.METAL);
        SOUND_TYPES.put("glass", SoundType.GLASS);
        SOUND_TYPES.put("cloth", SoundType.WOOL);
        SOUND_TYPES.put("sand", SoundType.SAND);
        SOUND_TYPES.put("snow", SoundType.SNOW);
        SOUND_TYPES.put("ladder", SoundType.LADDER);
        SOUND_TYPES.put("anvil", SoundType.ANVIL);
        SOUND_TYPES.put("slime", SoundType.SLIME_BLOCK);
    }

    private static void material(String name, MapColor color, SoundType sound, boolean solid) { MATERIALS.put(name, new Preset(color, sound, solid)); }

    public static Preset material(String name, Object context) {
        Preset preset = MATERIALS.get(key(name));
        if (preset != null) { return preset; }
        ContentLog.LOGGER.error("Unknown material '{}' in {}, using rock", name, context);
        return MATERIALS.get("rock");
    }

    public static MapColor mapColor(String name, MapColor fallback, Object context) {
        if (name == null || name.isEmpty()) { return fallback; }
        MapColor color = MAP_COLORS.get(key(name));
        if (color != null) { return color; }
        ContentLog.LOGGER.error("Unknown mapColor '{}' in {}, using the material default", name, context);
        return fallback;
    }

    public static SoundType soundType(String name, SoundType fallback, Object context) {
        if (name == null || name.isEmpty()) { return fallback; }
        SoundType sound = SOUND_TYPES.get(key(name));
        if (sound != null) { return sound; }
        ContentLog.LOGGER.error("Unknown soundType '{}' in {}, keeping the default", name, context);
        return fallback;
    }

    public static Rarity rarity(String name, Object context) {
        if (name == null || name.isEmpty()) { return Rarity.COMMON; }
        try { return Rarity.valueOf(name.toUpperCase(Locale.ROOT)); }
        catch (IllegalArgumentException ex) {
            ContentLog.LOGGER.error("Unknown rarity '{}' in {}, using common", name, context);
            return Rarity.COMMON;
        }
    }

    public static BlockBehaviour.Properties properties(BlockDef def, BlockVariant variant, boolean plant) {
        Preset preset = material(def.material(), def.key());
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.of()
                .mapColor(mapColor(def.mapColor(), preset.color(), def.key()))
                .sound(soundType(def.soundType(), preset.sound(), def.key()))
                .friction(def.slipperiness());
        float resistance = variant.resistance() / Math.max(0.01F, def.explosionResistanceDivisor());
        if (variant.hardness() < 0.0F) { properties = properties.strength(-1.0F, UNBREAKABLE); }
        else { properties = properties.strength(variant.hardness(), resistance); }
        int light = variant.light();
        if (light > 0) { properties = properties.lightLevel(state -> light); }
        if (variant.harvestLevelOr(def.harvestToolLevel()) > 0) { properties = properties.requiresCorrectToolForDrops(); }
        if (!def.opaque() || !def.fullCube()) { properties = properties.noOcclusion(); }
        if (def.flammability() > 0) { properties = properties.ignitedByLava(); }
        if (plant || !preset.solid()) { properties = properties.noCollission().pushReaction(PushReaction.DESTROY); }
        if (plant) { properties = properties.randomTicks(); }
        return properties;
    }

    private static String key(String name) { return name == null ? "" : name.trim().toLowerCase(Locale.ROOT); }

    public record Preset(MapColor color, SoundType sound, boolean solid) {}
}
