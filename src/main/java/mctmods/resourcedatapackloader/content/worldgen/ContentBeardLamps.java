package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.village.CityGrowth;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardBlocks;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardKeep;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlots;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoads;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoadsDecks;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoadsEnds;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoadsGrade;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoadsTunnels;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardStations;
import mctmods.resourcedatapackloader.content.worldgen.beard.interfaces.IRoadLayout;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IStructureComponentBox;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Random;

public final class ContentBeardLamps {
    private static final int LAMP_GAP = 7;

    private ContentBeardLamps() {}

    static void lampPosts(StructureStart start, StructureComponent piece, World world, StructureBoundingBox box, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        if (CityGrowth.bulbWide(piece)) {
            bulbLamps(start, piece, world, box, clip, at);
            return;
        }
        boolean alongX = BeardPlots.roadAlongX(box);
        int from = alongX ? box.minX : box.minZ;
        int to = alongX ? box.maxX : box.maxZ;
        if (to - from < 4) { return; }
        Random rand = new Random(world.getSeed() ^ ((long) box.minX << 32) ^ box.minZ);
        List<Integer> along = new ArrayList<>();
        along.add(from);
        for (int step = from + LAMP_GAP + rand.nextInt(6); step < to - 1; step += LAMP_GAP + rand.nextInt(6)) { along.add(step); }
        along.add(to);
        for (StructureComponent other : start.getComponents()) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox met = other.getBoundingBox();
            if (!box.intersectsWith(met)) { continue; }
            int meeting = alongX ? Math.max(box.minX, met.minX) : Math.max(box.minZ, met.minZ);
            if (meeting >= from && meeting <= to && !along.contains(meeting)) { along.add(meeting); }
        }
        int span = (alongX ? box.maxZ - box.minZ : box.maxX - box.minX) + 1;
        int[] bands = BeardRoads.bandsOf(box);
        int off = bands[2] > 0 && span == bands[0] ? 0 : 1;
        BeardRoads.Grade grade = piece instanceof IRoadLayout ? ((IRoadLayout) piece).rdpl$layout() : null;
        if (grade == null) { grade = BeardRoadsGrade.roadProfile(world, piece, alongX, from, to, alongX ? box.minZ : box.minX, alongX ? box.maxZ : box.maxX, true); }
        int raised = 0;
        for (int spot : along) {
            for (int side = 0; side < 2; side++) {
                int row = curbRow(start, box, alongX, along, spot, side == 1, from, to);
                if (row == Integer.MIN_VALUE || BeardRoadsTunnels.tunnelAt(grade, row) || BeardRoadsDecks.frameAt(grade, row)) { continue; }
                int roadTop = grade == null ? Integer.MIN_VALUE : grade.at(row);
                int x = alongX ? row : (side == 0 ? box.minX - off : box.maxX + off);
                int z = alongX ? (side == 0 ? box.minZ - off : box.maxZ + off) : row;
                if (lampBlocked(world, start, piece, x, z)) { continue; }
                if (raise(start, piece, world, clip, at, x, z, box, roadTop)) {
                    raised++;
                    break;
                }
            }
        }
        if (raised > 0) { ContentLog.LOGGER.debug("Raised {} lamp post(s) along the road at {}, {}", raised, box.minX, box.minZ); }
    }

    private static int curbRow(StructureStart start, StructureBoundingBox box, boolean alongX, List<Integer> along, int spot, boolean high, int from, int to) {
        StructureBoundingBox opening = BeardStations.openingAt(start.getComponents(), box, alongX, spot, high);
        if (opening == null) { return spot; }
        int least = alongX ? opening.minX : opening.minZ;
        int most = alongX ? opening.maxX : opening.maxZ;
        int near = spot - least <= most - spot ? least : most;
        int far = near == least ? most : least;
        for (int row : new int[] { near, far }) {
            if (row < from || row > to || BeardStations.openingAt(start.getComponents(), box, alongX, row, high) != null) { continue; }
            boolean crowded = false;
            for (int other : along) {
                if (other != spot && Math.abs(other - row) < LAMP_GAP) {
                    crowded = true;
                    break;
                }
            }
            if (!crowded) {
                ContentLog.LOGGER.debug("Slid the lamp at row {} of the road at {}, {} to row {}, clear of the subway station opening at {}, {}", spot, box.minX, box.minZ, row, opening.minX, opening.minZ);
                return row;
            }
        }
        ContentLog.LOGGER.debug("Skipped the lamp at row {} of the road at {}, {}, which stands in the subway station opening at {}, {} with no room to slide along the curb", spot, box.minX, box.minZ, opening.minX, opening.minZ);
        return Integer.MIN_VALUE;
    }

    private static void bulbLamps(StructureStart start, StructureComponent piece, World world, StructureBoundingBox box, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        BeardRoadsEnds.Bulb bulb = BeardRoadsEnds.bulbAt(world, piece);
        if (bulb == null) { return; }
        int walk = BeardRoads.pathSidewalkWidth();
        int ring = walk > 0 ? bulb.r : bulb.r + 1;
        int spots = Math.max(1, (int) Math.round(2.0D * Math.PI * ring));
        Random rand = new Random(world.getSeed() ^ ((long) box.minX << 32) ^ box.minZ);
        int raised = 0;
        for (int step = 0; step + 4 < spots; step += 7 + rand.nextInt(6)) {
            double angle = 2.0D * Math.PI * step / spots;
            int x = Integer.MIN_VALUE;
            int z = Integer.MIN_VALUE;
            for (int back = 0; back <= 2 && x == Integer.MIN_VALUE; back++) {
                int tryX = bulb.cx + (int) Math.round((ring - back) * Math.cos(angle));
                int tryZ = bulb.cz + (int) Math.round((ring - back) * Math.sin(angle));
                if (walk > 0 && !bulb.pavedAt(tryX, tryZ)) { continue; }
                x = tryX;
                z = tryZ;
            }
            if (x == Integer.MIN_VALUE || bulb.throatAt(x, z)) { continue; }
            if (lampBlocked(world, start, piece, x, z)) { continue; }
            if (raise(start, piece, world, clip, at, x, z, box, bulb.level)) { raised++; }
        }
        if (raised > 0) { ContentLog.LOGGER.debug("Raised {} lamp post(s) around the cul-de-sac at {}, {}", raised, box.minX, box.minZ); }
    }

    private static boolean lampPost(World world, int x, int y, int z, StructureStart start, StructureComponent piece, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        if (lampStructure(world, x, y, z)) { return true; }
        ContentStates.Spec post = BeardRoads.pathSpec("villagePathLampBlock", Config.worldgen.villagePathLampBlock);
        if (post == null || post.state.getBlock() == Blocks.AIR) { return false; }
        int high = ContentBeard.lampHeight();
        Set<Long> cells = new HashSet<>();
        for (int step = 0; step < high; step++) {
            at.setPos(x, y + step, z);
            ContentStates.place(world, at.toImmutable(), post.state, post.tag);
            cells.add(BeardKeep.packed(x, y + step, z));
        }
        ContentStates.Spec head = BeardRoads.pathSpec("villagePathLampTopBlock", Config.worldgen.villagePathLampTopBlock);
        if (head != null && head.state.getBlock() != Blocks.AIR) {
            at.setPos(x, y + high, z);
            ContentStates.place(world, at.toImmutable(), head.state, head.tag);
            cells.add(BeardKeep.packed(x, y + high, z));
        }
        ContentStates.Spec side = BeardRoads.pathSpec("villagePathLampSideBlock", Config.worldgen.villagePathLampSideBlock);
        if (side != null && side.state.getBlock() != Blocks.AIR) {
            for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                at.setPos(x + facing.getXOffset(), y + high, z + facing.getZOffset());
                if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }
                if (world.getBlockState(at).getMaterial() != Material.AIR) { continue; }
                ContentStates.place(world, at.toImmutable(), ContentBeard.faced(side.state, facing), side.tag);
                cells.add(BeardKeep.packed(at.getX(), at.getY(), at.getZ()));
            }
        }
        BeardKeep.holdLamp(cells);
        return true;
    }

    private static boolean lampStructure(World world, int x, int y, int z) {
        String named = ContentControl.text(ContentControl.VILLAGES, "villagePathLampStructure", Config.worldgen.villagePathLampStructure);
        if (named.isEmpty() || !(world instanceof WorldServer)) { return false; }
        WorldServer server = (WorldServer) world;
        Template loaded = server.getStructureTemplateManager().get(server.getMinecraftServer(), new ResourceLocation(named));
        if (loaded == null) {
            ContentLog.LOGGER.error("villagePathLampStructure names '{}', which could not be loaded, so no lamp is placed", named);
            return false;
        }
        BlockPos span = loaded.getSize();
        BlockPos origin = new BlockPos(x - span.getX() / 2, y, z - span.getZ() / 2);
        BeardKeep.watchArea(world, new StructureBoundingBox(origin.getX(), origin.getY(), origin.getZ(), origin.getX() + span.getX() - 1, origin.getY() + span.getY() - 1, origin.getZ() + span.getZ() - 1), "the lamp");
        loaded.addBlocksToWorld(world, origin, new PlacementSettings().setIgnoreEntities(true), 2);
        BeardKeep.learnLamp(world);
        return true;
    }

    public static boolean beforeADoor(World world, StructureBoundingBox clip, BlockPos.MutableBlockPos at, int x, int bed, int z) {
        StructureBoundingBox reach = new StructureBoundingBox(clip.minX - 2, clip.minY, clip.minZ - 2, clip.maxX + 2, clip.maxY, clip.maxZ + 2);
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int y = bed; y <= bed + 3; y++) {
                    at.setPos(x + dx, y, z + dz);
                    if (!reach.isVecInside(at)) { continue; }
                    if (world.getBlockState(at).getBlock() instanceof BlockDoor) { return true; }
                    if (ContentBeard.doorwayAt(world, at, x + dx, y, z + dz)) { return true; }
                }
            }
        }
        return false;
    }

    private static boolean lampBlocked(World world, StructureStart start, StructureComponent piece, int x, int z) { return ContentStructureSearch.anyVillage(world, start, village -> inPlaza(village, x, z) || onPaving(village, piece, x, z)); }

    private static boolean inPlaza(StructureStart start, int x, int z) { return BeardPlots.insidePlaza(start.getComponents(), x, z); }

    private static boolean onPaving(StructureStart start, StructureComponent piece, int x, int z) {
        int reach = (BeardRoads.pathFullWidth() - 3) / 2;
        for (StructureComponent other : start.getComponents()) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox held = ((IStructureComponentBox) other).rdpl$box();
            if (held == null) { continue; }
            if (other == piece) {
                if (CityGrowth.bulbWide(other)) { continue; }
                if (x < held.minX || x > held.maxX || z < held.minZ || z > held.maxZ) { continue; }
                boolean alongX = BeardPlots.roadAlongX(held);
                int center = alongX ? (held.minZ + held.maxZ) / 2 : (held.minX + held.maxX) / 2;
                int span = (alongX ? held.maxZ - held.minZ : held.maxX - held.minX) + 1;
                int roadway = Math.min((span - 1) / 2, 1 + BeardRoads.pathExtraWidth() + BeardRoads.pathLineColumns());
                if (Math.abs((alongX ? z : x) - center) <= roadway) { return true; }
                continue;
            }
            if (x >= held.minX - reach && x <= held.maxX + reach && z >= held.minZ - reach && z <= held.maxZ + reach) { return true; }
        }
        return false;
    }

    private static boolean raise(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip, BlockPos.MutableBlockPos at, int x, int z, StructureBoundingBox box, int roadTop) {
        at.setPos(x, box.minY, z);
        if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { return false; }
        int bed = Integer.MIN_VALUE;
        for (int y = box.minY + 6; y >= box.minY - 3; y--) {
            at.setPos(x, y, z);
            if (!clip.isVecInside(at)) { continue; }
            Material material = world.getBlockState(at).getMaterial();
            if (material.isLiquid()) { return false; }
            if (!material.isSolid()) { continue; }
            bed = y;
            break;
        }
        if (bed == Integer.MIN_VALUE) { return false; }
        for (int y = bed - 1; y >= bed - 3; y--) {
            at.setPos(x, y, z);
            if (!world.getBlockState(at).getMaterial().isSolid()) { return false; }
        }
        int stood = bed;
        at.setPos(x, stood, z);
        if (roadTop != Integer.MIN_VALUE && roadTop != bed && BeardBlocks.terrainBlock(world.getBlockState(at).getBlock())) { bed = roadTop; }
        if (bed < stood) {
            for (int y = stood; y > bed; y--) {
                at.setPos(x, y, z);
                if (!clip.isVecInside(at) || BeardKeep.holds(x, y, z)) { return false; }
                if (!BeardBlocks.terrainBlock(world.getBlockState(at).getBlock())) { return false; }
                BeardBlocks.clearAt(world, at);
            }
        }
        while (bed > box.minY - 3 && world.getBlockState(at.setPos(x, bed, z)).getBlock() == Blocks.AIR) {
            IBlockState under = world.getBlockState(at.setPos(x, bed - 1, z));
            if (!under.getMaterial().isSolid() || BeardBlocks.terrainBlock(under.getBlock())) { break; }
            bed--;
        }
        int top = bed + ContentBeard.lampHeight() + 1;
        for (int y = bed + 1; y <= top; y++) {
            at.setPos(x, y, z);
            if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { return false; }
            if (BeardKeep.holds(x, y, z)) { return false; }
            if (!world.getBlockState(at).getMaterial().isReplaceable() && world.getBlockState(at).getMaterial() != Material.AIR) { return false; }
        }
        if (beforeADoor(world, clip, at, x, bed, z)) { return false; }
        at.setPos(x, bed, z);
        if (!world.getBlockState(at).getMaterial().isSolid()) { return false; }
        return lampPost(world, x, bed + 1, z, start, piece, clip, at);
    }
}
