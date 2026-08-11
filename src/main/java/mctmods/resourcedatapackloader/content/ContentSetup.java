package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.mixin.AccessorBlock;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.util.ContentCreativeTab;
import mctmods.resourcedatapackloader.content.util.ContentTabs;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public final class ContentSetup {
    private static final Map<Block, BlockDef> FLAMMABLE = new LinkedHashMap<>();
    private static final Map<String, CreativeTabs> CREATED = new HashMap<>();

    private ContentSetup() {}

    public static List<String> names(BlockDef def) {
        List<String> names = new ArrayList<>(def.byMeta.length);
        for (BlockVariant value : def.byMeta) { names.add(value.name); }
        return names;
    }

    public static int metaOf(BlockDef def, String name) {
        for (int meta = 0; meta < def.byMeta.length; meta++) {
            if (def.byMeta[meta].name.equals(name)) { return meta; }
        }
        return 0;
    }

    public static void material(Block block, BlockDef def) {
        if (def.material == null) { return; }

        AccessorBlock inside = (AccessorBlock) block;
        inside.rdpl$setMaterial(def.material);
        inside.rdpl$setMapColor(def.material.getMaterialMapColor());
    }

    public static void apply(Block block, BlockDef def) {
        block.setRegistryName(def.registryName);
        block.setTranslationKey(def.registryName.toString());
        harvest(block, def);
        apply(block, def.creativeTab);
        properties(block, def);
    }

    public static void properties(Block block, BlockDef def) {
        BlockVariant first = def.at(0);
        block.setDefaultSlipperiness(def.slipperiness);
        block.setLightOpacity(def.lightOpacity);
        block.setHardness(first.hardness);
        block.setResistance(first.resistance / Math.max(0.01F, def.explosionResistanceDivisor));
        if (first.light > 0) { block.setLightLevel(first.light / 15.0F); }
        if (def.flammability > 0) { FLAMMABLE.put(block, def); }
    }

    public static void applyFire() {
        for (Map.Entry<Block, BlockDef> entry : FLAMMABLE.entrySet()) {
            Blocks.FIRE.setFireInfo(entry.getKey(), entry.getValue().fireSpread, entry.getValue().flammability);
        }
        FLAMMABLE.clear();
    }

    public static void harvest(Block block, BlockDef def) {
        if (def.harvestTool.isEmpty()) { return; }
        block.setHarvestLevel(def.harvestTool, def.harvestToolLevel);
    }

    public static void apply(Block block, String creativeTab) {
        CreativeTabs tab = tab(creativeTab, () -> new ItemStack(block));
        if (tab != null) { block.setCreativeTab(tab); }
    }

    public static void apply(Item item, String creativeTab) {
        CreativeTabs tab = tab(creativeTab, () -> new ItemStack(item));
        if (tab != null) { item.setCreativeTab(tab); }
    }

    private static Supplier<ItemStack> declared(String label, Supplier<ItemStack> fallback) {
        String declared = ContentTabs.icon(label);
        if (declared == null) { return fallback; }

        return () -> {
            ItemStack stack = ContentStacks.parse(ContentTabs.source(label), declared, 1);
            return stack.isEmpty() ? fallback.get() : stack;
        };
    }

    @Nullable public static CreativeTabs tab(String label, Supplier<ItemStack> icon) {
        if (label == null || label.isEmpty()) { return null; }
        if (FMLCommonHandler.instance().getSide() == Side.SERVER) { return null; }

        for (CreativeTabs tab : CreativeTabs.CREATIVE_TAB_ARRAY) {
            if (label.equals(tab.getTabLabel())) { return tab; }
        }
        return CREATED.computeIfAbsent(label, created -> new ContentCreativeTab(created, declared(created, icon)));
    }
}
