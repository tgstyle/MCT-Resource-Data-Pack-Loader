package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.block.ContentCropBlock;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.def.DropDef;
import mctmods.resourcedatapackloader.content.def.ExposureDef;
import mctmods.resourcedatapackloader.content.def.FluidDef;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.def.ItemVariant;
import mctmods.resourcedatapackloader.content.def.MaterialDef;
import mctmods.resourcedatapackloader.content.def.TabDef;
import mctmods.resourcedatapackloader.content.item.ContentDrinkItem;
import mctmods.resourcedatapackloader.content.types.ContentBlockTypes;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.pack.PackOptions;
import mctmods.resourcedatapackloader.pack.PackRequirements;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonParseException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.TriState;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentRegistry {
    public static final String MAIN = "";
    public static final String WALL = "wall";
    private static final Map<ResourceLocation, BlockDef> BLOCK_DEFS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, ItemDef> ITEM_DEFS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, FluidDef> FLUID_DEFS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, MaterialDef> MATERIAL_DEFS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, TabDef> TAB_DEFS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, ExposureDef> EXPOSURE_DEFS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, BlockEntry> BLOCKS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, ItemEntry> ITEMS = new LinkedHashMap<>();
    private static final Map<Block, BlockEntry> BY_BLOCK = new LinkedHashMap<>();
    private static final Set<String> WARNED = new HashSet<>();
    private static final Set<String> CLAIMED = new HashSet<>();
    private static boolean loaded;

    private ContentRegistry() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (Config.contentOff()) { return; }
        Json.eachFile(PackManager.EXPOSURES, "exposure definition", (key, contents) -> {
            ExposureDef def = ContentParser.exposure(key, contents);
            if (def != null) { EXPOSURE_DEFS.put(key, def); }
        });
        if (!EXPOSURE_DEFS.isEmpty()) { Summary.info("exposures", "Loaded " + EXPOSURE_DEFS.size() + " exposure definition(s)"); }
        for (Map.Entry<ResourceLocation, String> held : ContentInherits.collect(PackManager.BLOCKS).entrySet()) {
            if (reserved(held.getKey())) { continue; }
            try {
                BlockDef def = ContentParser.block(held.getKey(), held.getValue());
                if (def != null) { BLOCK_DEFS.put(held.getKey(), def); }
            }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in block definition {}, ignoring it: {}", held.getKey(), ex.getMessage()); }
        }
        for (Map.Entry<ResourceLocation, String> held : ContentInherits.collect(PackManager.ITEMS).entrySet()) {
            if (reserved(held.getKey())) { continue; }
            try {
                ItemDef def = ContentParser.item(held.getKey(), held.getValue());
                if (def != null) { ITEM_DEFS.put(held.getKey(), def); }
            }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in item definition {}, ignoring it: {}", held.getKey(), ex.getMessage()); }
        }
        Json.eachFile(PackManager.FLUIDS, "fluid definition", (key, contents) -> {
            if (reserved(key)) { return; }
            FluidDef def = ContentParser.fluid(key, contents);
            if (def != null) { FLUID_DEFS.put(key, def); }
        });
        Json.eachFile(PackManager.MATERIALS, "material file", (key, contents) -> {
            MaterialDef def = ContentParser.material(key, contents);
            if (def != null) { MATERIAL_DEFS.put(key, def); }
        });
        Json.eachFile(PackManager.TABS, "creative tab", (key, contents) -> {
            TabDef def = ContentParser.tab(key, contents);
            if (def != null) { TAB_DEFS.put(def.id(), def); }
        });
        if (!BLOCK_DEFS.isEmpty() || !ITEM_DEFS.isEmpty() || !FLUID_DEFS.isEmpty() || !TAB_DEFS.isEmpty()) {
            Summary.info("content", "Loaded " + BLOCK_DEFS.size() + " block, " + ITEM_DEFS.size() + " item, " + FLUID_DEFS.size() + " fluid and " + TAB_DEFS.size() + " creative tab definition(s)");
        }
        if (!MATERIAL_DEFS.isEmpty()) { Summary.info("materials", "Loaded " + MATERIAL_DEFS.size() + " material definition(s)"); }
    }

    public static boolean reserved(ResourceLocation key) {
        if (!ResourceDataPackLoader.MOD_ID.equals(key.getNamespace())) { return false; }
        if (CLAIMED.add(key.getNamespace())) { ContentLog.LOGGER.error("A pack is trying to define content under '{}', which belongs to this mod. Content there is ignored, because it would claim ownership of things this mod registers and confuse the whitelists that read it. Use your own namespace, such as the pack name. Overriding this mod's own assets is still fine, only registering content is not", key.getNamespace()); }
        return true;
    }

    public static boolean available(List<String> requires, ResourceLocation key) {
        for (String asked : requires) {
            if (asked.startsWith(PackRequirements.CONFIG_GATE)) { PackOptions.gating(asked.substring(PackRequirements.CONFIG_GATE.length())); }
        }
        for (String asked : requires) {
            if (asked.startsWith(PackRequirements.FILE_GATE)) {
                String path = asked.substring(PackRequirements.FILE_GATE.length()).replace('\\', '/');
                if (path.contains("..") || path.startsWith("/") || path.contains(":")) {
                    if (WARNED.add(asked)) { ContentLog.LOGGER.warn("Skipping anything that requires {}, whose path must be relative to the game folder, without '..'", asked); }
                    return false;
                }
                if (Files.exists(PackRequirements.gameDirectory().resolve(path))) { continue; }
                ContentLog.LOGGER.debug("Skipping {}, it requires {}", key, asked);
                return false;
            }
            if (asked.startsWith(PackRequirements.CONFIG_GATE)) {
                String option = asked.substring(PackRequirements.CONFIG_GATE.length());
                int split = option.indexOf(':');
                Boolean held = split < 0 ? PackOptions.anywhere(option) : PackOptions.option(option.substring(0, split), option.substring(split + 1));
                if (Boolean.TRUE.equals(held)) { continue; }
                if (held == null && WARNED.add(asked)) { ContentLog.LOGGER.warn("Skipping anything that requires {}, which no pack option file defines. Check the name against the files in rdploader/config", asked); }
                ContentLog.LOGGER.debug("Skipping {}, it requires {}", key, asked);
                return false;
            }
            if (PackRequirements.modLoaded(asked) || PackManager.get().provides(asked)) { continue; }
            if (WARNED.add(asked)) { ContentLog.LOGGER.info("Skipping anything that requires {}, which is neither an installed mod nor a loaded pack. Turn on debug logging to see each one", asked); }
            ContentLog.LOGGER.debug("Skipping {}, it requires {}", key, asked);
            return false;
        }
        return true;
    }

    public static void addDef(BlockDef def) { BLOCK_DEFS.put(def.key(), def); }

    public static boolean hasDef(ResourceLocation key) { return BLOCK_DEFS.containsKey(key); }

    public static Collection<BlockDef> blockDefs() { return Collections.unmodifiableCollection(BLOCK_DEFS.values()); }

    public static Collection<ItemDef> itemDefs() { return Collections.unmodifiableCollection(ITEM_DEFS.values()); }

    public static Collection<FluidDef> fluidDefs() { return Collections.unmodifiableCollection(FLUID_DEFS.values()); }

    public static Collection<MaterialDef> materialDefs() { return Collections.unmodifiableCollection(MATERIAL_DEFS.values()); }

    public static Collection<TabDef> tabDefs() { return Collections.unmodifiableCollection(TAB_DEFS.values()); }

    public static Collection<ExposureDef> exposures() { return Collections.unmodifiableCollection(EXPOSURE_DEFS.values()); }

    @Nullable public static MaterialDef material(String name, Object context) {
        if (name == null || name.isEmpty()) {
            ContentLog.LOGGER.error("{} names no material, the item is skipped", context);
            return null;
        }
        ResourceLocation key = ResourceLocation.tryParse(name);
        MaterialDef def = key == null ? null : MATERIAL_DEFS.get(key);
        if (def != null) { return available(def.requires(), def.key()) ? def : null; }
        ContentLog.LOGGER.error("Unknown material '{}' in {}, the item is skipped. Known materials are {}", name, context, MATERIAL_DEFS.keySet());
        return null;
    }

    @Nullable public static TabDef tab(String label) {
        if (label == null || label.isEmpty()) { return null; }
        ResourceLocation key = ResourceLocation.tryParse(label);
        return key == null ? null : TAB_DEFS.get(key);
    }

    public static void addBlock(ResourceLocation id, Block block, BlockDef def, BlockVariant variant, String role) {
        BlockEntry entry = new BlockEntry(id, block, def, variant, role);
        BLOCKS.put(id, entry);
        BY_BLOCK.put(block, entry);
        if (def.flammability() > 0) { ((FireBlock) Blocks.FIRE).setFlammable(block, def.fireSpread(), def.flammability()); }
    }

    public static void addItem(ResourceLocation id, Item item, @Nullable ItemDef def, @Nullable ItemVariant variant, @Nullable BlockEntry block, String tab) {
        ItemEntry entry = new ItemEntry(id, item, def, variant, block, tabId(tab, id.getNamespace()));
        ITEMS.put(id, entry);
    }

    private static String tabId(String named, String namespace) {
        String wanted = named == null ? "" : named.trim();
        if (wanted.isEmpty() || wanted.indexOf(':') >= 0) { return wanted; }
        String vanilla = ContentFormats.vanillaTab(wanted);
        return vanilla != null ? vanilla : namespace + ":" + wanted.toLowerCase(Locale.ROOT);
    }

    public static Collection<BlockEntry> blocks() { return Collections.unmodifiableCollection(BLOCKS.values()); }

    public static Collection<ItemEntry> items() { return Collections.unmodifiableCollection(ITEMS.values()); }

    @Nullable public static BlockEntry block(ResourceLocation id) { return BLOCKS.get(id); }

    @Nullable public static BlockEntry entry(Block block) { return BY_BLOCK.get(block); }

    @Nullable public static Integer lightBlock(Block block) {
        BlockEntry entry = BY_BLOCK.get(block);
        if (entry == null || ContentBlockTypes.SLAB.equals(entry.def().type())) { return null; }
        BlockDef def = entry.def();
        if (ContentBlockTypes.CONTAINER.equals(def.type()) && def.container() != null && def.container().chestModel()) { return 0; }
        if (ContentBlockTypes.BELL.equals(def.type())) { return 0; }
        return Math.clamp(def.lightOpacity(), 0, 15);
    }

    public static boolean lacks(String behavior, Block block) {
        BlockEntry entry = BY_BLOCK.get(block);
        return entry == null || !entry.def().behavesAs().contains(behavior);
    }

    public static Set<Block> resolveSoil(Iterable<String> names, ResourceLocation owner) {
        Set<Block> resolved = new HashSet<>();
        for (String name : names) {
            Block block = Registered.find(BuiltInRegistries.BLOCK, ResourceLocation.tryParse(name));
            if (block != null) { resolved.add(block); }
            else { ContentLog.LOGGER.error("{} names soil {}, which is not registered, leaving it out", owner, name); }
        }
        return resolved;
    }

    public static void resolveStacks() {
        for (BlockDef def : BLOCK_DEFS.values()) {
            for (BlockVariant variant : def.variants()) {
                for (DropDef drop : variant.drops()) { resolveDrop(def, variant, drop); }
            }
        }
        for (BlockEntry entry : BLOCKS.values()) {
            if (entry.block() instanceof ContentCropBlock crop) { crop.resolve(ContentStacks.find(entry.def().key(), entry.def().cropSeed()), ContentStacks.find(entry.def().key(), entry.def().cropProduce())); }
        }
        ContentGenerated.cropLoot();
        for (ItemDef def : ITEM_DEFS.values()) {
            if (def.container().isEmpty()) { continue; }
            Item container = ContentStacks.find(def.key(), def.container());
            for (ItemEntry entry : ITEMS.values()) {
                if (entry.def() == def && entry.item() instanceof ContentDrinkItem drink) { drink.resolveContainer(container); }
            }
        }
    }

    private static void resolveDrop(BlockDef def, BlockVariant variant, DropDef drop) {
        if (drop.entity() != null) {
            if (!BuiltInRegistries.ENTITY_TYPE.containsKey(drop.entity())) { ContentLog.LOGGER.error("Drop entity {} for {} '{}' is not registered, that drop is skipped", drop.entity(), def.key(), variant.name()); }
            return;
        }
        if (drop.item() == null || !ContentStacks.registered(drop.item())) { ContentLog.LOGGER.error("Drop {} for {} '{}' is not registered, that drop is skipped", drop.item(), def.key(), variant.name()); }
    }

    public static boolean sustains(Set<Block> soil, BlockGetter level, BlockPos ground, BlockState plant) {
        BlockState state = level.getBlockState(ground);
        if (!soil.isEmpty()) { return soil.contains(state.getBlock()); }
        TriState decided = state.canSustainPlant(level, ground, Direction.UP, plant);
        if (decided.isDefault()) { return state.is(BlockTags.DIRT) || state.getBlock() instanceof FarmBlock; }
        return decided.isTrue();
    }

    public record BlockEntry(ResourceLocation id, Block block, BlockDef def, BlockVariant variant, String role) {
        public boolean isMain() { return MAIN.equals(role); }
    }

    public record ItemEntry(ResourceLocation id, Item item, @Nullable ItemDef def, @Nullable ItemVariant variant, @Nullable BlockEntry block, String tab) {}
}
