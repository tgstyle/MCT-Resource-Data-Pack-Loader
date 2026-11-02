package mctmods.resourcedatapackloader.common.blocks.fluid;

import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public final class FluidTouch {
    private final List<Block> blocks = new ArrayList<>();
    private final List<BlockState> states = new ArrayList<>();
    private final List<Fluid> fluids = new ArrayList<>();
    private final List<TagKey<Block>> tags = new ArrayList<>();
    private final List<TagKey<Fluid>> fluidTags = new ArrayList<>();

    private FluidTouch() {}

    @Nullable public static FluidTouch parse(Object owner, List<String> entries) {
        FluidTouch touch = new FluidTouch();
        for (String entry : entries) { touch.add(entry.trim(), owner); }
        return touch.blocks.isEmpty() && touch.states.isEmpty() && touch.fluids.isEmpty() && touch.tags.isEmpty() ? null : touch;
    }

    @Nullable public static Fluid fluidOf(Block block) { return block instanceof LiquidBlock liquid ? liquid.fluid.getSource() : null; }

    public boolean matches(BlockState state) {
        Block block = state.getBlock();
        if (blocks.contains(block)) { return true; }
        for (BlockState wanted : states) {
            if (ContentStates.alike(wanted, state)) { return true; }
        }
        if (!fluids.isEmpty() && fluids.contains(fluidOf(block))) { return true; }
        for (TagKey<Block> tag : tags) {
            if (state.is(tag)) { return true; }
        }
        FluidState fluid = state.getFluidState();
        for (TagKey<Fluid> tag : fluidTags) {
            if (fluid.is(tag)) { return true; }
        }
        return false;
    }

    private void add(String entry, Object owner) {
        if (entry.isEmpty()) { return; }
        if (entry.startsWith("#")) {
            addTag(entry.substring(1).trim(), entry, owner);
            return;
        }
        if (entry.indexOf('[') >= 0 || entry.indexOf('{') >= 0) {
            BlockState state = ContentStates.parse(entry, owner);
            if (state != null) { states.add(state); }
            return;
        }
        Block block = ContentStates.registered(entry);
        if (block != null) {
            Fluid fluid = fluidOf(block);
            if (fluid != null) { fluids.add(fluid); }
            else { blocks.add(block); }
            return;
        }
        Fluid fluid = Registered.find(BuiltInRegistries.FLUID, ResourceLocation.tryParse(Settings.lower(entry)));
        if (fluid == null || fluid == Fluids.EMPTY) { ContentLog.LOGGER.error("{} touches {}, which is neither a block nor a fluid, so that entry is skipped", owner, entry); }
        else { fluids.add(fluid instanceof FlowingFluid flowing ? flowing.getSource() : fluid); }
    }

    private void addTag(String name, String entry, Object owner) {
        ResourceLocation id = ResourceLocation.tryParse(name);
        if (id == null) { ContentLog.LOGGER.error("{} touches {}, which is not a valid tag name, so that entry matches nothing", owner, entry); }
        else {
            tags.add(TagKey.create(Registries.BLOCK, id));
            fluidTags.add(TagKey.create(Registries.FLUID, id));
        }
    }
}
