package mctmods.resourcedatapackloader.common.worldgen.feature.tree;

import mctmods.resourcedatapackloader.common.util.world.TemplateBlocks;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.common.worldgen.feature.shape.ContentImprint;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;

public final class TreeTemplates {
    private static final Map<String, Built> CACHE = new HashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private TreeTemplates() {}

    public static final class Built {
        public final Map<BlockPos, IBlockState> blocks;
        public final Map<BlockPos, NBTTagCompound> blockEntities;
        public final int hash;
        public final Rotation rotation;
        public final Mirror mirror;
        public final boolean overBounds;
        @Nullable public final TreeShape shape;

        private Built(Map<BlockPos, IBlockState> blocks, Map<BlockPos, NBTTagCompound> blockEntities, int hash, Rotation rotation, Mirror mirror, boolean overBounds, @Nullable TreeShape shape) {
            this.blocks = blocks;
            this.blockEntities = blockEntities;
            this.hash = hash;
            this.rotation = rotation;
            this.mirror = mirror;
            this.overBounds = overBounds;
            this.shape = shape;
        }
    }

    @Nullable public static Built get(WorldServer world, String name, Rotation rotation, Mirror mirror, int steps) {
        if (GENERATION.stale()) { CACHE.clear(); }
        String key = name + "|" + rotation + "|" + mirror + "|" + steps;
        Built cached = CACHE.get(key);
        if (cached != null) { return cached; }
        Template template = ContentImprint.template(world, name);
        if (template == null) { return null; }
        Built built = build(template, rotation, mirror, steps);
        CACHE.put(key, built);
        return built;
    }

    private static Built build(Template template, Rotation rotation, Mirror mirror, int steps) {
        BlockPos size = template.getSize();
        int cx = size.getX() / 2;
        int cz = size.getZ() / 2;
        PlacementSettings turn = new PlacementSettings().setMirror(mirror).setRotation(rotation);
        Map<BlockPos, IBlockState> blocks = new LinkedHashMap<>();
        Map<BlockPos, NBTTagCompound> blockEntities = new HashMap<>();
        int hash = 1;
        boolean over = false;
        for (TemplateBlocks.Entry entry : TemplateBlocks.blocks(template)) {
            IBlockState state = entry.state();
            Block block = state.getBlock();
            if (block == Blocks.AIR || block == Blocks.STRUCTURE_VOID) { continue; }
            BlockPos local = entry.pos;
            hash = 31 * hash + Long.hashCode(local.toLong());
            hash = 31 * hash + String.valueOf(block.getRegistryName()).hashCode();
            hash = 31 * hash + block.getMetaFromState(state);
            BlockPos offset = Template.transformedBlockPos(turn, new BlockPos(local.getX() - cx, local.getY(), local.getZ() - cz));
            if (TreeRecorder.outOfBounds(offset.getX(), offset.getY(), offset.getZ())) { over = true; }
            blocks.put(offset, state.withMirror(mirror).withRotation(rotation));
            if (entry.nbt != null) {
                NBTTagCompound data = entry.nbt.copy();
                data.removeTag("x");
                data.removeTag("y");
                data.removeTag("z");
                blockEntities.put(offset, data);
            }
        }
        boolean overBounds = over || blocks.size() > TreeRecorder.MAX_ENTRIES;
        TreeShape shape = overBounds ? null : new TreeShape(blocks, blockEntities, steps);
        return new Built(blocks, blockEntities, hash, rotation, mirror, overBounds, shape);
    }
}
