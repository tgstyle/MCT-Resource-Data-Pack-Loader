package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentSetup;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ContentSoil {
    private final List<String> names;
    private final ResourceLocation owner;
    private Set<Block> blocks = new HashSet<>();

    public ContentSoil(List<String> names, ResourceLocation owner) {
        this.names = names;
        this.owner = owner;
    }

    public void resolve() { blocks = ContentSetup.resolveSoil(names, owner); }

    public Set<Block> blocks() { return blocks; }

    public boolean sustains(World world, BlockPos ground, IPlantable plant) { return ContentSetup.sustains(blocks, world, ground, plant); }
}
