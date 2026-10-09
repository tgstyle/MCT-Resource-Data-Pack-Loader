package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.GrowthDef;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.util.ContentCreativeTab;
import mctmods.resourcedatapackloader.content.util.ContentTabs;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IBlock;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registries;

import java.util.HashSet;
import java.util.Set;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.NonNullList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
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
    private static final Map<Block, int[]> FLAMMABLE = new LinkedHashMap<>();
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
        IBlock inside = (IBlock) block;
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
        block.setResistance(resistanceOf(def, first));
        if (first.light > 0) { block.setLightLevel(first.light / 15.0F); }
        flammable(block, def.fireSpread, def.flammability);
    }

    public static void flammable(Block block, int fireSpread, int flammability) { if (flammability > 0 || fireSpread > 0) { FLAMMABLE.put(block, new int[] {fireSpread, flammability}); } }

    public static float resistanceOf(BlockDef def, BlockVariant variant) { return variant.resistance / def.explosionResistanceDivisor; }

    public static void applyFire() {
        for (Map.Entry<Block, int[]> entry : FLAMMABLE.entrySet()) {
            Blocks.FIRE.setFireInfo(entry.getKey(), entry.getValue()[0], entry.getValue()[1]);
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

    public static Set<Block> resolveSoil(List<String> soil, ResourceLocation owner) {
        Set<Block> resolved = new HashSet<>();
        for (String name : soil) {
            Block block = Registries.find(ForgeRegistries.BLOCKS, new ResourceLocation(name));
            if (block != null) { resolved.add(block); }
            else { ContentLog.LOGGER.error("{} names soil {}, which is not registered, leaving it out", owner, name); }
        }
        return resolved;
    }

    public static boolean sustains(Set<Block> soil, World world, BlockPos ground, IPlantable plant) {
        IBlockState state = world.getBlockState(ground);
        if (soil.isEmpty()) { return state.getBlock().canSustainPlant(state, world, ground, EnumFacing.UP, plant); }
        return soil.contains(state.getBlock());
    }

    public static boolean placeable(BlockBush block, World world, BlockPos pos) { return world.getBlockState(pos).getBlock().isReplaceable(world, pos) && block.canBlockStay(world, pos, block.getDefaultState()); }

    public static boolean forwardEvent(World world, BlockPos pos, int id, int value) {
        TileEntity tile = world.getTileEntity(pos);
        return tile != null && tile.receiveClientEvent(id, value);
    }

    public static void growthDrops(Block block, BlockDef def, GrowthDef growth, NonNullList<ItemStack> drops) {
        if (growth.drop.isEmpty()) {
            drops.add(new ItemStack(block, growth.dropCount));
            return;
        }
        ItemStack stack = ContentStacks.parse(def.registryName, growth.drop, growth.dropCount);
        if (!stack.isEmpty()) { drops.add(stack); }
    }
}
