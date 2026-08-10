package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.village.ContentVillages;
import mctmods.resourcedatapackloader.content.def.PathIntersectDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentPathIntersects;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import java.util.List;
import javax.annotation.Nullable;

public final class BeardRoads {
    private BeardRoads() {}

    public static final class Grade {
        final int[] profile;
        final int[] ground;
        final boolean[] bridged;
        final int start;
        final int capped;
        Grade(int[] profile, int[] ground, boolean[] bridged, int start, int capped) {
            this.profile = profile;
            this.ground = ground;
            this.bridged = bridged;
            this.start = start;
            this.capped = capped;
        }

        public int at(int row) { return profile[Math.max(0, Math.min(profile.length - 1, row - start))]; }
    }

    @Nullable public static Grade roadProfile(World world, @Nullable StructureComponent piece, boolean alongX, int rowLeast, int rowMost, int acrossLeast, int acrossMost, boolean junctions) {
        int[] profile = BeardGrade.noiseProfile(world, alongX, rowLeast, rowMost, acrossLeast, acrossMost);
        if (profile == null) { return null; }

        int[] ground = profile.clone();
        BeardGrade.flatRuns(world, alongX, rowLeast, acrossLeast, acrossMost, profile);
        boolean[] bridged = BeardGrade.smooth(profile);
        boolean[] pinned = new boolean[profile.length];
        boolean[] plaza = new boolean[profile.length];
        int capped;
        if (junctions) {
            roadApron(world, piece, alongX, rowLeast, rowMost, acrossLeast, acrossMost, profile, pinned);
            clampToWell(world, alongX, rowLeast, acrossLeast, acrossMost, profile, plaza);
            for (int i = 0; i < pinned.length; i++) { if (plaza[i]) { pinned[i] = true; } }
            BeardGrade.settle(profile, pinned);
            boolean[] fixed = plaza.clone();
            capped = 0;
            for (int pass = 0; pass < 4; pass++) {
                int clamped = BeardGrade.capEmbankment(profile, ground, bridged, fixed);
                capped += clamped;
                if (clamped == 0) { break; }

                boolean[] hold = new boolean[profile.length];
                for (int i = 0; i < hold.length; i++) { hold[i] = fixed[i] || (pinned[i] && profile[i] <= ground[i] + BeardGrade.CAP); }
                BeardGrade.settle(profile, hold);
            }
        }
        else { capped = BeardGrade.capEmbankment(profile, ground, bridged, plaza); }
        return new Grade(profile, ground, bridged, rowLeast, capped);
    }

    public static int roadReach(StructureBoundingBox box, EnumFacing facing) {
        World world = ContentBeard.samplerWorld;
        if (world == null || facing == null || ContentBeard.samplerFor(world) == null) {
            if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The reach test for a road at {}, {} cannot run: world {}, facing {}, ContentBeard.sampler {}", box.minX, box.minZ, world == null ? "none" : "held", facing, world == null || ContentBeard.samplerFor(world) == null ? "none" : "held"); }
            return Integer.MAX_VALUE;
        }

        boolean alongX = facing.getAxis() == EnumFacing.Axis.X;
        int rows = (alongX ? box.maxX - box.minX : box.maxZ - box.minZ) + 1;
        int step = (alongX ? facing.getXOffset() : facing.getZOffset()) >= 0 ? 1 : -1;
        int from = step > 0 ? (alongX ? box.minX : box.minZ) : (alongX ? box.maxX : box.maxZ);
        int acrossLeast = alongX ? box.minZ : box.minX;
        int acrossMost = alongX ? box.maxZ : box.maxX;
        for (int length = rows; length >= 7; length -= 7) {
            int far = from + step * (length - 1);
            int rowLeast = Math.min(from, far);
            int rowMost = Math.max(from, far);
            Grade grade = roadProfile(world, null, alongX, rowLeast, rowMost, acrossLeast, acrossMost, true);
            if (grade == null) {
                if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The reach test for a road at {}, {} along {} has no profile at length {}, so its full {} rows stand", box.minX, box.minZ, alongX ? "x" : "z", length, rows); }
                return rows;
            }
            if (ContentLog.LOGGER.debugEnabled()) {
                StringBuilder trace = new StringBuilder();
                for (int i = 0; i < grade.profile.length; i++) {
                    trace.append(' ').append(rowLeast + i).append(':');
                    trace.append(grade.ground[i] == Integer.MIN_VALUE ? "-" : String.valueOf(grade.ground[i])).append('/');
                    trace.append(grade.profile[i] == Integer.MIN_VALUE ? "-" : String.valueOf(grade.profile[i]));
                    if (grade.bridged[i]) { trace.append('b'); }
                }
                ContentLog.LOGGER.debug("The reach test for a road at {}, {} along {} at length {} is {}, capped {} row(s), as row:ground/graded:{}", box.minX, box.minZ, alongX ? "x" : "z", length, BeardGrade.walkable(grade.profile, grade.bridged) ? "walkable" : "too steep", grade.capped, trace);
            }
            if (BeardGrade.walkable(grade.profile, grade.bridged)) { return length; }
        }
        return 0;
    }

