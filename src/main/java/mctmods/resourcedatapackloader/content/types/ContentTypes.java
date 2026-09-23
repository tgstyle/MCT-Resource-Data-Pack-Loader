package mctmods.resourcedatapackloader.content.types;

import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class ContentTypes {
    private static final float UNBREAKABLE = 3600000.0F;
    private static final float LEGACY_RESISTANCE = 0.6F;
    private static final Set<String> HOLDS_BACK_LIQUID = Set.of("portal", "structure_void");
    private static final Map<String, Preset> MATERIALS = new HashMap<>();
    private static final Map<String, MapColor> MAP_COLORS = new HashMap<>();
    private static final Map<String, SoundType> SOUND_TYPES = new HashMap<>();

    private ContentTypes() {}

    static {
        material("air", MapColor.NONE, false, false, false, true, PushReaction.NORMAL);
        material("grass", MapColor.GRASS, true, false, false, false, PushReaction.NORMAL);
        material("ground", MapColor.DIRT, true, false, false, false, PushReaction.NORMAL);
        material("wood", MapColor.WOOD, true, false, true, false, PushReaction.NORMAL);
        material("rock", MapColor.STONE, true, true, false, false, PushReaction.NORMAL);
        material("iron", MapColor.METAL, true, true, false, false, PushReaction.NORMAL);
        material("anvil", MapColor.METAL, true, true, false, false, PushReaction.BLOCK);
        material("water", MapColor.WATER, false, false, false, true, PushReaction.DESTROY);
        material("lava", MapColor.FIRE, false, false, false, true, PushReaction.DESTROY);
        material("leaves", MapColor.PLANT, true, false, true, false, PushReaction.DESTROY);
        material("plants", MapColor.PLANT, false, false, false, false, PushReaction.DESTROY);
        material("vine", MapColor.PLANT, false, false, true, true, PushReaction.DESTROY);
        material("sponge", MapColor.COLOR_YELLOW, true, false, false, false, PushReaction.NORMAL);
        material("cloth", MapColor.WOOL, true, false, true, false, PushReaction.NORMAL);
        material("fire", MapColor.NONE, false, false, false, true, PushReaction.DESTROY);
        material("sand", MapColor.SAND, true, false, false, false, PushReaction.NORMAL);
        material("circuits", MapColor.NONE, false, false, false, false, PushReaction.DESTROY);
        material("carpet", MapColor.WOOL, false, false, true, false, PushReaction.NORMAL);
        material("glass", MapColor.NONE, true, false, false, false, PushReaction.NORMAL);
        material("redstone_light", MapColor.NONE, true, false, false, false, PushReaction.NORMAL);
        material("tnt", MapColor.FIRE, true, false, true, false, PushReaction.NORMAL);
        material("coral", MapColor.PLANT, true, false, false, false, PushReaction.DESTROY);
        material("ice", MapColor.ICE, true, false, false, false, PushReaction.NORMAL);
        material("packed_ice", MapColor.ICE, true, false, false, false, PushReaction.NORMAL);
        material("snow", MapColor.SNOW, false, true, false, true, PushReaction.DESTROY);
        material("crafted_snow", MapColor.SNOW, true, true, false, false, PushReaction.NORMAL);
        material("cactus", MapColor.PLANT, true, false, false, false, PushReaction.DESTROY);
        material("clay", MapColor.CLAY, true, false, false, false, PushReaction.NORMAL);
        material("gourd", MapColor.PLANT, true, false, false, false, PushReaction.DESTROY);
        material("dragon_egg", MapColor.PLANT, true, false, false, false, PushReaction.DESTROY);
        material("portal", MapColor.NONE, false, false, false, false, PushReaction.BLOCK);
        material("cake", MapColor.NONE, true, false, false, false, PushReaction.DESTROY);
        material("web", MapColor.WOOL, false, true, false, false, PushReaction.DESTROY);
        material("piston", MapColor.STONE, true, false, false, false, PushReaction.BLOCK);
        material("barrier", MapColor.NONE, true, true, false, false, PushReaction.BLOCK);
        material("structure_void", MapColor.NONE, false, false, false, true, PushReaction.NORMAL);
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
        MAP_COLORS.put("obsidian", MapColor.PODZOL);
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

    private static void material(String name, MapColor color, boolean blocks, boolean tool, boolean burns, boolean replaceable, PushReaction push) { MATERIALS.put(name, new Preset(color, SoundType.STONE, blocks, tool, burns, replaceable, push)); }

    public static Preset material(String name, Object context) {
        Preset preset = MATERIALS.get(key(name));
        if (preset != null) { return preset; }
        ContentLog.LOGGER.error("Unknown material '{}' in {}, using rock", name, context);
        return MATERIALS.get("rock");
    }

    public static String materialName(BlockDef def) {
        return switch (def.type()) {
            case ContentBlockTypes.LOG -> "wood";
            case ContentBlockTypes.LEAVES -> "leaves";
            case ContentBlockTypes.VINE -> "vine";
            case ContentBlockTypes.TORCH, ContentBlockTypes.LADDER -> "circuits";
            case ContentBlockTypes.CROP -> "plants";
            default -> def.material();
        };
    }

    @SuppressWarnings("deprecation") public static boolean washedAway(BlockState state) {
        ContentRegistry.BlockEntry entry = ContentRegistry.entry(state.getBlock());
        return entry != null && !state.blocksMotion() && !HOLDS_BACK_LIQUID.contains(materialName(entry.def()));
    }

    private static String modelledType(String type) { return ContentBlockTypes.borrowsModel(type) ? ContentBlockTypes.BASIC : type; }

    private static Preset preset(BlockDef def, String type) {
        if (ContentBlockTypes.borrowsModel(type)) { return modelled(ContentBlockTypes.base(def), type); }
        Preset preset = material(materialName(def), def.key());
        return switch (type) {
            case ContentBlockTypes.LOG -> preset.sounding(SoundType.WOOD);
            case ContentBlockTypes.LEAVES, ContentBlockTypes.CROP -> preset.sounding(SoundType.GRASS);
            default -> preset;
        };
    }

    @SuppressWarnings("deprecation") private static Preset modelled(BlockState model, String type) {
        ContentRegistry.BlockEntry entry = ContentRegistry.entry(model.getBlock());
        MapColor color = model.getMapColor(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
        if (entry != null) {
            Preset preset = preset(entry.def(), modelledType(entry.def().type()));
            return new Preset(ContentBlockTypes.STAIRS.equals(type) ? color : preset.color(), model.getSoundType(), preset.blocks(), preset.tool(), preset.burns(), preset.replaceable(), preset.push());
        }
        return new Preset(color, model.getSoundType(), model.isSolid(), model.requiresCorrectToolForDrops(), model.ignitedByLava(), model.canBeReplaced(), model.getPistonPushReaction());
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

    public static SoundType sound(BlockDef def, String type) { return sound(def, preset(def, type)); }

    private static SoundType sound(BlockDef def, Preset preset) { return soundType(def.soundType(), preset.sound(), def.key()); }

    public static Rarity rarity(String name, Object context) {
        if (name == null || name.isEmpty()) { return Rarity.COMMON; }
        try { return Rarity.valueOf(name.toUpperCase(Locale.ROOT)); }
        catch (IllegalArgumentException ex) {
            ContentLog.LOGGER.error("Unknown rarity '{}' in {}, using common", name, context);
            return Rarity.COMMON;
        }
    }

    public static float legacyResistance(float written) { return written * LEGACY_RESISTANCE; }

    @SuppressWarnings("deprecation") public static BlockBehaviour.Properties properties(BlockDef def, BlockVariant variant, String type) {
        Preset preset = preset(def, type);
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.of()
                .mapColor(ContentBlockTypes.borrowsModel(type) ? preset.color() : mapColor(def.mapColor(), preset.color(), def.key()))
                .sound(sound(def, preset))
                .friction(def.slipperiness())
                .pushReaction(preset.push());
        float resistance = legacyResistance(variant.resistance() / Math.max(0.01F, def.explosionResistanceDivisor()));
        if (variant.hardness() < 0.0F) { properties = properties.strength(-1.0F, UNBREAKABLE); }
        else { properties = properties.strength(variant.hardness(), resistance); }
        int light = variant.light();
        if (light > 0) { properties = properties.lightLevel(state -> light); }
        if (preset.tool()) { properties = properties.requiresCorrectToolForDrops(); }
        if (!def.opaque() || !def.fullCube() || def.container() != null && def.container().chestModel()) { properties = properties.noOcclusion(); }
        if (preset.burns()) { properties = properties.ignitedByLava(); }
        if (preset.replaceable()) { properties = properties.replaceable(); }
        properties = preset.blocks() ? properties.forceSolidOn() : properties.forceSolidOff();
        if (ContentBlockTypes.plant(type)) { properties = properties.noCollission().randomTicks(); }
        return properties;
    }

    private static String key(String name) { return name == null ? "" : name.trim().toLowerCase(Locale.ROOT); }

    public record Preset(MapColor color, SoundType sound, boolean blocks, boolean tool, boolean burns, boolean replaceable, PushReaction push) {
        Preset sounding(SoundType other) { return new Preset(color, other, blocks, tool, burns, replaceable, push); }
    }
}
