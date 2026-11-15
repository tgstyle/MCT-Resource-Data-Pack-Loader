package mctmods.resourcedatapackloader.common.worldgen.feature.tree;

import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.common.worldgen.structure.ContentTreeProcessor;
import mctmods.resourcedatapackloader.core.util.LineCompat;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public final class TreeTemplates {
    private static final Map<String, Built> CACHE = new HashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private TreeTemplates() {}

    public static final class Built {
        public final Map<BlockPos, BlockState> blocks;
        public final Map<BlockPos, CompoundTag> blockEntities;
        public final int hash;
        public final Rotation rotation;
        public final Mirror mirror;
        public final boolean overBounds;
        @Nullable public final TreeShape shape;

        private Built(Map<BlockPos, BlockState> blocks, Map<BlockPos, CompoundTag> blockEntities, int hash, Rotation rotation, Mirror mirror, boolean overBounds, @Nullable TreeShape shape) {
            this.blocks = blocks;
            this.blockEntities = blockEntities;
            this.hash = hash;
            this.rotation = rotation;
            this.mirror = mirror;
            this.overBounds = overBounds;
            this.shape = shape;
        }
    }

    @Nullable public static Built get(ServerLevel level, String name, Rotation rotation, Mirror mirror, int steps) {
        if (GENERATION.stale()) { CACHE.clear(); }
        String key = name + "|" + rotation + "|" + mirror + "|" + steps;
        Built cached = CACHE.get(key);
        if (cached != null) { return cached; }
        Identifier named = Identifier.tryParse(name);
        Optional<StructureTemplate> template = named == null ? Optional.empty() : LineCompat.templates(level.getServer()).get(named);
        if (template.isEmpty()) { return null; }
        Built built = build(level, template.get(), rotation, mirror, steps);
        CACHE.put(key, built);
        return built;
    }

    private static boolean outOfBounds(BlockPos offset) { return TreeRecorder.outOfBounds(offset.getX(), offset.getY(), offset.getZ()); }

    private static Built build(ServerLevel level, StructureTemplate template, Rotation rotation, Mirror mirror, int steps) {
        Vec3i size = template.getSize();
        BlockPos center = new BlockPos(size.getX() / 2, 0, size.getZ() / 2);
        ContentTreeProcessor recorder = new ContentTreeProcessor();
        StructurePlaceSettings settings = new StructurePlaceSettings().setIgnoreEntities(true).addProcessor(recorder);
        template.placeInWorld(level, BlockPos.ZERO, BlockPos.ZERO, settings, level.getRandom(), 2);
        Map<BlockPos, BlockState> blocks = new LinkedHashMap<>();
        Map<BlockPos, CompoundTag> blockEntities = new HashMap<>();
        int hash = 1;
        boolean over = false;
        for (StructureTemplate.StructureBlockInfo info : recorder.recorded()) {
            BlockState state = info.state();
            Block block = state.getBlock();
            if (block == Blocks.AIR || block == Blocks.CAVE_AIR || block == Blocks.VOID_AIR || block == Blocks.STRUCTURE_VOID) { continue; }
            hash = 31 * hash + Long.hashCode(info.pos().asLong());
            hash = 31 * hash + NbtUtils.writeBlockState(state).toString().hashCode();
            BlockPos offset = StructureTemplate.transform(info.pos().subtract(center), mirror, rotation, BlockPos.ZERO);
            if (outOfBounds(offset)) { over = true; }
            blocks.put(offset, state.mirror(mirror).rotate(level, offset, rotation));
            if (info.nbt() != null) {
                CompoundTag data = info.nbt().copy();
                data.remove("x");
                data.remove("y");
                data.remove("z");
                blockEntities.put(offset, data);
            }
        }
        boolean overBounds = over || blocks.size() > TreeRecorder.MAX_ENTRIES;
        TreeShape shape = overBounds ? null : new TreeShape(blocks, blockEntities, steps);
        return new Built(blocks, blockEntities, hash, rotation, mirror, overBounds, shape);
    }
}