    public static void pave(StructureComponent piece, World world, StructureBoundingBox clip, IBlockState path, IBlockState gravel, IBlockState planks, boolean chosenSurface) {
        StructureBoundingBox box = piece.getBoundingBox();
        boolean alongX = BeardPlots.roadAlongX(piece);
        int least = Math.max(alongX ? box.minX : box.minZ, alongX ? clip.minX : clip.minZ);
        int most = Math.min(alongX ? box.maxX : box.maxZ, alongX ? clip.maxX : clip.maxZ);
        if (most < least) { return; }

        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Paving the road at {}, {}, {} across, with surface {} (chosen={}), support {}, bridge {}", box.minX, box.minZ, (alongX ? box.maxZ - box.minZ : box.maxX - box.minX) + 1, path, chosenSurface, gravel, planks); }
        int acrossLeast = alongX ? box.minZ : box.minX;
        int acrossMost = alongX ? box.maxZ : box.maxX;
        int start = least;
        boolean computed = false;
        int[] ground;
        int[] profile;
        boolean[] bridged;
        int capped;
        Grade graded = roadProfile(world, piece, alongX, alongX ? box.minX : box.minZ, alongX ? box.maxX : box.maxZ, acrossLeast, acrossMost, true);
        if (graded != null) {
            start = alongX ? box.minX : box.minZ;
            computed = true;
            profile = graded.profile;
            ground = graded.ground;
            bridged = graded.bridged;
            capped = graded.capped;
        }
        else {
            int rows = most - least + 1;
            profile = new int[rows];
            for (int i = 0; i < rows; i++) {
                int found = Integer.MIN_VALUE;
                for (int across = acrossLeast; across <= acrossMost; across++) {
                    int x = alongX ? least + i : across;
                    int z = alongX ? across : least + i;
                    BlockPos spot = new BlockPos(x, 64, z);
                    if (!clip.isVecInside(spot)) { continue; }

                    BlockPos top = world.getTopSolidOrLiquidBlock(spot).down();
                    if (top.getY() < world.getSeaLevel() - 1 || world.getBlockState(top).getMaterial().isLiquid()) { continue; }
                    if (top.getY() > found) { found = top.getY(); }
                }
                profile[i] = found;
            }
            int before = roadAnchor(world, alongX, least - 1, acrossLeast, acrossMost, path, gravel);
            if (before != Integer.MIN_VALUE && profile[0] != Integer.MIN_VALUE) { profile[0] = Math.max(before - 1, Math.min(before + 1, profile[0])); }
            int after = roadAnchor(world, alongX, most + 1, acrossLeast, acrossMost, path, gravel);
            if (after != Integer.MIN_VALUE && profile[rows - 1] != Integer.MIN_VALUE) { profile[rows - 1] = Math.max(after - 1, Math.min(after + 1, profile[rows - 1])); }
            ground = profile.clone();
            bridged = BeardGrade.smooth(profile);
            capped = BeardGrade.capEmbankment(profile, ground, bridged, new boolean[profile.length]);
        }
        if (capped > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Capped {} row(s) of the road at {}, {} to {} block(s) above their own ground", capped, box.minX, box.minZ, BeardGrade.CAP); }
        if (ContentLog.LOGGER.debugEnabled()) {
            StringBuilder trace = new StringBuilder();
            for (int i = 0; i < profile.length; i++) {
                if (i > 0) { trace.append(' '); }
                trace.append(start + i).append(':');
                if (ground[i] == Integer.MIN_VALUE) { trace.append('-'); }
                else { trace.append(ground[i]); }
                trace.append('/');
                if (profile[i] == Integer.MIN_VALUE) { trace.append('-'); }
                else { trace.append(profile[i]); }
                if (bridged[i]) { trace.append('b'); }
            }
            ContentLog.LOGGER.debug("Profile of the road at {}, {} along {}, computed {}, as row:ground/graded, capped {} row(s) at {}: {}", box.minX, box.minZ, alongX ? "x" : "z", computed, capped, BeardGrade.CAP, trace);
        }
        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} grades from y {} to y {} along its length", box.minX, box.minZ, profile[0] == Integer.MIN_VALUE ? "water" : profile[0], profile[profile.length - 1] == Integer.MIN_VALUE ? "water" : profile[profile.length - 1]); }
        int cut = 0;
        int filled = 0;
        int paved = 0;
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int i = Math.max(0, least - start); i < profile.length && start + i <= most; i++) {
            for (int across = acrossLeast; across <= acrossMost; across++) {
                int x = alongX ? start + i : across;
                int z = alongX ? across : start + i;
                BlockPos spot = new BlockPos(x, 64, z);
                if (!clip.isVecInside(spot)) { continue; }

                BlockPos top = world.getTopSolidOrLiquidBlock(spot).down();
                if (top.getY() < world.getSeaLevel()) { top = new BlockPos(x, world.getSeaLevel() - 1, z); }
                if (profile[i] == Integer.MIN_VALUE) {
                    if (world.getBlockState(top).getMaterial().isLiquid()) { paved += deckBridge(world, alongX, start + i, across, acrossLeast, acrossMost, top.getY() + 1, planks, at); }
                    continue;
                }
                if (bridged[i]) {
                    paved += deckBridge(world, alongX, start + i, across, acrossLeast, acrossMost, profile[i], planks, at);
                    continue;
                }
                if (world.getBlockState(top).getMaterial().isLiquid()) {
                    paved += deckBridge(world, alongX, start + i, across, acrossLeast, acrossMost, top.getY() + 1, planks, at);
                    continue;
                }

                int target = profile[i];
                at.setPos(x, target, z);
                IBlockState held = world.getBlockState(at);
                Block base = held.getBlock();
                if (held.getMaterial().isSolid() && held.getMaterial() != Material.WOOD && held.getMaterial() != Material.LEAVES && !BeardBlocks.terrainBlock(base) && base != Blocks.GRASS_PATH && base != Blocks.PLANKS && base != Blocks.SANDSTONE && base != Blocks.RED_SANDSTONE && base != Blocks.HARDENED_CLAY && base != Blocks.STAINED_HARDENED_CLAY && base != Blocks.MYCELIUM) { continue; }

                for (int y = target + 1; y <= target + 4; y++) {
                    at.setPos(x, y, z);
                    IBlockState above = world.getBlockState(at);
                    Block up = above.getBlock();
                    if (up == Blocks.AIR) { continue; }
                    if (BeardKeep.holds(x, y, z)) { continue; }
                    if (!BeardBlocks.terrainBlock(up) && above.getMaterial().isSolid() && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Paving the road at {}, {} takes {} out of the air above it at {}, {}, {}", box.minX, box.minZ, up.getRegistryName(), x, y, z); }
                    if (above.getMaterial().isLiquid()) { break; }
                    if (BeardBlocks.terrainBlock(up) || up == Blocks.GRASS_PATH || up == Blocks.SANDSTONE || up == Blocks.MYCELIUM || above.getMaterial() == Material.WOOD || above.getMaterial() == Material.LEAVES || !above.getMaterial().isSolid()) {
                        world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
                        cut++;
                        continue;
                    }
                    break;
                }
                filled += BeardBlocks.fillUnder(world, at, x, z, target - 1, target - 8);
                at.setPos(x, target, z);
                boolean earthy = base == Blocks.GRASS || base == Blocks.DIRT || base == Blocks.MYCELIUM || base == Blocks.GRASS_PATH || base == Blocks.AIR || !world.getBlockState(at).getMaterial().isSolid();
                IBlockState natural = chosenSurface ? path : pathForGround(world, x, z, path, gravel, earthy);
                IBlockState dressed = dressSurface(world, piece, alongX, alongX ? x : z, alongX ? z : x, (acrossLeast + acrossMost) / 2, natural);
                if (BeardKeep.holds(x, target, z)) { continue; }

                world.setBlockState(at, dressed != null ? dressed : natural, 2);
                paved++;
            }
        }
        if ((cut + filled + paved > 0) && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Graded the road at {}, {} within its chunk: paved {} column(s), cut {} block(s) off bumps, filled {} into dips", box.minX, box.minZ, paved, cut, filled); }
    }

    public static int bridge(World world, StructureStart start, StructureComponent piece, StructureBoundingBox box, StructureBoundingBox near, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        if (near == null || Math.abs(box.minY - near.minY) > 2) { return 0; }

        int[] strip = ContentBeard.facingStrip(box, near, 6);
        if (strip == null) { return 0; }

        int fromX = strip[0];
        int toX = strip[1];
        int fromZ = strip[2];
        int toZ = strip[3];
        int cleared = 0;
        for (int x = fromX; x <= toX; x++) {
            for (int z = fromZ; z <= toZ; z++) {
                if (BeardPlots.underRoad(start, piece, x, z)) { continue; }

                int toBox = Math.max(0, Math.max(box.minX - x, x - box.maxX)) + Math.max(0, Math.max(box.minZ - z, z - box.maxZ));
                int toNear = Math.max(0, Math.max(near.minX - x, x - near.maxX)) + Math.max(0, Math.max(near.minZ - z, z - near.maxZ));
                int base = toBox <= toNear ? box.minY : near.minY;
                int bed = BeardGround.roadTop(world, start, at, x, z, base + 1, base + 12);
                for (int y = bed == Integer.MIN_VALUE ? base + 1 : bed + 1; y <= base + 12; y++) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }
                    IBlockState held = world.getBlockState(at);
                    if (BeardBlocks.opening(held.getMaterial()) || BeardBlocks.overhang(held)) { cleared += BeardBlocks.clearAt(world, at); }
                }
            }
        }
        return cleared;
    }

    public static int deckBridge(World world, boolean alongX, int row, int across, int acrossLeast, int acrossMost, int deckAt, IBlockState planks, BlockPos.MutableBlockPos at) {
        int deckY = deckAt;
        for (int lift = 0; lift < 8 && world.getBlockState(at.setPos(alongX ? row : across, deckY, alongX ? across : row)).getMaterial().isLiquid(); lift++) { deckY++; }
        at.setPos(alongX ? row : across, deckY, alongX ? across : row);
        if (world.getBlockState(at).getMaterial().isSolid()) { return 0; }

        int span = acrossMost - acrossLeast + 1;
        int laid = 0;
        IBlockState deck = planks;
        if (span > 3) {
            int center = (acrossLeast + acrossMost) / 2;
            int offset = Math.abs(across - center);
            if (offset > 1 + pathExtraWidth() + pathLineColumns()) {
                IBlockState walk = pathBlock("villagePathSidewalkBlock", Config.worldgen.villagePathSidewalkBlock, planks);
                deck = pathBlock("villagePathBridgeSidewalkBlock", Config.worldgen.villagePathBridgeSidewalkBlock, walk);
            }
            if (offset == (span - 1) / 2) {
                IBlockState barrier = pathBlock("villagePathBridgeBarrierBlock", Config.worldgen.villagePathBridgeBarrierBlock, planks);
                if (barrier != planks) {
                    int height = Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villagePathBridgeBarrierHeight", Config.worldgen.villagePathBridgeBarrierHeight));
                    for (int y = deckY + 1; y <= deckY + height; y++) {
                        at.setPos(alongX ? row : across, y, alongX ? across : row);
                        if (world.getBlockState(at).getMaterial().isSolid()) { break; }

                        world.setBlockState(at, barrier, 2);
                        laid++;
                    }
                    at.setPos(alongX ? row : across, deckY, alongX ? across : row);
                }
            }
        }
        if (BeardKeep.holds(at.getX(), at.getY(), at.getZ())) { return laid; }

        world.setBlockState(at, deck, 2);
        return laid + 1;
    }

    @Nullable public static IBlockState dressSurface(World world, StructureComponent piece, boolean alongX, int row, int across, int acrossCenter, IBlockState path) {
        int span = 3 + 2 * (pathExtraWidth() + pathLineColumns() + pathSidewalkWidth());
        StructureBoundingBox box = piece.getBoundingBox();
        if ((alongX ? box.maxZ - box.minZ : box.maxX - box.minX) + 1 != span || span == 3) { return null; }

        int offset = Math.abs(across - acrossCenter);
        int core = 1 + pathExtraWidth();
        char role = offset <= core ? (offset == 0 ? 'c' : 'r') : offset <= core + pathLineColumns() ? 'l' : 's';
        IBlockState stamped = stampAt(world, piece, alongX, row, across, acrossCenter, core, path);
        if (stamped != null) { return stamped; }
        if (role != 'r' && insideMouth(piece, alongX, row, across, acrossCenter)) { return path; }
        if (role == 'c') {
            IBlockState center = pathBlock("villagePathCenterBlock", Config.worldgen.villagePathCenterBlock, path);
            if (center == path) { return path; }

            int dash = Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathCenterDash", Config.worldgen.villagePathCenterDash));
            if (dash > 0 && Math.floorMod(row, dash + 1) == dash) { return path; }

            return ContentBeard.axised(center, alongX);
        }
        if (role == 'l') { return ContentBeard.axised(pathBlock("villagePathLineBlock", Config.worldgen.villagePathLineBlock, path), alongX); }
        if (role == 's') { return pathBlock("villagePathSidewalkBlock", Config.worldgen.villagePathSidewalkBlock, path); }
        return path;
    }

    public static boolean insideMouth(StructureComponent piece, boolean alongX, int row, int across, int acrossCenter) {
        List<StructureComponent> pieces = ContentBeard.components();
        if (pieces == null) { return false; }

        boolean outward = across > acrossCenter;
        for (StructureComponent other : pieces) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox road = other.getBoundingBox();
            boolean otherAlongX = BeardPlots.roadAlongX(road);
            if (otherAlongX == alongX) { continue; }

            int otherCore = 1 + pathExtraWidth();
            int otherCenter = alongX ? (road.minX + road.maxX) / 2 : (road.minZ + road.maxZ) / 2;
            if (row < otherCenter - otherCore || row > otherCenter + otherCore) { continue; }

            int edge = alongX ? (outward ? road.minZ : road.maxZ) : (outward ? road.minX : road.maxX);
            int gap = outward ? edge - across : across - edge;
            if (gap >= -1 && gap <= 2 + pathLineColumns() + pathSidewalkWidth()) { return true; }
        }
        return false;
    }

    @Nullable public static IBlockState stampAt(World world, StructureComponent piece, boolean alongX, int row, int across, int acrossCenter, int core, IBlockState path) {
        List<StructureComponent> pieces = ContentBeard.components();
        if (pieces == null || Math.abs(across - acrossCenter) > core) { return null; }

        for (StructureComponent other : pieces) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox road = other.getBoundingBox();
            boolean otherAlongX = BeardPlots.roadAlongX(road);
            if (otherAlongX == alongX) { continue; }

            int otherCenter = alongX ? (road.minX + road.maxX) / 2 : (road.minZ + road.maxZ) / 2;
            PathIntersectDef def = ContentPathIntersects.forJunction(world, alongX ? otherCenter : acrossCenter, alongX ? acrossCenter : otherCenter);
            if (def == null) { continue; }

            int otherCore = 1 + pathExtraWidth();
            int before = otherCenter - otherCore - 1;
            int after = otherCenter + otherCore + 1;
            IBlockState fromMouth = mouthCell(def, row, across, acrossCenter, core, before, after, path);
            if (fromMouth != null) { return fromMouth; }

            IBlockState fromCorner = cornerCell(def, row, across, acrossCenter, core, otherCenter, otherCore, path);
            if (fromCorner != null) { return fromCorner; }
        }
        return null;
    }

    @Nullable public static IBlockState mouthCell(PathIntersectDef def, int row, int across, int acrossCenter, int core, int before, int after, IBlockState path) {
        if (def.mouth.length == 0) { return null; }

        int line = -1;
        if (row <= before && row > before - def.mouth.length) { line = before - row; }
        if (row >= after && row < after + def.mouth.length) { line = row - after; }
        if (line < 0) { return null; }

        String cells = def.mouth[line];
        if (cells.isEmpty()) { return null; }

        return cellState(def, cells.charAt(Math.floorMod(across - (acrossCenter - core), cells.length())), path);
    }

    @Nullable public static IBlockState cornerCell(PathIntersectDef def, int row, int across, int acrossCenter, int core, int otherCenter, int otherCore, IBlockState path) {
        if (def.corner.length == 0 || row < otherCenter - otherCore || row > otherCenter + otherCore) { return null; }

        int fromRowEdge = Math.min(row - (otherCenter - otherCore), (otherCenter + otherCore) - row);
        int fromColEdge = core - Math.abs(across - acrossCenter);
        if (fromRowEdge >= def.corner.length) { return null; }

        String cells = def.corner[fromRowEdge];
        if (fromColEdge >= cells.length()) { return null; }

        return cellState(def, cells.charAt(fromColEdge), path);
    }

    @Nullable public static IBlockState cellState(PathIntersectDef def, char cell, IBlockState path) {
        if (cell == '.') { return null; }
        if (cell == 'r' || cell == 'c') { return path; }
        if (cell == 'l') { return pathBlock("villagePathLineBlock", Config.worldgen.villagePathLineBlock, path); }
        if (cell == 's') { return pathBlock("villagePathSidewalkBlock", Config.worldgen.villagePathSidewalkBlock, path); }
        return def.legend.getOrDefault(cell, path);
    }

    public static int chainGradeAt(World world, StructureComponent road, boolean alongX, int row) {
        StructureBoundingBox box = road.getBoundingBox();
        int least = alongX ? box.minX : box.minZ;
        int most = alongX ? box.maxX : box.maxZ;
        Grade grade = roadProfile(world, road, alongX, least, most, alongX ? box.minZ : box.minX, alongX ? box.maxZ : box.maxX, false);
        if (grade == null) { return Integer.MIN_VALUE; }

        return grade.at(row);
    }

    public static int roadGradeBeside(World world, StructureBoundingBox box) {
        List<StructureComponent> pieces = ContentBeard.components();
        if (pieces == null) { return Integer.MIN_VALUE; }

        for (StructureComponent other : pieces) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox road = other.getBoundingBox();
            int gap = Math.max(Math.max(road.minX - box.maxX, box.minX - road.maxX), Math.max(road.minZ - box.maxZ, box.minZ - road.maxZ));
            if (gap > 2) { continue; }

            boolean alongX = BeardPlots.roadAlongX(other);
            int start = alongX ? road.minX : road.minZ;
            Grade grade = roadProfile(world, other, alongX, start, alongX ? road.maxX : road.maxZ, alongX ? road.minZ : road.minX, alongX ? road.maxZ : road.maxX, true);
            if (grade == null) { return Integer.MIN_VALUE; }

            int center = alongX ? (box.minX + box.maxX) / 2 : (box.minZ + box.maxZ) / 2;
            int row = Math.max(start, Math.min(start + grade.profile.length - 1, center));
            if (grade.profile[row - start] == Integer.MIN_VALUE) { continue; }

            return grade.profile[row - start] + 1;
        }
        return Integer.MIN_VALUE;
    }

    public static void roadApron(World world, @Nullable StructureComponent piece, boolean alongX, int start, int rowMost, int acrossLeast, int acrossMost, int[] profile, boolean[] held) {
        List<StructureComponent> pieces = ContentBeard.components();
        if (pieces == null) { return; }

        StructureBoundingBox own = piece != null ? piece.getBoundingBox()
                : new StructureBoundingBox(alongX ? start : acrossLeast, 0, alongX ? acrossLeast : start, alongX ? rowMost : acrossMost, 0, alongX ? acrossMost : rowMost);
        for (StructureComponent other : pieces) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox road = other.getBoundingBox();
            boolean otherAlongX = BeardPlots.roadAlongX(other);
            if (otherAlongX == alongX) { continue; }

            if (road.minX - 1 > own.maxX || own.minX - 1 > road.maxX || road.minZ - 1 > own.maxZ || own.minZ - 1 > road.maxZ) { continue; }

            int center = alongX ? (road.minX + road.maxX) / 2 : (road.minZ + road.maxZ) / 2;
            int anchorRow = Math.max(start, Math.min(start + profile.length - 1, center));
            int grade = profile[anchorRow - start];
            if (grade == Integer.MIN_VALUE) { continue; }

            int crossRow = otherAlongX ? (own.minX + own.maxX) / 2 : (own.minZ + own.maxZ) / 2;
            int crossed = chainGradeAt(world, other, otherAlongX, crossRow);
            if (crossed != Integer.MIN_VALUE && crossed < grade) { grade = crossed; }

            if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("A junction apron holds the rows around {} to y {} for the road at {}, {}", center, grade, own.minX, own.minZ); }
            int reach = 1 + pathExtraWidth() + 3;
            for (int row = center - reach; row <= center + reach; row++) {
                if (row < start || row > start + profile.length - 1) { continue; }
                if (profile[row - start] != Integer.MIN_VALUE) {
                    profile[row - start] = grade;
                    held[row - start] = true;
                }
            }
        }
    }

    public static void clampToWell(World world, boolean alongX, int start, int acrossLeast, int acrossMost, int[] profile, boolean[] held) {
        List<StructureComponent> pieces = ContentBeard.components();
        if (pieces == null || pieces.isEmpty()) { return; }

        StructureBoundingBox well = pieces.get(0).getBoundingBox();
        int reach = ContentBeard.plazaReach();
        if (acrossMost < (alongX ? well.minZ : well.minX) - reach || acrossLeast > (alongX ? well.maxZ : well.maxX) + reach) { return; }

        int ground = BeardSite.wellGround(world, well);
        int rowLeast = (alongX ? well.minX : well.minZ) - reach;
        int rowMost = (alongX ? well.maxX : well.maxZ) + reach;
        int clamped = 0;
        for (int row = Math.max(start, rowLeast); row <= Math.min(start + profile.length - 1, rowMost); row++) {
            if (profile[row - start] == Integer.MIN_VALUE) { continue; }

            held[row - start] = true;
            if (profile[row - start] == ground) { continue; }

            profile[row - start] = ground;
            clamped++;
        }
        if (clamped > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Clamped {} road row(s) beside the well to its ground at y {}", clamped, ground); }
    }

    public static int roadAnchor(World world, boolean alongX, int row, int acrossLeast, int acrossMost, IBlockState path, IBlockState gravel) {
        for (int across = acrossLeast; across <= acrossMost; across++) {
            int x = alongX ? row : across;
            int z = alongX ? across : row;
            BlockPos spot = new BlockPos(x, 64, z);
            if (!world.isBlockLoaded(spot)) { continue; }

            IBlockState held = world.getBlockState(world.getTopSolidOrLiquidBlock(spot).down());
            if (held == path || held == gravel) { return world.getTopSolidOrLiquidBlock(spot).down().getY(); }
        }
        return Integer.MIN_VALUE;
    }

    public static boolean clearable(IBlockState held) {
        Block block = held.getBlock();
        if (block == Blocks.AIR) { return false; }
        if (block == Blocks.STONE && !held.getValue(BlockStone.VARIANT).isNatural()) { return false; }

        return BeardBlocks.terrainBlock(block) || held.getMaterial() == Material.VINE || held.getMaterial() == Material.PLANTS;
    }

    public static IBlockState pathForGround(World world, int x, int z, IBlockState path, IBlockState gravel, boolean earthy) {
        Block ground = BeardBlocks.fillGround(world, x, z).getBlock();
        if (ground == Blocks.SAND) { return asked(Blocks.SANDSTONE.getDefaultState()); }
        if (ground == Blocks.HARDENED_CLAY) { return asked(Blocks.HARDENED_CLAY.getDefaultState()); }
        if (ground == Blocks.GRAVEL) { return asked(Blocks.GRAVEL.getDefaultState()); }

        return earthy ? path : gravel;
    }

    private static IBlockState asked(IBlockState picked) {
        IBlockState wanted = ContentVillages.swap(picked);
        return wanted != null ? wanted : picked;
    }

    public static boolean pathChosen() { return !ContentControl.text(ContentControl.VILLAGES, "villagePathBlock", Config.worldgen.villagePathBlock).isEmpty(); }

    public static int pathExtraWidth() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathExtraWidth", Config.worldgen.villagePathExtraWidth)); }

    public static int pathLineColumns() { return ContentControl.text(ContentControl.VILLAGES, "villagePathLineBlock", Config.worldgen.villagePathLineBlock).isEmpty() ? 0 : 1; }

    public static int pathSidewalkWidth() {
        if (ContentControl.text(ContentControl.VILLAGES, "villagePathSidewalkBlock", Config.worldgen.villagePathSidewalkBlock).isEmpty()) { return 0; }

        return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathSidewalkWidth", Config.worldgen.villagePathSidewalkWidth));
    }

    public static int pathFullWidth() { return 3 + 2 * (pathExtraWidth() + pathLineColumns() + pathSidewalkWidth()); }

    public static int pathMinimumWidth() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathMinimumWidth", Config.worldgen.villagePathMinimumWidth)); }

    public static IBlockState pathBlock(String key, String fromConfig, IBlockState vanilla) {
        String named = ContentControl.text(ContentControl.VILLAGES, key, fromConfig);
        if (named.isEmpty()) { return vanilla; }

        IBlockState state = ContentStates.parse(named, key);
        if (state == null) {
            ContentLog.LOGGER.error("{} '{}' is not a registered block, using the vanilla road block", key, named);
            return vanilla;
        }
        return state;
    }
}
