package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.block.ContentBushBlock;
import mctmods.resourcedatapackloader.content.block.ContentCaneBlock;
import mctmods.resourcedatapackloader.content.block.ContentCropBlock;
import mctmods.resourcedatapackloader.content.block.ContentSaplingBlock;
import mctmods.resourcedatapackloader.content.extra.ContentPotions;
import mctmods.resourcedatapackloader.content.extra.ContentSounds;
import mctmods.resourcedatapackloader.content.extra.ContentVillagers;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.entity.ContentEntityTypes;
import mctmods.resourcedatapackloader.content.entity.ContentEntitySpawns;
import mctmods.resourcedatapackloader.content.item.ContentPotionItem;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.def.ItemVariant;
import mctmods.resourcedatapackloader.content.def.TabDef;
import mctmods.resourcedatapackloader.content.block.ContentFluids;
import mctmods.resourcedatapackloader.content.types.ContentBlockTypes;
import mctmods.resourcedatapackloader.content.types.ContentItemTypes;
import mctmods.resourcedatapackloader.content.util.ContentMaterials;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomes;
import mctmods.resourcedatapackloader.content.worldgen.ContentCaveRegions;
import mctmods.resourcedatapackloader.content.worldgen.ContentCaveStructureFeature;
import mctmods.resourcedatapackloader.content.worldgen.ContentCity;
import mctmods.resourcedatapackloader.content.worldgen.ContentCityBulbPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentCityCapPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentCityDeckPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentCityDecorPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentCityPlazaPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentCitySewerHatchPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentCitySewerLoopPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentCitySewerPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentCityStationPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentCityStairsPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentCityStampPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentCityFarmPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentCityIntersectPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentCityLampPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentCityPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentCityPierPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentCityPlotPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentCityRailPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentCityStructure;
import mctmods.resourcedatapackloader.content.worldgen.ContentCityWellPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentPlotPoolElement;
import mctmods.resourcedatapackloader.content.worldgen.ContentCoverFeature;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.content.worldgen.ContentFlatSource;
import mctmods.resourcedatapackloader.content.worldgen.ContentGeneratorControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentChunkTokens;
import mctmods.resourcedatapackloader.content.worldgen.ContentOreControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentShapeFeature;
import mctmods.resourcedatapackloader.content.worldgen.ContentMapPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentMapStructure;
import mctmods.resourcedatapackloader.content.worldgen.ContentSpreadPlacement;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureMaps;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureRings;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSpread;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldgen;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldShape;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public final class ContentEvents {
    private ContentEvents() {}

    public static void onRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.FEATURE)) {
            event.register(Registries.FEATURE, helper -> {
                helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentWorldgen.SHAPE_FEATURE), ContentShapeFeature.INSTANCE);
                helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentCaveRegions.COVER_FEATURE), ContentCoverFeature.INSTANCE);
                helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentCaveRegions.STRUCTURE_FEATURE), ContentCaveStructureFeature.INSTANCE);
            });
        }
        else if (event.getRegistryKey().equals(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS)) { event.register(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, helper -> {
                helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentOreControl.ID), ContentOreControl.CODEC);
                helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentEntitySpawns.ID), ContentEntitySpawns.CODEC);
            });
        }
        else if (event.getRegistryKey().equals(Registries.CHUNK_GENERATOR)) { event.register(Registries.CHUNK_GENERATOR, helper -> helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentFlatSource.ID), ContentFlatSource.CODEC)); }
        else if (event.getRegistryKey().equals(Registries.STRUCTURE_TYPE)) { event.register(Registries.STRUCTURE_TYPE, helper -> {
            helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentStructureMaps.MAP_STRUCTURE), ContentMapStructure.TYPE);
            helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentCity.STRUCTURE), ContentCityStructure.TYPE);
        }); }
        else if (event.getRegistryKey().equals(Registries.STRUCTURE_POOL_ELEMENT)) { event.register(Registries.STRUCTURE_POOL_ELEMENT, helper -> helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentCity.STRUCTURE + "_plot_element"), ContentPlotPoolElement.TYPE)); }
        else if (event.getRegistryKey().equals(Registries.STRUCTURE_PIECE)) { event.register(Registries.STRUCTURE_PIECE, helper -> {
            helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentStructureMaps.MAP_PIECE), ContentMapPiece.TYPE);
            helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentCity.STRUCTURE + "_piece"), ContentCityPiece.TYPE);
            helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentCity.STRUCTURE + "_plot"), ContentCityPlotPiece.TYPE);
            helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentCity.STRUCTURE + "_deck"), ContentCityDeckPiece.TYPE);
            helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentCity.STRUCTURE + "_lamp"), ContentCityLampPiece.TYPE);
            helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentCity.STRUCTURE + "_rail"), ContentCityRailPiece.TYPE);
            helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentCity.STRUCTURE + "_farm"), ContentCityFarmPiece.TYPE);
            helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentCity.STRUCTURE + "_bulb"), ContentCityBulbPiece.TYPE);
            helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentCity.STRUCTURE + "_cap"), ContentCityCapPiece.TYPE);
            helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentCity.STRUCTURE + "_pier"), ContentCityPierPiece.TYPE);
            helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentCity.STRUCTURE + "_intersect"), ContentCityIntersectPiece.TYPE);
            helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentCity.STRUCTURE + "_decor"), ContentCityDecorPiece.TYPE);
            helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentCity.STRUCTURE + "_sewer"), ContentCitySewerPiece.TYPE);
            helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentCity.STRUCTURE + "_sewer_loop"), ContentCitySewerLoopPiece.TYPE);
            helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentCity.STRUCTURE + "_sewer_hatch"), ContentCitySewerHatchPiece.TYPE);
            helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentCity.STRUCTURE + "_plaza"), ContentCityPlazaPiece.TYPE);
            helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentCity.STRUCTURE + "_well"), ContentCityWellPiece.TYPE);
            helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentCity.STRUCTURE + "_station"), ContentCityStationPiece.TYPE);
            helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentCity.STRUCTURE + "_stairs"), ContentCityStairsPiece.TYPE);
            helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentCity.STRUCTURE + "_stamp"), ContentCityStampPiece.TYPE);
        }); }
        else if (event.getRegistryKey().equals(Registries.STRUCTURE_PLACEMENT)) {
            event.register(Registries.STRUCTURE_PLACEMENT, helper -> {
                helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentWorldgen.SPREAD_PLACEMENT), ContentStructureSpread.TYPE);
                helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentWorldgen.RINGS_PLACEMENT), ContentStructureRings.TYPE);
            });
        }
        else if (event.getRegistryKey().equals(Registries.PLACEMENT_MODIFIER_TYPE)) { event.register(Registries.PLACEMENT_MODIFIER_TYPE, helper -> helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentWorldgen.SPREAD_PLACEMENT), ContentSpreadPlacement.TYPE)); }
        else if (event.getRegistryKey().equals(NeoForgeRegistries.Keys.ATTACHMENT_TYPES)) { event.register(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, helper -> helper.register(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentWorldgen.RETROGEN_TOKENS), ContentChunkTokens.type())); }
        if (Config.contentOff()) {
            if (event.getRegistryKey().equals(Registries.ITEM)) { event.register(Registries.ITEM, helper -> generateData()); }
            return;
        }
        ContentFluids.prepare();
        if (event.getRegistryKey().equals(Registries.ARMOR_MATERIAL)) { event.register(Registries.ARMOR_MATERIAL, helper -> ContentMaterials.registerArmor(helper::register)); }
        else if (event.getRegistryKey().equals(NeoForgeRegistries.Keys.FLUID_TYPES)) { event.register(NeoForgeRegistries.Keys.FLUID_TYPES, helper -> ContentFluids.registerTypes(helper::register)); }
        else if (event.getRegistryKey().equals(Registries.FLUID)) { event.register(Registries.FLUID, helper -> ContentFluids.registerFluids(helper::register)); }
        else if (event.getRegistryKey().equals(Registries.BLOCK)) { event.register(Registries.BLOCK, ContentEvents::registerBlocks); }
        else if (event.getRegistryKey().equals(Registries.ITEM)) { event.register(Registries.ITEM, ContentEvents::registerItems); }
        else if (event.getRegistryKey().equals(Registries.CREATIVE_MODE_TAB)) { event.register(Registries.CREATIVE_MODE_TAB, ContentEvents::registerTabs); }
        else if (event.getRegistryKey().equals(Registries.ENTITY_TYPE)) { event.register(Registries.ENTITY_TYPE, ContentEntityTypes::registerTypes); }
        else if (event.getRegistryKey().equals(Registries.BLOCK_ENTITY_TYPE)) {
            event.register(Registries.BLOCK_ENTITY_TYPE, ContentBanners::register);
            event.register(Registries.BLOCK_ENTITY_TYPE, ContentContainers::register);
            event.register(Registries.BLOCK_ENTITY_TYPE, ContentBells::register);
        }
        else if (event.getRegistryKey().equals(Registries.MENU)) { event.register(Registries.MENU, ContentContainers::registerMenu); }
        else if (event.getRegistryKey().equals(Registries.SOUND_EVENT)) { event.register(Registries.SOUND_EVENT, ContentSounds::register); }
        else if (event.getRegistryKey().equals(Registries.MOB_EFFECT)) { event.register(Registries.MOB_EFFECT, ContentPotions::registerPotions); }
        else if (event.getRegistryKey().equals(Registries.POTION)) { event.register(Registries.POTION, ContentPotions::registerTypes); }
        else if (event.getRegistryKey().equals(Registries.POINT_OF_INTEREST_TYPE)) { event.register(Registries.POINT_OF_INTEREST_TYPE, ContentVillagers::registerJobSites); }
        else if (event.getRegistryKey().equals(Registries.VILLAGER_PROFESSION)) { event.register(Registries.VILLAGER_PROFESSION, ContentVillagers::registerProfessions); }
    }

    private static void registerBlocks(RegisterEvent.RegisterHelper<Block> helper) {
        int count = registerBlocks(helper, false) + registerBlocks(helper, true);
        ContentFluids.registerBlocks((id, block) -> {
            if (BuiltInRegistries.BLOCK.containsKey(id)) {
                ContentLog.LOGGER.warn("A block named {} is already registered, skipping the pack fluid block", id);
                return;
            }
            helper.register(id, block);
        });
        if (count > 0) { Summary.info("content.blocks", "Registered " + count + " block(s) from packs"); }
    }

    private static int registerBlocks(RegisterEvent.RegisterHelper<Block> helper, boolean modeled) {
        int count = 0;
        for (BlockDef def : ContentRegistry.blockDefs()) {
            if (ContentBlockTypes.borrowsModel(def.type()) != modeled || !ContentRegistry.available(def.requires(), def.key())) { continue; }
            for (BlockVariant variant : def.variants()) {
                if (BuiltInRegistries.BLOCK.containsKey(variant.id())) {
                    ContentLog.LOGGER.warn("A block named {} is already registered, skipping the pack definition", variant.id());
                    continue;
                }
                for (ContentBlockTypes.Created made : ContentBlockTypes.create(def, variant)) {
                    if (BuiltInRegistries.BLOCK.containsKey(made.id())) {
                        ContentLog.LOGGER.warn("A block named {} is already registered, skipping the pack definition", made.id());
                        continue;
                    }
                    helper.register(made.id(), made.block());
                    ContentRegistry.addBlock(made.id(), made.block(), def, variant, made.role());
                    count++;
                }
            }
        }
        return count;
    }

    private static void registerItems(RegisterEvent.RegisterHelper<Item> helper) {
        int count = 0;
        ContentEntityTypes.registerEggs(helper);
        for (ContentRegistry.BlockEntry entry : new ArrayList<>(ContentRegistry.blocks())) {
            Item item = ContentBlockTypes.item(entry);
            if (item == null || BuiltInRegistries.ITEM.containsKey(entry.id())) { continue; }
            helper.register(entry.id(), item);
            ContentRegistry.addItem(entry.id(), item, null, null, entry, entry.def().creativeTab());
            count++;
        }
        for (ItemDef def : ContentRegistry.itemDefs()) {
            if (!ContentRegistry.available(def.requires(), def.key())) { continue; }
            for (ItemVariant variant : def.variants()) {
                if (BuiltInRegistries.ITEM.containsKey(variant.id())) {
                    ContentLog.LOGGER.warn("An item named {} is already registered, skipping the pack definition", variant.id());
                    continue;
                }
                Item item = ContentItemTypes.create(def, variant);
                if (item == null) { continue; }
                helper.register(variant.id(), item);
                ContentRegistry.addItem(variant.id(), item, def, variant, null, def.creativeTab());
                count++;
            }
        }
        ContentFluids.registerItems((id, item) -> {
            if (BuiltInRegistries.ITEM.containsKey(id)) {
                ContentLog.LOGGER.warn("An item named {} is already registered, skipping the pack bucket", id);
                return;
            }
            helper.register(id, item);
            for (ContentFluids.Made made : ContentFluids.made()) {
                if (made.bucket == item) { ContentRegistry.addItem(id, item, null, null, null, made.def.creativeTab()); }
            }
        });
        resolveSoils();
        if (count > 0) { Summary.info("content.items", "Registered " + count + " item(s) from packs"); }
        generateData();
    }

    private static void generateData() {
        ContentGenerated.generate();
        ContentEntityTypes.generate();
        ContentExposures.generate();
        ContentBiomes.generate();
        ContentCaveRegions.generate();
        ContentOreControl.generate();
        ContentGeneratorControl.load();
        ContentStructureControl.generate();
        ContentStructureMaps.generate();
        ContentCity.generate();
        ContentWorldShape.generate();
        ContentDimensions.generate();
        ContentWorldgen.generate();
    }

    private static void resolveSoils() {
        for (ContentRegistry.BlockEntry entry : ContentRegistry.blocks()) {
            Block block = entry.block();
            if (block instanceof ContentBushBlock bush) { bush.resolveSoil(); }
            else if (block instanceof ContentCaneBlock cane) { cane.resolveSoil(); }
            else if (block instanceof ContentSaplingBlock sapling) { sapling.resolveSoil(); }
            else if (block instanceof ContentCropBlock crop) { crop.resolveSoil(cropSoil(entry)); }
        }
    }

    private static List<String> cropSoil(ContentRegistry.BlockEntry crop) {
        if (crop.def().growth() != null && !crop.def().growth().soil().isEmpty()) { return crop.def().growth().soil(); }
        List<String> soil = new ArrayList<>();
        for (ItemDef def : ContentRegistry.itemDefs()) {
            if (!ContentItemTypes.SEED.equals(def.type()) || !crop.id().toString().equals(def.crop()) || def.soil().isEmpty()) { continue; }
            soil.add(def.soil());
        }
        return soil;
    }

    private static void registerTabs(RegisterEvent.RegisterHelper<CreativeModeTab> helper) {
        int count = 0;
        for (TabDef def : ContentRegistry.tabDefs()) {
            if (!ContentRegistry.available(def.requires(), def.key())) { continue; }
            helper.register(ResourceKey.create(Registries.CREATIVE_MODE_TAB, def.id()), tab(def.id(), def.key(), def.icon()));
            count++;
        }
        Set<ResourceLocation> undeclared = new LinkedHashSet<>();
        for (ContentRegistry.ItemEntry entry : ContentRegistry.items()) {
            ResourceLocation named = entry.tab().isEmpty() ? null : ResourceLocation.tryParse(entry.tab());
            if (named == null || "minecraft".equals(named.getNamespace()) || ContentRegistry.tab(entry.tab()) != null || BuiltInRegistries.CREATIVE_MODE_TAB.containsKey(named)) { continue; }
            undeclared.add(named);
        }
        for (ResourceLocation named : undeclared) {
            helper.register(ResourceKey.create(Registries.CREATIVE_MODE_TAB, named), tab(named, named, ""));
            count++;
        }
        if (count > 0) { Summary.info("content.tabs", "Registered " + count + " creative tab(s) from packs"); }
    }

    private static CreativeModeTab tab(ResourceLocation id, ResourceLocation source, String declared) {
        return CreativeModeTab.builder()
                .title(Component.translatable("itemGroup." + id.getNamespace() + "." + id.getPath().replace('/', '.')))
                .icon(() -> icon(id, source, declared))
                .displayItems((parameters, out) -> {
                    for (ContentRegistry.ItemEntry entry : ContentRegistry.items()) {
                        if (id.equals(ResourceLocation.tryParse(entry.tab()))) { show(entry.item(), out::accept); }
                    }
                })
                .build();
    }

    private static void show(Item item, Consumer<ItemStack> out) {
        if (!(item instanceof ContentPotionItem bottle)) {
            out.accept(new ItemStack(item));
            return;
        }
        for (ItemStack stack : bottle.stacks()) { out.accept(stack.copy()); }
    }

    private static ItemStack icon(ResourceLocation id, ResourceLocation source, String named) {
        ItemStack declared = ContentStacks.parse(source, named, 1);
        if (!declared.isEmpty()) { return declared; }
        for (ContentRegistry.ItemEntry entry : ContentRegistry.items()) {
            if (id.equals(ResourceLocation.tryParse(entry.tab()))) { return new ItemStack(entry.item()); }
        }
        return new ItemStack(Blocks.STONE);
    }

    public static void onBuildTab(BuildCreativeModeTabContentsEvent event) {
        ResourceLocation tab = event.getTabKey().location();
        if (ContentRegistry.tab(tab.toString()) != null) { return; }
        for (ContentRegistry.ItemEntry entry : ContentRegistry.items()) {
            if (tab.equals(ResourceLocation.tryParse(entry.tab()))) { show(entry.item(), event::accept); }
        }
    }
}
