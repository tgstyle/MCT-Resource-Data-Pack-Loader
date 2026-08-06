package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;
import mctmods.resourcedatapackloader.content.block.ContentBlockCane;
import mctmods.resourcedatapackloader.content.block.ContentBlockCrop;
import mctmods.resourcedatapackloader.content.block.ContentBlockFlower;
import mctmods.resourcedatapackloader.content.block.ContentBlockSapling;
import mctmods.resourcedatapackloader.content.block.ContentBlockFluid;
import mctmods.resourcedatapackloader.content.block.ContentFluid;
import mctmods.resourcedatapackloader.content.def.*;
import mctmods.resourcedatapackloader.content.worldgen.ContentCofhWorld;
import mctmods.resourcedatapackloader.core.CofhWorldContainer;
import mctmods.resourcedatapackloader.content.worldgen.ContentSpawning;
import mctmods.resourcedatapackloader.content.interfaces.IContentBlock;
import mctmods.resourcedatapackloader.content.types.*;
import mctmods.resourcedatapackloader.content.util.ContentMaterials;
import mctmods.resourcedatapackloader.content.util.ContentOreDict;
import mctmods.resourcedatapackloader.core.util.ConfigCore;
import mctmods.resourcedatapackloader.core.util.ConfigLate;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.pack.PackOptions;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonParseException;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import java.util.*;
import javax.annotation.Nullable;

public final class ContentRegistry {
    private static final Map<ResourceLocation, BlockDef> BLOCK_DEFS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, ItemDef> ITEM_DEFS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, FluidDef> FLUID_DEFS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, WorldgenDef> WORLDGEN_DEFS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, Block> BLOCKS_BY_NAME = new LinkedHashMap<>();
    private static final Map<ResourceLocation, Item> ITEMS_BY_NAME = new LinkedHashMap<>();
    private static final Map<ResourceLocation, ContentBlockFluid> FLUID_BLOCKS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, BlockDef> DEF_BY_BLOCK = new LinkedHashMap<>();
    private static final Set<String> WARNED = new HashSet<>();
    private static boolean loaded;

    private ContentRegistry() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (!Config.content.load) { return; }

        for (Map.Entry<ResourceLocation, String> held : ContentInherits.collect(PackManager.BLOCKS).entrySet()) {
            ResourceLocation key = held.getKey();
            if (ContentOwners.reserved(key)) { continue; }
            try {
                BlockDef def = ContentParser.block(key, held.getValue());
                if (def != null) { BLOCK_DEFS.put(key, def); }
            }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in block definition {}, ignoring it: {}", key, ex.getMessage()); }
        }

        for (Map.Entry<ResourceLocation, String> held : ContentInherits.collect(PackManager.ITEMS).entrySet()) {
            ResourceLocation key = held.getKey();
            if (ContentOwners.reserved(key)) { continue; }
            try {
                ItemDef def = ContentParser.item(key, held.getValue());
                if (def != null) { ITEM_DEFS.put(key, def); }
            }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in item definition {}, ignoring it: {}", key, ex.getMessage()); }
        }

