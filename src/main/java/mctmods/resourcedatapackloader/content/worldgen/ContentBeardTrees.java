package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.blastplaster.util.TreeCollector;
import mctmods.resourcedatapackloader.content.village.CityGrowth;
import mctmods.resourcedatapackloader.content.village.ContentVillageDecor;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardBlocks;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardClearing;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlots;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.function.Predicate;

public final class ContentBeardTrees {

    private ContentBeardTrees() {}

    static int fellAround(World world, StructureStart start, StructureComponent piece, StructureBoundingBox box, StructureBoundingBox clip, BlockPos.MutableBlockPos at, boolean bare) {
        List<BlockPos> seeds = new ArrayList<>();
        List<BlockPos> canopy = new ArrayList<>();
        int felled = 0;
        int top = piece instanceof StructureVillagePieces.Well ? box.maxY + 1 : box.maxY;
        boolean bulb = piece instanceof StructureVillagePieces.Path && CityGrowth.bulbWide(piece);
        int reach = bulb ? 6 : 2;
        int floor = bulb ? box.minY - 8 : box.minY + 1;
        int leastX = Math.max(box.minX - reach, clip.minX);
        int mostX = Math.min(box.maxX + reach, clip.maxX);
        int leastZ = Math.max(box.minZ - reach, clip.minZ);
        int mostZ = Math.min(box.maxZ + reach, clip.maxZ);
        for (int x = leastX; x <= mostX; x++) {
            for (int z = leastZ; z <= mostZ; z++) {
                int from = !bare && !bulb && x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ ? Math.max(floor, top + 1) : floor;
                for (int y = from; y <= box.maxY + 16; y++) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at)) { continue; }
                    IBlockState held = world.getBlockState(at);
                    if (held.getBlock() == Blocks.AIR || BeardPlots.insideAnother(start, piece, at)) { continue; }
                    if (mctmods.blastplaster.util.BlastPlasterUtil.isTreeWood(held)) { if (!ContentVillageDecor.plantedAt(world, x, z)) { seeds.add(at.toImmutable()); } }
                    else if (held.getMaterial() == Material.LEAVES) { canopy.add(at.toImmutable()); }
                    else if (held.getMaterial() == Material.VINE) { felled += BeardBlocks.clearAt(world, at); }
                }
            }
        }
        Predicate<BlockPos> within = BeardPlots.outside(world, start, piece, box, !bare && !bulb, top);
        felled += fellTrees(world, seeds, within, at);
        for (BlockPos leaf : canopy) {
            IBlockState held = world.getBlockState(leaf);
            if (held.getMaterial() != Material.LEAVES) { continue; }
            if (held.getPropertyKeys().contains(BlockLeaves.DECAYABLE) && !held.getValue(BlockLeaves.DECAYABLE)) { continue; }
            if (sustained(world, leaf, within)) { continue; }
            at.setPos(leaf.getX(), leaf.getY(), leaf.getZ());
            felled += BeardBlocks.clearAt(world, at);
        }
        return felled;
    }

    public static int fellTrees(World world, List<BlockPos> seeds, Predicate<BlockPos> within, BlockPos.MutableBlockPos at) {
        int felled = 0;
        Set<BlockPos> felledLogs = new HashSet<>();
        for (BlockPos seed : seeds) {
            if (felledLogs.contains(seed)) { continue; }
            TreeCollector.Tree tree = TreeCollector.collect(world, seed, mctmods.blastplaster.Config.view(world).getMaxTreeSize(), within);
            for (BlockPos log : tree.logs) {
                felledLogs.add(log);
                at.setPos(log.getX(), log.getY(), log.getZ());
                felled += BeardBlocks.clearAt(world, at);
            }
            for (BlockPos leaf : tree.leaves) {
                at.setPos(leaf.getX(), leaf.getY(), leaf.getZ());
                felled += BeardBlocks.clearAt(world, at);
            }
            for (BlockPos log : tree.logs) {
                for (int x = log.getX() - 5; x <= log.getX() + 5; x++) {
                    for (int z = log.getZ() - 5; z <= log.getZ() + 5; z++) {
                        for (int y = log.getY() - 5; y <= log.getY() + 5; y++) {
                            at.setPos(x, y, z);
                            if (!within.test(at)) { continue; }
                            IBlockState held = world.getBlockState(at);
                            if (held.getMaterial() != Material.LEAVES) { continue; }
                            if (held.getPropertyKeys().contains(BlockLeaves.DECAYABLE) && !held.getValue(BlockLeaves.DECAYABLE)) { continue; }
                            if (sustained(world, at.toImmutable(), within)) { continue; }
                            felled += BeardBlocks.clearAt(world, at);
                        }
                    }
                }
            }
        }
        return felled;
    }

    private static boolean sustained(World world, BlockPos leaf, Predicate<BlockPos> within) { return BeardClearing.sustainer(world, leaf, within) != null; }
}
