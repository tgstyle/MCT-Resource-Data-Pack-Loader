package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.IPlantable;
import java.util.Collections;
import java.util.Set;

public final class ContentSoil {
    private final ResourceLocation owner;
    private Set<Block> blocks = Collections.emptySet();

    public ContentSoil(ResourceLocation owner) { this.owner = owner; }

    public void resolve(Iterable<String> names) { blocks = ContentRegistry.resolveSoil(names, owner); }

    public boolean isEmpty() { return blocks.isEmpty(); }

    public boolean contains(Block block) { return blocks.contains(block); }

    public boolean sustains(BlockGetter level, BlockPos ground, IPlantable plant) { return ContentRegistry.sustains(blocks, level, ground, plant); }
}
