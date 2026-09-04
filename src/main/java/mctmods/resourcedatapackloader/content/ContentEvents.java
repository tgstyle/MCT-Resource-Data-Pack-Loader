package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.block.ContentBushBlock;
import mctmods.resourcedatapackloader.content.block.ContentCaneBlock;
import mctmods.resourcedatapackloader.content.block.ContentCropBlock;
import mctmods.resourcedatapackloader.content.block.ContentSaplingBlock;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.def.ItemVariant;
import mctmods.resourcedatapackloader.content.def.TabDef;
import mctmods.resourcedatapackloader.content.fluid.ContentFluids;
import mctmods.resourcedatapackloader.content.types.ContentBlockTypes;
import mctmods.resourcedatapackloader.content.types.ContentItemTypes;
import mctmods.resourcedatapackloader.content.util.ContentMaterials;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import java.util.ArrayList;
import java.util.List;

public final class ContentEvents {
    private ContentEvents() {}

    public static void onRegister(RegisterEvent event) {
        if (Config.contentOff()) { return; }
        ContentFluids.prepare();
        if (event.getRegistryKey().equals(Registries.ARMOR_MATERIAL)) { event.register(Registries.ARMOR_MATERIAL, helper -> ContentMaterials.registerArmor(helper::register)); }
        else if (event.getRegistryKey().equals(NeoForgeRegistries.Keys.FLUID_TYPES)) { event.register(NeoForgeRegistries.Keys.FLUID_TYPES, helper -> ContentFluids.registerTypes(helper::register)); }
        else if (event.getRegistryKey().equals(Registries.FLUID)) { event.register(Registries.FLUID, helper -> ContentFluids.registerFluids(helper::register)); }
        else if (event.getRegistryKey().equals(Registries.BLOCK)) { event.register(Registries.BLOCK, ContentEvents::registerBlocks); }
        else if (event.getRegistryKey().equals(Registries.ITEM)) { event.register(Registries.ITEM, ContentEvents::registerItems); }
        else if (event.getRegistryKey().equals(Registries.CREATIVE_MODE_TAB)) { event.register(Registries.CREATIVE_MODE_TAB, ContentEvents::registerTabs); }
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

    private static int registerBlocks(RegisterEvent.RegisterHelper<Block> helper, boolean stairs) {
        int count = 0;
        for (BlockDef def : ContentRegistry.blockDefs()) {
            if (ContentBlockTypes.STAIRS.equals(def.type()) != stairs || !ContentRegistry.available(def.requires(), def.key())) { continue; }
            for (BlockVariant variant : def.variants()) {
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
        ContentGenerated.generate();
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
        for (TabDef def : ContentRegistry.tabDefs()) {
            ResourceKey<CreativeModeTab> key = ResourceKey.create(Registries.CREATIVE_MODE_TAB, def.key());
            CreativeModeTab tab = CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + def.key().getNamespace() + "." + def.key().getPath().replace('/', '.')))
                    .icon(() -> icon(def))
                    .displayItems((parameters, out) -> {
                        for (ContentRegistry.ItemEntry entry : ContentRegistry.items()) {
                            if (def.key().equals(ResourceLocation.tryParse(entry.tab()))) { out.accept(entry.item()); }
                        }
                    })
                    .build();
            helper.register(key, tab);
        }
    }

    private static ItemStack icon(TabDef def) {
        ItemStack declared = ContentStacks.parse(def.key(), def.icon(), 1);
        if (!declared.isEmpty()) { return declared; }
        for (ContentRegistry.ItemEntry entry : ContentRegistry.items()) {
            if (def.key().equals(ResourceLocation.tryParse(entry.tab()))) { return new ItemStack(entry.item()); }
        }
        return new ItemStack(Blocks.STONE);
    }

    public static void onBuildTab(BuildCreativeModeTabContentsEvent event) {
        ResourceLocation tab = event.getTabKey().location();
        if (ContentRegistry.tab(tab.toString()) != null) { return; }
        for (ContentRegistry.ItemEntry entry : ContentRegistry.items()) {
            if (tab.equals(ResourceLocation.tryParse(entry.tab()))) { event.accept(entry.item()); }
        }
    }

    public static void onBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof ServerLevel level) { ContentDrops.release(level, event.getPos(), event.getState()); }
    }

    public static void onDetonate(ExplosionEvent.Detonate event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        for (BlockPos pos : event.getAffectedBlocks()) { ContentDrops.release(level, pos, level.getBlockState(pos)); }
    }
}
