package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.interfaces.IContentSoil;
import mctmods.resourcedatapackloader.content.extra.ContentPotions;
import mctmods.resourcedatapackloader.content.extra.ContentSounds;
import mctmods.resourcedatapackloader.content.extra.ContentVillagers;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.entity.ContentEntityTypes;
import mctmods.resourcedatapackloader.content.entity.ContentEntitySpawns;
import mctmods.resourcedatapackloader.content.entity.EntityStorage;
import mctmods.resourcedatapackloader.content.item.ContentPotionItem;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.def.ItemVariant;
import mctmods.resourcedatapackloader.content.def.TabDef;
import mctmods.resourcedatapackloader.content.block.ContentFluids;
import mctmods.resourcedatapackloader.content.types.ContentBlockTypes;
import mctmods.resourcedatapackloader.content.types.ContentItemTypes;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomes;
import mctmods.resourcedatapackloader.content.worldgen.ContentCaveRegions;
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
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.content.worldgen.ContentFeatureTypes;
import mctmods.resourcedatapackloader.content.worldgen.ContentFlatSource;
import mctmods.resourcedatapackloader.content.worldgen.ContentGeneratorControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentListBiomes;
import mctmods.resourcedatapackloader.content.worldgen.ContentChunkTokens;
import mctmods.resourcedatapackloader.content.worldgen.ContentOreControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentMapPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentMapStructure;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureMaps;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldgen;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldShape;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
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
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public final class ContentEvents {
    private ContentEvents() {}

    private static Identifier id(String path) { return ResourceDataPackLoader.id(path); }

    public static void onRegister(RegisterEvent event) {
        ContentFeatureTypes.register(event);
        event.register(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, helper -> {
            helper.register(id(ContentOreControl.ID), ContentOreControl.CODEC);
            helper.register(id(ContentEntitySpawns.ID), ContentEntitySpawns.CODEC);
        });
        event.register(Registries.CHUNK_GENERATOR, helper -> helper.register(id(ContentFlatSource.ID), ContentFlatSource.CODEC));
        event.register(Registries.BIOME_SOURCE, helper -> helper.register(id(ContentListBiomes.ID), ContentListBiomes.CODEC));
        event.register(Registries.STRUCTURE_TYPE, helper -> {
            helper.register(id(ContentStructureMaps.MAP_STRUCTURE), ContentMapStructure.TYPE);
            helper.register(id(ContentCity.STRUCTURE), ContentCityStructure.TYPE);
        });
        event.register(Registries.STRUCTURE_POOL_ELEMENT, helper -> helper.register(id(ContentCity.STRUCTURE + "_plot_element"), ContentPlotPoolElement.TYPE));
        event.register(Registries.STRUCTURE_PIECE, helper -> {
            helper.register(id(ContentStructureMaps.MAP_PIECE), ContentMapPiece.TYPE);
            helper.register(id(ContentCity.STRUCTURE + "_piece"), ContentCityPiece.TYPE);
            helper.register(id(ContentCity.STRUCTURE + "_plot"), ContentCityPlotPiece.TYPE);
            helper.register(id(ContentCity.STRUCTURE + "_deck"), ContentCityDeckPiece.TYPE);
            helper.register(id(ContentCity.STRUCTURE + "_lamp"), ContentCityLampPiece.TYPE);
            helper.register(id(ContentCity.STRUCTURE + "_rail"), ContentCityRailPiece.TYPE);
            helper.register(id(ContentCity.STRUCTURE + "_farm"), ContentCityFarmPiece.TYPE);
            helper.register(id(ContentCity.STRUCTURE + "_bulb"), ContentCityBulbPiece.TYPE);
            helper.register(id(ContentCity.STRUCTURE + "_cap"), ContentCityCapPiece.TYPE);
            helper.register(id(ContentCity.STRUCTURE + "_pier"), ContentCityPierPiece.TYPE);
            helper.register(id(ContentCity.STRUCTURE + "_intersect"), ContentCityIntersectPiece.TYPE);
            helper.register(id(ContentCity.STRUCTURE + "_decor"), ContentCityDecorPiece.TYPE);
            helper.register(id(ContentCity.STRUCTURE + "_sewer"), ContentCitySewerPiece.TYPE);
            helper.register(id(ContentCity.STRUCTURE + "_sewer_loop"), ContentCitySewerLoopPiece.TYPE);
            helper.register(id(ContentCity.STRUCTURE + "_sewer_hatch"), ContentCitySewerHatchPiece.TYPE);
            helper.register(id(ContentCity.STRUCTURE + "_plaza"), ContentCityPlazaPiece.TYPE);
            helper.register(id(ContentCity.STRUCTURE + "_well"), ContentCityWellPiece.TYPE);
            helper.register(id(ContentCity.STRUCTURE + "_station"), ContentCityStationPiece.TYPE);
            helper.register(id(ContentCity.STRUCTURE + "_stairs"), ContentCityStairsPiece.TYPE);
            helper.register(id(ContentCity.STRUCTURE + "_stamp"), ContentCityStampPiece.TYPE);
        });
        event.register(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, helper -> {
            helper.register(id(ContentWorldgen.RETROGEN_TOKENS), ContentChunkTokens.type());
            helper.register(EntityStorage.KEY, EntityStorage.TYPE);
        });
        if (Config.contentOff()) {
            event.register(Registries.ITEM, _ -> generateData());
            return;
        }
        ContentFluids.prepare();
        event.register(NeoForgeRegistries.Keys.FLUID_TYPES, helper -> ContentFluids.registerTypes(helper::register));
        event.register(Registries.FLUID, helper -> ContentFluids.registerFluids(helper::register));
        event.register(Registries.BLOCK, ContentEvents::registerBlocks);
        event.register(Registries.ITEM, ContentEvents::registerItems);
        event.register(Registries.CREATIVE_MODE_TAB, ContentEvents::registerTabs);
        event.register(Registries.ENTITY_TYPE, ContentEntityTypes::registerTypes);
        event.register(Registries.BLOCK_ENTITY_TYPE, ContentBanners::register);
        event.register(Registries.BLOCK_ENTITY_TYPE, ContentContainers::register);
        event.register(Registries.BLOCK_ENTITY_TYPE, ContentBells::register);
        event.register(Registries.MENU, ContentContainers::registerMenu);
        event.register(Registries.SOUND_EVENT, ContentSounds::register);
        event.register(Registries.MOB_EFFECT, ContentPotions::registerPotions);
        event.register(Registries.POTION, ContentPotions::registerTypes);
        event.register(Registries.POINT_OF_INTEREST_TYPE, ContentVillagers::registerJobSites);
        event.register(Registries.VILLAGER_PROFESSION, ContentVillagers::registerProfessions);
    }

    private static void registerBlocks(RegisterEvent.RegisterHelper<Block> helper) {
        int count = registerBlocks(helper, false) + registerBlocks(helper, true);
        ContentFluids.registerBlocks((id, block) -> {
            if (registered(BuiltInRegistries.BLOCK.containsKey(id), "A block", id, "fluid block")) { return; }
            helper.register(id, block);
            if (block.getDef().flammability() > 0 || block.getDef().fireSpread() > 0) { ContentOverrides.flammable(block, block.getDef().fireSpread(), block.getDef().flammability()); }
        });
        if (count > 0) { Summary.info("content.blocks", "Registered " + count + " block(s) from packs"); }
    }

    private static boolean registered(boolean present, String what, Identifier id, String skipped) {
        if (present) { ContentLog.LOGGER.warn("{} named {} is already registered, skipping the pack {}", what, id, skipped); }
        return present;
    }

    private static int registerBlocks(RegisterEvent.RegisterHelper<Block> helper, boolean modeled) {
        int count = 0;
        for (BlockDef def : ContentRegistry.blockDefs()) {
            if (ContentBlockTypes.borrowsModel(def.type()) != modeled || !ContentRegistry.available(def.requires(), def.key())) { continue; }
            for (BlockVariant variant : def.variants()) {
                if (registered(BuiltInRegistries.BLOCK.containsKey(variant.id()), "A block", variant.id(), "definition")) { continue; }
                for (ContentBlockTypes.Created made : ContentBlockTypes.create(def, variant)) {
                    if (registered(BuiltInRegistries.BLOCK.containsKey(made.id()), "A block", made.id(), "definition")) { continue; }
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
                if (registered(BuiltInRegistries.ITEM.containsKey(variant.id()), "An item", variant.id(), "definition")) { continue; }
                Item item = ContentItemTypes.create(def, variant);
                if (item == null) { continue; }
                helper.register(variant.id(), item);
                ContentRegistry.addItem(variant.id(), item, def, variant, null, def.creativeTab());
                count++;
            }
        }
        ContentFluids.registerItems((id, item) -> {
            if (registered(BuiltInRegistries.ITEM.containsKey(id), "An item", id, "bucket")) { return; }
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
            if (entry.block() instanceof IContentSoil soil) { soil.resolveSoil(entry); }
        }
    }

    private static void registerTabs(RegisterEvent.RegisterHelper<CreativeModeTab> helper) {
        int count = 0;
        for (TabDef def : ContentRegistry.tabDefs()) {
            if (!ContentRegistry.available(def.requires(), def.key())) { continue; }
            helper.register(ResourceKey.create(Registries.CREATIVE_MODE_TAB, def.id()), tab(def.id(), def.key(), def.icon()));
            count++;
        }
        Set<Identifier> undeclared = new LinkedHashSet<>();
        for (ContentRegistry.ItemEntry entry : ContentRegistry.items()) {
            Identifier named = entry.tab().isEmpty() ? null : Identifier.tryParse(entry.tab());
            if (named == null || "minecraft".equals(named.getNamespace()) || ContentRegistry.tab(entry.tab()) != null || BuiltInRegistries.CREATIVE_MODE_TAB.containsKey(named)) { continue; }
            undeclared.add(named);
        }
        for (Identifier named : undeclared) {
            helper.register(ResourceKey.create(Registries.CREATIVE_MODE_TAB, named), tab(named, named, ""));
            count++;
        }
        if (count > 0) { Summary.info("content.tabs", "Registered " + count + " creative tab(s) from packs"); }
    }

    private static CreativeModeTab tab(Identifier id, Identifier source, String declared) {
        return CreativeModeTab.builder()
                .title(Component.translatable("itemGroup." + id.getNamespace() + "." + id.getPath().replace('/', '.')))
                .icon(() -> icon(id, source, declared))
                .displayItems((_, out) -> {
                    for (ContentRegistry.ItemEntry entry : ContentRegistry.items()) {
                        if (inTab(entry, id)) { show(entry.item(), out::accept); }
                    }
                })
                .build();
    }

    private static boolean inTab(ContentRegistry.ItemEntry entry, Identifier tab) { return Objects.equals(tab, Identifier.tryParse(entry.tab())); }

    private static void show(Item item, Consumer<ItemStack> out) {
        if (!(item instanceof ContentPotionItem bottle)) {
            out.accept(new ItemStack(item));
            return;
        }
        for (ItemStack stack : bottle.stacks()) { out.accept(stack.copy()); }
    }

    private static ItemStack icon(Identifier id, Identifier source, String named) {
        ItemStack declared = ContentStacks.parse(source, named, 1);
        if (!declared.isEmpty()) { return declared; }
        for (ContentRegistry.ItemEntry entry : ContentRegistry.items()) {
            if (inTab(entry, id)) { return new ItemStack(entry.item()); }
        }
        return new ItemStack(Blocks.STONE);
    }

    public static void onBuildTab(BuildCreativeModeTabContentsEvent event) {
        Identifier tab = event.getTabKey().identifier();
        if (ContentRegistry.tab(tab.toString()) != null) { return; }
        for (ContentRegistry.ItemEntry entry : ContentRegistry.items()) {
            if (inTab(entry, tab)) { show(entry.item(), event::accept); }
        }
    }
}
