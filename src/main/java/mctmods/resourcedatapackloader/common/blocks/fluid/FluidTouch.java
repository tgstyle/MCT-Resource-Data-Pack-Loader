package mctmods.resourcedatapackloader.common.blocks.fluid;

import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.pack.io.port.Ids;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

public final class FluidTouch {
    private final List<Block> blocks = new ArrayList<>();
    private final List<IBlockState> states = new ArrayList<>();
    private final List<Fluid> fluids = new ArrayList<>();
    private final List<String> ores = new ArrayList<>();

    private FluidTouch() {}

    @Nullable public static FluidTouch parse(Object owner, List<String> entries) {
        FluidTouch touch = new FluidTouch();
        for (String entry : entries) { touch.add(entry.trim(), owner); }
        return touch.blocks.isEmpty() && touch.states.isEmpty() && touch.fluids.isEmpty() && touch.ores.isEmpty() ? null : touch;
    }

    @Nullable public static Fluid fluidOf(Block block) {
        if (block instanceof IFluidBlock) { return ((IFluidBlock) block).getFluid(); }
        if (!(block instanceof BlockLiquid)) { return null; }
        Material material = block.getDefaultState().getMaterial();
        return material == Material.WATER ? FluidRegistry.WATER : material == Material.LAVA ? FluidRegistry.LAVA : null;
    }

    public boolean matches(IBlockState state) {
        Block block = state.getBlock();
        if (blocks.contains(block)) { return true; }
        for (IBlockState wanted : states) {
            if (ContentStates.alike(wanted, state)) { return true; }
        }
        if (!fluids.isEmpty() && fluids.contains(fluidOf(block))) { return true; }
        return !ores.isEmpty() && ore(state);
    }

    private boolean ore(IBlockState state) {
        Block block = state.getBlock();
        Item item = Item.getItemFromBlock(block);
        if (item == Items.AIR) { return false; }
        ItemStack stack = new ItemStack(item, 1, block.damageDropped(state));
        for (String ore : ores) {
            for (ItemStack member : OreDictionary.getOres(ore, false)) {
                if (OreDictionary.itemMatches(member, stack, false)) { return true; }
            }
        }
        return false;
    }

    private void add(String entry, Object owner) {
        if (entry.isEmpty()) { return; }
        if (entry.startsWith("#")) {
            addOre(entry, owner);
            return;
        }
        if (entry.split(":").length >= 3) {
            IBlockState state = ContentStates.parse(entry, owner);
            if (state != null) { states.add(state); }
            return;
        }
        ResourceLocation id = new ResourceLocation(entry);
        if (ForgeRegistries.BLOCKS.containsKey(id)) {
            Block block = ForgeRegistries.BLOCKS.getValue(id);
            Fluid fluid = block == null ? null : fluidOf(block);
            if (fluid != null) { fluids.add(fluid); }
            else if (block != null) { blocks.add(block); }
            return;
        }
        Fluid fluid = FluidRegistry.getFluid(id.getPath());
        if (fluid == null) { ContentLog.LOGGER.error("{} touches {}, which is neither a block nor a fluid, so that entry is skipped", owner, entry); }
        else { fluids.add(fluid); }
    }

    private void addOre(String entry, Object owner) {
        String ore = entry.indexOf(':') < 0 ? entry.substring(1).trim() : Ids.oreName(entry);
        if (ore == null) {
            ContentLog.LOGGER.error("{} touches {}, which has no ore dictionary name on this version, so that entry matches nothing", owner, entry);
            return;
        }
        if (!OreDictionary.doesOreNameExist(ore)) { ContentLog.LOGGER.error("{} touches {}, whose ore dictionary name {} has no members, so that entry matches nothing until some are added", owner, entry, ore); }
        ores.add(ore);
    }
}