        PackManager.get().forEach(PackManager.FLUIDS, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            if (ContentOwners.reserved(key)) { return; }
            try {
                FluidDef def = ContentParser.fluid(key, contents);
                if (def != null) { FLUID_DEFS.put(key, def); }
            }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in fluid definition {}, ignoring it: {}", key, ex.getMessage()); }
        });

        PackManager.get().forEach(PackManager.WORLDGEN, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            if (ContentOwners.reserved(key)) { return; }
            try {
                WorldgenDef def = ContentParser.worldgen(key, contents);
                if (def != null) { WORLDGEN_DEFS.put(key, def); }
            }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in worldgen definition {}, ignoring it: {}", key, ex.getMessage()); }
        });

        if (ConfigCore.read(ConfigLate.WORLDGEN, "readCofhWorldFiles") && (!Loader.isModLoaded("cofhworld") || CofhWorldContainer.emulated())) {
            for (Map.Entry<ResourceLocation, String> entry : ContentCofhWorld.collect().entrySet()) {
                if (WORLDGEN_DEFS.containsKey(entry.getKey())) { continue; }
                try {
                    WorldgenDef def = ContentParser.worldgen(entry.getKey(), entry.getValue());
                    if (def != null) { WORLDGEN_DEFS.put(entry.getKey(), def); }
                }
                catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in CoFH World entry {}, ignoring it: {}", entry.getKey(), ex.getMessage()); }
            }
        }

        if (!BLOCK_DEFS.isEmpty() || !ITEM_DEFS.isEmpty() || !FLUID_DEFS.isEmpty() || !WORLDGEN_DEFS.isEmpty()) {
            Summary.info("content", "Loaded " + BLOCK_DEFS.size() + " block, " + ITEM_DEFS.size() + " item, " + FLUID_DEFS.size() + " fluid and " + WORLDGEN_DEFS.size() + " worldgen definition(s)");
        }
    }

    public static boolean available(List<String> requires, ResourceLocation key) {
        for (String modid : requires) {
            if (modid.startsWith("file:")) {
                String asked = modid.substring("file:".length()).replace('\\', '/');
                if (asked.contains("..") || asked.startsWith("/") || asked.contains(":")) {
                    if (WARNED.add(modid)) { ContentLog.LOGGER.warn("Skipping anything that requires {}, whose path must be relative to the game folder, without '..'", modid); }
                    return false;
                }
                java.io.File home = net.minecraft.launchwrapper.Launch.minecraftHome != null ? net.minecraft.launchwrapper.Launch.minecraftHome : new java.io.File(".");
                if (new java.io.File(home, asked).exists()) { continue; }

                ContentLog.LOGGER.debug("Skipping {}, it requires {}", key, modid);
                return false;
            }
            if (modid.startsWith("config:")) {
                String asked = modid.substring("config:".length());
                int split = asked.indexOf(':');
                Boolean held = split < 0 ? PackOptions.anywhere(asked) : PackOptions.option(asked.substring(0, split), asked.substring(split + 1));
                if (Boolean.TRUE.equals(held)) { continue; }

                if (held == null && WARNED.add(modid)) { ContentLog.LOGGER.warn("Skipping anything that requires {}, which no pack option file defines. Check the name against the files in rdploader/config", modid); }
                ContentLog.LOGGER.debug("Skipping {}, it requires {}", key, modid);
                return false;
            }
            if (Loader.isModLoaded(modid) || PackManager.get().provides(modid)) { continue; }

            if (WARNED.add(modid)) {
                ContentLog.LOGGER.info("Skipping anything that requires {}, which is neither an installed mod nor a loaded pack. Turn on debug logging to see each one", modid);
            }
            ContentLog.LOGGER.debug("Skipping {}, it requires {}", key, modid);
            return false;
        }
        return true;
    }

    public static boolean wantsBuckets() {
        load();
        for (FluidDef def : FLUID_DEFS.values()) {
            if (def.bucket && available(def.requires, def.registryName)) { return true; }
        }
        return false;
    }

    public static void registerFluids() {
        load();
        for (FluidDef def : FLUID_DEFS.values()) {
            if (!available(def.requires, def.registryName)) { continue; }

            boolean registered;
            Fluid fluid;
            ModContainer previous = Loader.instance().activeModContainer();
            try {
                Loader.instance().setActiveModContainer(ContentOwners.of(def.registryName.getNamespace()));
                fluid = new ContentFluid(def);
                registered = FluidRegistry.registerFluid(fluid);
                if (registered && def.bucket) { FluidRegistry.addBucketForFluid(fluid); }
            }
            finally { Loader.instance().setActiveModContainer(previous); }

            if (registered) {
                def.resolve(fluid);
                continue;
            }

            Fluid existing = FluidRegistry.getFluid(def.name);
            ContentLog.LOGGER.warn("A fluid named {} is already registered, {} will use the existing one", def.name, def.registryName);
            def.resolve(existing);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        load();
        for (Map.Entry<ResourceLocation, BlockDef> entry : BLOCK_DEFS.entrySet()) {
            ResourceLocation key = entry.getKey();
            BlockDef def = entry.getValue();
            if (!available(def.requires, key)) { continue; }
            ModContainer previous = Loader.instance().activeModContainer();
            try {
                Loader.instance().setActiveModContainer(ContentOwners.of(key.getNamespace()));
                for (Block block : ContentBlockTypes.get(def.type, key).create(def)) {
                    ResourceLocation name = block.getRegistryName();
                    if (name == null) {
                        ContentLog.LOGGER.error("A block made from {} has no registry name, skipping it", key);
                        continue;
                    }
                    if (ForgeRegistries.BLOCKS.containsKey(name)) {
                        ContentLog.LOGGER.warn("A block named {} is already registered, skipping the pack definition", name);
                        continue;
                    }
                    event.getRegistry().register(block);
                    BLOCKS_BY_NAME.put(name, block);
                    DEF_BY_BLOCK.put(name, def);
                }
            }
            finally { Loader.instance().setActiveModContainer(previous); }
        }

        for (Map.Entry<ResourceLocation, FluidDef> entry : FLUID_DEFS.entrySet()) {
            ResourceLocation key = entry.getKey();
            FluidDef def = entry.getValue();
            if (!def.createBlock) { continue; }
            if (!available(def.requires, key)) { continue; }
            if (def.getResolved() == null) {
                ContentLog.LOGGER.error("Fluid {} was never registered, its block is skipped", key);
                continue;
            }
            if (ForgeRegistries.BLOCKS.containsKey(key)) {
                ContentLog.LOGGER.warn("A block named {} is already registered, skipping the pack fluid block", key);
                continue;
            }

            ModContainer previous = Loader.instance().activeModContainer();
            try {
                Loader.instance().setActiveModContainer(ContentOwners.of(key.getNamespace()));
                ContentBlockFluid block = new ContentBlockFluid(def.getResolved(), def);
                ContentSetup.apply(block, def.creativeTab);
                event.getRegistry().register(block);
                FLUID_BLOCKS.put(key, block);
            }
            finally { Loader.instance().setActiveModContainer(previous); }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) { ContentEntities.register(event.getRegistry()); }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerItems(RegistryEvent.Register<Item> event) {
        ContentMaterials.register();

        for (Map.Entry<ResourceLocation, Block> entry : BLOCKS_BY_NAME.entrySet()) {
            ResourceLocation key = entry.getKey();
            if (ForgeRegistries.ITEMS.containsKey(key)) { continue; }
            ModContainer previous = Loader.instance().activeModContainer();
            try {
                Loader.instance().setActiveModContainer(ContentOwners.of(key.getNamespace()));
                Block block = entry.getValue();
                if (!(block instanceof IContentBlock)) { continue; }
                Item blockItem = ((IContentBlock) block).createItem();
                if (blockItem == null) { continue; }
                blockItem.setRegistryName(key);
                event.getRegistry().register(blockItem);
            }
            finally { Loader.instance().setActiveModContainer(previous); }
        }

        for (Map.Entry<ResourceLocation, ItemDef> entry : ITEM_DEFS.entrySet()) {
            ResourceLocation key = entry.getKey();
            if (ForgeRegistries.ITEMS.containsKey(key)) {
                ContentLog.LOGGER.warn("An item named {} is already registered, skipping the pack definition", key);
                continue;
            }
            ItemDef def = entry.getValue();
            if (!available(def.requires, key)) { continue; }
            ModContainer previous = Loader.instance().activeModContainer();
            try {
                Loader.instance().setActiveModContainer(ContentOwners.of(key.getNamespace()));
                for (Item item : ContentItemTypes.get(def.type, key).create(def)) {
                    event.getRegistry().register(item);
                    ITEMS_BY_NAME.put(key, item);
                }
            }
            finally { Loader.instance().setActiveModContainer(previous); }
        }

        resolve();
        resolveCrops();
        ContentSpawning.resolve();
        ContentMaterials.resolveRepairItems();
        registerOreDictionary();
        ContentOreDict.apply();
    }

    public static List<WorldgenDef> resolveWorldgen() {
        List<WorldgenDef> active = new ArrayList<>();
        for (Map.Entry<ResourceLocation, WorldgenDef> entry : WORLDGEN_DEFS.entrySet()) {
            WorldgenDef def = entry.getValue();
            if (!available(def.requires, entry.getKey())) { continue; }

            if (def.blocks.isEmpty() && !ForgeRegistries.BLOCKS.containsKey(def.block)) {
                ContentLog.LOGGER.error("Worldgen {} names block {}, which is not registered, skipping it", entry.getKey(), def.block);
                continue;
            }
            Set<Block> targets = new LinkedHashSet<>();
            Set<IBlockState> exact = new LinkedHashSet<>();
            for (BlockMatchDef name : def.replaces) {
                Block target = ForgeRegistries.BLOCKS.containsKey(name.block) ? ForgeRegistries.BLOCKS.getValue(name.block) : null;
                if (target == null) {
                    ContentLog.LOGGER.error("Worldgen {} names replace block {}, which is not registered, leaving it out", entry.getKey(), name.block);
                    continue;
                }
                if (!name.properties.isEmpty()) { exact.add(ContentStates.of(target, 0, name.properties, entry.getKey())); }
                else if (name.meta >= 0) { exact.add(ContentStates.of(target, name.meta)); }
                else { targets.add(target); }
            }
            if (targets.isEmpty() && exact.isEmpty()) {
                ContentLog.LOGGER.error("Worldgen {} has no registered block to replace, skipping it", entry.getKey());
                continue;
            }

            Set<Block> surface = new LinkedHashSet<>();
            if (ShapeDef.DECORATION.equals(def.shape.type) || ShapeDef.TREE.equals(def.shape.type)) {
                Set<String> unknown = new LinkedHashSet<>();
                for (String name : def.shape.surface) {
                    ResourceLocation location = new ResourceLocation(name);
                    Block target = ForgeRegistries.BLOCKS.containsKey(location) ? ForgeRegistries.BLOCKS.getValue(location) : null;
                    if (target == null) {
                        unknown.add(name);
                        continue;
                    }
                    surface.add(target);
                }
                if (!unknown.isEmpty() && !surface.isEmpty()) { ContentLog.LOGGER.error("Worldgen {} names surface block(s) {}, which are not registered, leaving them out", entry.getKey(), unknown); }
                else if (!unknown.isEmpty()) {
                    ContentLog.LOGGER.error("Worldgen {} has no registered surface block, skipping it", entry.getKey());
                    continue;
                }
            }

            List<IBlockState> states = new ArrayList<>();
            List<Integer> weights = new ArrayList<>();
            if (def.blocks.isEmpty()) {
                Block block = ForgeRegistries.BLOCKS.getValue(def.block);
                if (block == null) { continue; }

                states.add(ContentStates.of(block, def.meta));
                weights.add(1);
            }
            else {
                Set<String> missing = new LinkedHashSet<>();
                for (BlockWeightDef weighted : def.blocks) {
                    Block block = ForgeRegistries.BLOCKS.containsKey(weighted.block) ? ForgeRegistries.BLOCKS.getValue(weighted.block) : null;
                    if (block == null) {
                        missing.add(weighted.block.toString());
                        continue;
                    }
                    states.add(ContentStates.of(block, weighted.meta, weighted.properties, entry.getKey()));
                    weights.add(weighted.weight);
                }
                if (!missing.isEmpty() && !states.isEmpty()) {
                    ContentLog.LOGGER.error("Worldgen {} names {} in its blocks list, which are not registered, leaving them out", entry.getKey(), missing);
                }
                else if (!missing.isEmpty()) {
                    ContentLog.LOGGER.error("Worldgen {} names only {} in its blocks list, none of which are registered, skipping it. Add \"requires\" naming the mod so this is expected rather than an error", entry.getKey(), missing);
                }
            }
            if (states.isEmpty()) { continue; }

            def.resolve(states, weights, targets, exact, surface, extra(entry.getKey(), def.shape.outline), extra(entry.getKey(), def.shape.fill));
            active.add(def);
        }

        if (!active.isEmpty()) { Summary.info("worldgen", "Generating " + active.size() + " vein type(s) from packs"); }
        return active;
    }

    @Nullable private static IBlockState extra(ResourceLocation key, String name) {
        if (name.isEmpty()) { return null; }

        IBlockState state = ContentStates.parse(name, key);
        if (state == null) { ContentLog.LOGGER.error("Worldgen {} names block {} in its shape, which is not registered, leaving it out", key, name); }
        return state;
    }

    private static void resolveCrops() {
        for (Map.Entry<ResourceLocation, Block> entry : BLOCKS_BY_NAME.entrySet()) {
            if (entry.getValue() instanceof ContentBlockCane) {
                ((ContentBlockCane) entry.getValue()).resolveSoil();
                continue;
            }
            if (entry.getValue() instanceof ContentBlockSapling) {
                ((ContentBlockSapling) entry.getValue()).resolveSoil();
                continue;
            }
            if (entry.getValue() instanceof ContentBlockFlower) {
                ((ContentBlockFlower) entry.getValue()).resolveSoil();
                continue;
            }
            if (!(entry.getValue() instanceof ContentBlockCrop)) { continue; }

            ContentBlockCrop crop = (ContentBlockCrop) entry.getValue();
            BlockDef def = DEF_BY_BLOCK.get(entry.getKey());
            if (def == null) { continue; }
            crop.resolve(ContentStacks.parse(def.registryName, def.cropSeed, 1), ContentStacks.parse(def.registryName, def.cropProduce, 1));
        }
    }

    private static void resolve() {
        for (BlockDef def : BLOCK_DEFS.values()) {
            for (BlockVariant variant : def.visible) {
                for (DropDef drop : variant.drops) {
                    if (!ForgeRegistries.ITEMS.containsKey(drop.block)) {
                        ContentLog.LOGGER.error("Drop {} for {} '{}' is not registered, that drop is skipped", drop.block, def.registryName, variant.name);
                        continue;
                    }
                    drop.resolve(ForgeRegistries.ITEMS.getValue(drop.block));
                }
            }
        }

        for (ItemDef def : ITEM_DEFS.values()) {
            for (ItemVariant variant : def.visible) {
                if (variant.potion == null) { continue; }
                variant.resolvePotion(potion(def.registryName, variant.name, variant.potion));
            }
            if (!def.container.isEmpty()) { def.resolveContainer(ContentStacks.parse(def.registryName, def.container, 1)); }
        }

        for (Map.Entry<ResourceLocation, ContentBlockFluid> entry : FLUID_BLOCKS.entrySet()) {
            FluidDef def = FLUID_DEFS.get(entry.getKey());
            List<PotionEffect> effects = new ArrayList<>();
            for (String value : def.potions) {
                PotionEffect effect = potion(def.registryName, def.name, value);
                if (effect != null) { effects.add(effect); }
            }
            entry.getValue().setEffects(effects);
        }
    }

    @Nullable private static PotionEffect potion(ResourceLocation key, String name, String value) {
        String[] parts = value.split(",");
        if (parts.length < 3) {
            ContentLog.LOGGER.error("Potion '{}' for {} '{}' needs id, duration and amplifier", value, key, name);
            return null;
        }

        Potion potion = Potion.getPotionFromResourceLocation(parts[0].trim());
        if (potion == null) {
            ContentLog.LOGGER.error("Unknown potion '{}' for {} '{}'", parts[0].trim(), key, name);
            return null;
        }

        try {
            int duration = Integer.parseInt(parts[1].trim());
            int amplifier = Integer.parseInt(parts[2].trim());
            boolean ambient = parts.length > 3 && Boolean.parseBoolean(parts[3].trim());
            return new PotionEffect(potion, duration, amplifier, ambient, false);
        }
        catch (NumberFormatException ex) {
            ContentLog.LOGGER.error("Potion '{}' for {} '{}' has a bad number", value, key, name);
            return null;
        }
    }

    private static void registerOreDictionary() {
        int count = 0;
        for (Map.Entry<ResourceLocation, Block> entry : BLOCKS_BY_NAME.entrySet()) {
            BlockDef def = DEF_BY_BLOCK.get(entry.getKey());
            if (def == null) { continue; }
            if (Item.getItemFromBlock(entry.getValue()) == Items.AIR) { continue; }
            for (BlockVariant variant : def.visible) {
                for (String name : variant.oreDict) {
                    OreDictionary.registerOre(name, new ItemStack(entry.getValue(), 1, variant.meta));
                    count++;
                }
            }
        }

        for (Map.Entry<ResourceLocation, Item> entry : ITEMS_BY_NAME.entrySet()) {
            ItemDef def = ITEM_DEFS.get(entry.getKey());
            for (ItemVariant variant : def.visible) {
                for (String name : variant.oreDict) {
                    OreDictionary.registerOre(name, new ItemStack(entry.getValue(), 1, variant.meta));
                    count++;
                }
            }
        }

        if (count > 0) { Summary.info("content_oredict", "Registered " + count + " ore dictionary entry/entries from packs"); }
    }

    public static Set<Map.Entry<ResourceLocation, Block>> registeredBlocks() { return BLOCKS_BY_NAME.entrySet(); }

    public static Set<Map.Entry<ResourceLocation, Item>> registeredItems() { return ITEMS_BY_NAME.entrySet(); }

    public static Set<Map.Entry<ResourceLocation, ContentBlockFluid>> registeredFluidBlocks() { return FLUID_BLOCKS.entrySet(); }

    @Nullable public static FluidDef fluidDef(ResourceLocation key) { return FLUID_DEFS.get(key); }

    @Nullable public static BlockDef blockDef(ResourceLocation key) { return DEF_BY_BLOCK.get(key); }

    @Nullable public static ItemDef itemDef(ResourceLocation key) { return ITEM_DEFS.get(key); }
}
