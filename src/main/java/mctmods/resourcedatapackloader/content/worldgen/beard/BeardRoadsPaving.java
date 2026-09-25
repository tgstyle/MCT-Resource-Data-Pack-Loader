package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.village.CityGrowth;
import mctmods.resourcedatapackloader.content.village.MergePiece;
import mctmods.resourcedatapackloader.content.village.RailPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.beard.interfaces.IRoadLayout;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.world.GroundLevel;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class BeardRoadsPaving {
    private BeardRoadsPaving() {}

    private static final int VERGE_WET = 8;
    static final int FILL_UNDER = 8;

    public static void pave(StructureComponent piece, World world, StructureBoundingBox clip, IBlockState path, IBlockState gravel, IBlockState planks, boolean chosenSurface) {
        StructureBoundingBox box = piece.getBoundingBox();
        boolean alongX = BeardPlots.roadAlongX(piece);
        if (!(piece instanceof MergePiece) && CityGrowth.bulbWide(piece)) {
            BeardRoadsEnds.paveBulb(piece, world, clip, path, gravel, chosenSurface);
            return;
        }
        List<StructureComponent> nearby = BeardRoads.villagePieces(world);
        BeardRoadsEnds.DeadEndCap cap = BeardRoadsEnds.deadEndCap(world, nearby, piece, alongX);
        int least = Math.max(alongX ? box.minX : box.minZ, alongX ? clip.minX : clip.minZ);
        int most = Math.min(alongX ? box.maxX : box.maxZ, alongX ? clip.maxX : clip.maxZ);
        if (most < least) { return; }
        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Paving the road at {}, {}, {} across, with surface {} (chosen={}), support {}, bridge {}", box.minX, box.minZ, (alongX ? box.maxZ - box.minZ : box.maxX - box.minX) + 1, path, chosenSurface, gravel, planks); }
        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} runs along {} from {} to {}, is asked for the patch of land from {}, {} to {}, {}, and so will lay rows {} to {}", box.minX, box.minZ, alongX ? "x" : "z", alongX ? box.minX : box.minZ, alongX ? box.maxX : box.maxZ, clip.minX, clip.minZ, clip.maxX, clip.maxZ, least, most); }
        int acrossLeast = alongX ? box.minZ : box.minX;
        int acrossMost = alongX ? box.maxZ : box.maxX;
        BeardRoads.Grade graded = storedGrade(piece, box, alongX);
        boolean stored = graded != null;
        if (graded == null) { graded = BeardRoadsGrade.roadProfile(world, piece, alongX, alongX ? box.minX : box.minZ, alongX ? box.maxX : box.maxZ, acrossLeast, acrossMost, true); }
        boolean computed = graded != null;
        if (graded == null) { graded = groundGrade(world, clip, alongX, least, most, acrossLeast, acrossMost, path, gravel); }
        traceGrade(box, alongX, graded, computed);
        Paving paving = new Paving(world, piece, clip, alongX, path, gravel, planks, chosenSurface, cap, graded, nearby);
        for (int i = Math.max(0, least - graded.start); i < graded.profile.length && graded.start + i <= most; i++) { paving.row(i); }
        if (stored && (alongX ? clip.minZ : clip.minX) <= acrossLeast - 1 && (alongX ? clip.maxZ : clip.maxX) >= acrossMost + 1) {
            for (int row = least; row <= most; row++) { graded.covered[row - graded.start] = true; }
        }
        if (piece instanceof MergePiece) { BeardSewers.lay(piece, world, clip, alongX, graded, least, most, acrossLeast, acrossMost, new ArrayList<>(), new ArrayList<>(), ((MergePiece) piece)::centerAt); }
        else {
            List<StructureBoundingBox> sewerCrossed = new ArrayList<>(paving.crossed);
            sewerCrossed.addAll(BeardSewers.loopCrossings(nearby, box));
            BeardSewers.lay(piece, world, clip, alongX, graded, least, most, acrossLeast, acrossMost, sewerCrossed, paving.crossed);
        }
        if ((paving.cut + paving.filled + paving.paved + paving.lined > 0) && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Graded the road at {}, {} within its chunk: paved {} column(s), cut {} block(s) off bumps, filled {} into dips, lined {} of tunnel", box.minX, box.minZ, paving.paved, paving.cut, paving.filled, paving.lined); }
    }

    @Nullable private static BeardRoads.Grade storedGrade(StructureComponent piece, StructureBoundingBox box, boolean alongX) {
        BeardRoads.Grade graded = piece instanceof IRoadLayout ? ((IRoadLayout) piece).rdpl$layout() : null;
        if (graded != null && (graded.start != (alongX ? box.minX : box.minZ) || graded.rows() != (alongX ? box.maxX - box.minX : box.maxZ - box.minZ) + 1)) {
            if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The stored profile of the road at {}, {} no longer matches its box, so it is set aside and recomputed", box.minX, box.minZ); }
            return null;
        }
        return graded;
    }

    private static BeardRoads.Grade groundGrade(World world, StructureBoundingBox clip, boolean alongX, int least, int most, int acrossLeast, int acrossMost, IBlockState path, IBlockState gravel) {
        int rows = most - least + 1;
        int[] profile = new int[rows];
        for (int i = 0; i < rows; i++) {
            int found = Integer.MIN_VALUE;
            for (int across = acrossLeast; across <= acrossMost; across++) {
                int x = alongX ? least + i : across;
                int z = alongX ? across : least + i;
                BlockPos spot = new BlockPos(x, 64, z);
                if (!clip.isVecInside(spot)) { continue; }
                BlockPos top = GroundLevel.inWindow(world, spot).down();
                if (top.getY() < world.getSeaLevel() - 1 || world.getBlockState(top).getMaterial().isLiquid()) { continue; }
                if (top.getY() > found) { found = top.getY(); }
            }
            profile[i] = found;
        }
        int before = roadAnchor(world, alongX, least - 1, acrossLeast, acrossMost, path, gravel);
        if (before != Integer.MIN_VALUE && profile[0] != Integer.MIN_VALUE) { profile[0] = MathHelper.clamp(profile[0], before - 1, before + 1); }
        int after = roadAnchor(world, alongX, most + 1, acrossLeast, acrossMost, path, gravel);
        if (after != Integer.MIN_VALUE && profile[rows - 1] != Integer.MIN_VALUE) { profile[rows - 1] = MathHelper.clamp(profile[rows - 1], after - 1, after + 1); }
        int[] ground = profile.clone();
        boolean[] bridged = BeardGrade.smooth(profile);
        int capped = BeardGrade.capEmbankment(profile, ground, bridged, new boolean[profile.length]);
        return new BeardRoads.Grade(profile, ground, bridged, new boolean[profile.length], least, capped);
    }

    private static void traceGrade(StructureBoundingBox box, boolean alongX, BeardRoads.Grade graded, boolean computed) {
        if (!ContentLog.LOGGER.debugEnabled()) { return; }
        int[] profile = graded.profile;
        if (graded.capped > 0) { ContentLog.LOGGER.debug("Capped {} row(s) of the road at {}, {} to {} block(s) above their own ground", graded.capped, box.minX, box.minZ, BeardGrade.CAP); }
        StringBuilder trace = new StringBuilder();
        for (int i = 0; i < profile.length; i++) {
            if (i > 0) { trace.append(' '); }
            trace.append(graded.start + i).append(':');
            if (graded.ground[i] == Integer.MIN_VALUE) { trace.append('-'); }
            else { trace.append(graded.ground[i]); }
            trace.append('/');
            if (profile[i] == Integer.MIN_VALUE) { trace.append('-'); }
            else { trace.append(profile[i]); }
            if (graded.bridged[i]) { trace.append('b'); }
        }
        ContentLog.LOGGER.debug("Profile of the road at {}, {} along {}, computed {}, as row:ground/graded, capped {} row(s) at {}: {}", box.minX, box.minZ, alongX ? "x" : "z", computed, graded.capped, BeardGrade.CAP, trace);
        ContentLog.LOGGER.debug("The road at {}, {} grades from y {} to y {} along its length", box.minX, box.minZ, profile[0] == Integer.MIN_VALUE ? "water" : profile[0], profile[profile.length - 1] == Integer.MIN_VALUE ? "water" : profile[profile.length - 1]);
    }

    private enum Footing { DONE, PIER, GROUND }

    private static final class Paving {
        private final World world;
        private final StructureComponent piece;
        private final StructureBoundingBox box;
        private final StructureBoundingBox clip;
        private final boolean alongX;
        private final IBlockState planks;
        private final boolean chosenSurface;
        private final BeardRoadsEnds.DeadEndCap cap;
        private final BeardRoads.Grade graded;
        private final int start;
        private final int[] profile;
        private final boolean[] bridged;
        private final int acrossLeast;
        private final int acrossMost;
        private final int center;
        private final BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        private final BeardRoadsDecks.Pier dock;
        private final List<StructureBoundingBox> crossed;
        private final List<StructureBoundingBox> touching = new ArrayList<>();
        private final boolean[] tunnels;
        private final List<RailPiece> bores;
        private final boolean[] lit;
        private final boolean[] frames;
        private final boolean[] legs;
        private IBlockState path;
        private IBlockState gravel;
        private BeardRoads.Palette linings;
        private IBlockState light;
        private int cut;
        private int filled;
        private int paved;
        private int lined;

        private Paving(World world, StructureComponent piece, StructureBoundingBox clip, boolean alongX, IBlockState path, IBlockState gravel, IBlockState planks, boolean chosenSurface, BeardRoadsEnds.DeadEndCap cap, BeardRoads.Grade graded, List<StructureComponent> nearby) {
            this.world = world;
            this.piece = piece;
            this.box = piece.getBoundingBox();
            this.clip = clip;
            this.alongX = alongX;
            this.path = path;
            this.gravel = gravel;
            this.planks = planks;
            this.chosenSurface = chosenSurface;
            this.cap = cap;
            this.graded = graded;
            this.start = graded.start;
            this.profile = graded.profile;
            this.bridged = graded.bridged;
            this.acrossLeast = alongX ? box.minZ : box.minX;
            this.acrossMost = alongX ? box.maxZ : box.maxX;
            int depth = BeardRoadsTunnels.tunnelDepth();
            this.linings = BeardRoadsTunnels.tunnelPalette();
            this.light = BeardRoads.pathBlock("villagePathTunnelLightBlock", Config.worldgen.villagePathTunnelLightBlock, Blocks.AIR.getDefaultState());
            int lightRun = Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villagePathTunnelLightRun", Config.worldgen.villagePathTunnelLightRun));
            this.dock = BeardRoadsDecks.pierFor(world, piece, alongX, box, graded);
            this.crossed = BeardRoads.crossings(nearby, piece, box);
            for (StructureBoundingBox other : crossed) {
                if (other.maxX >= box.minX - 1 && other.minX <= box.maxX + 1 && other.maxZ >= box.minZ - 1 && other.minZ <= box.maxZ + 1) { touching.add(other); }
            }
            this.tunnels = new boolean[profile.length];
            this.bores = BeardRails.subways(world, box);
            this.center = (acrossLeast + acrossMost) / 2;
            for (int i = 0; i < tunnels.length; i++) { tunnels[i] = graded.tunneledAt(start + i, depth) && BeardRoadsTunnels.uncrossedRow(touching, alongX, start + i) && !BeardRoads.insidePlaza(alongX ? start + i : center, alongX ? center : start + i); }
            BeardRoadsTunnels.dropShortRuns(tunnels);
            this.lit = BeardRoadsTunnels.tunnelLights(tunnels, start, lightRun);
            this.frames = BeardRoadsDecks.bridgeFrames(profile, bridged);
            this.legs = BeardRoadsDecks.bridgeLegs(profile, bridged);
            for (int i = 0; i < legs.length; i++) { legs[i] |= frames[i]; }
            trace();
        }

        private void trace() {
            if (!ContentLog.LOGGER.debugEnabled()) { return; }
            StringBuilder roofed = new StringBuilder();
            for (int i = 0; i < tunnels.length; i++) {
                if (!tunnels[i]) { continue; }
                roofed.append(' ').append(start + i);
                if (lit[i]) { roofed.append('*'); }
            }
            if (roofed.length() > 0) { ContentLog.LOGGER.debug("The road at {}, {} roofs row(s){}, lit where starred, with {} for the light", box.minX, box.minZ, roofed, light.getBlock().getRegistryName()); }
            if (!crossed.isEmpty()) { ContentLog.LOGGER.debug("The road at {}, {} sees {} crossing road box(es): {}", box.minX, box.minZ, crossed.size(), crossed); }
        }

        private void row(int i) {
            int row = start + i;
            if (piece instanceof mctmods.resourcedatapackloader.mixin.rdpl.common.IVillagePiece && BeardBiome.moved(world, alongX ? row : center, alongX ? center : row)) {
                path = BeardRoads.pathBlock("villagePathBlock", Config.worldgen.villagePathBlock, ((mctmods.resourcedatapackloader.mixin.rdpl.common.IVillagePiece) piece).rdpl$biomeBlock(Blocks.GRASS_PATH.getDefaultState()));
                gravel = BeardRoads.pathBlock("villagePathSupportBlock", Config.worldgen.villagePathSupportBlock, ((mctmods.resourcedatapackloader.mixin.rdpl.common.IVillagePiece) piece).rdpl$biomeBlock(Blocks.GRAVEL.getDefaultState()));
                linings = BeardRoadsTunnels.tunnelPalette();
                light = BeardRoads.pathBlock("villagePathTunnelLightBlock", Config.worldgen.villagePathTunnelLightBlock, Blocks.AIR.getDefaultState());
            }
            for (int across = acrossLeast; across <= acrossMost; across++) { cell(i, across); }
            if (frames[i] && BeardRoadsTunnels.uncrossedRow(touching, alongX, row) && (dock == null || !dock.covers(row))) {
                int deckY = profile[i] != Integer.MIN_VALUE ? profile[i] : graded.deck[i];
                if (deckY != Integer.MIN_VALUE) { paved += BeardRoadsDecks.bridgeFrame(world, piece, alongX, row, acrossLeast, acrossMost, deckY, clip, at); }
            }
            if (profile[i] == Integer.MIN_VALUE) { return; }
            for (int side = 0; side < 2; side++) { side(i, side == 0 ? acrossLeast - 1 : acrossMost + 1); }
        }

        private void side(int i, int across) {
            int x = alongX ? start + i : across;
            int z = alongX ? across : start + i;
            at.setPos(x, profile[i], z);
            if (!clip.isVecInside(at)) { return; }
            if (bridged[i]) {
                if (wetBed(world, at, x, z, profile[i]) == Integer.MIN_VALUE) { filled += vergeFill(world, piece, x, z, profile[i], at); }
                return;
            }
            if (tunnels[i]) {
                lined += BeardRoadsTunnels.tunnelWall(world, piece, at, x, z, profile[i], linings);
                return;
            }
            filled += vergeFill(world, piece, x, z, profile[i], at);
        }

        private void cell(int i, int across) {
            int x = alongX ? start + i : across;
            int z = alongX ? across : start + i;
            BlockPos spot = new BlockPos(x, 64, z);
            if (!clip.isVecInside(spot)) {
                if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} left {}, {} unpaved because it lies outside the patch of land it was asked for, {}, {} to {}, {}", box.minX, box.minZ, x, z, clip.minX, clip.minZ, clip.maxX, clip.maxZ); }
                return;
            }
            if (BeardRoads.insidePlaza(x, z)) { return; }
            if (!bores.isEmpty() && BeardRails.insideBore(world, bores, x, profile[i] == Integer.MIN_VALUE ? graded.deck[i] : profile[i], z)) { return; }
            BlockPos top = GroundLevel.inWindow(world, spot).down();
            if (top.getY() < world.getSeaLevel()) { top = new BlockPos(x, world.getSeaLevel() - 1, z); }
            if (profile[i] == Integer.MIN_VALUE && ungraded(i, across, x, z, top)) { return; }
            boolean pier = false;
            if (profile[i] != Integer.MIN_VALUE) {
                Footing footing = gradedFooting(i, across, x, z, top);
                if (footing == Footing.DONE) { return; }
                pier = footing == Footing.PIER;
            }
            surface(i, across, x, z, top, pier);
        }

        private boolean ungraded(int i, int across, int x, int z, BlockPos top) {
            boolean wet = false;
            int deckAt = Integer.MIN_VALUE;
            if (graded.deck[i] != Integer.MIN_VALUE) {
                for (int y = graded.deck[i]; y >= graded.deck[i] - 8 && y >= 1; y--) {
                    IBlockState stood = world.getBlockState(at.setPos(x, y, z));
                    if (stood.getBlock() == Blocks.AIR) { continue; }
                    if (stood.getMaterial().isLiquid()) {
                        wet = true;
                        deckAt = y + 1;
                    }
                    break;
                }
            }
            else if (world.getBlockState(top).getMaterial().isLiquid()) {
                wet = true;
                deckAt = top.getY() + 1;
            }
            if (wet) {
                if (graded.held[i] && BeardRoadsDecks.onPiling(start + i, across, acrossLeast, acrossMost)) { filled += BeardBlocks.fillPier(world, at, x, z, deckAt - 1, pierFloor(x, z), gravel); }
                paved += BeardRoadsDecks.deckBridge(world, box, alongX, start + i, across, acrossLeast, acrossMost, deckAt, planks, gravel, at, dock, crossed, legs[i], frames[i]);
                if (dock == null) { paved += BeardRoadsEnds.deadEndCap(cap, world, piece, alongX, start + i, across, acrossLeast, at.getY(), at); }
                return true;
            }
            if (graded.deck[i] == Integer.MIN_VALUE) {
                if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} left {}, {} unpaved because no grade was worked out for that row and the ground under it is {}, not water to bridge", box.minX, box.minZ, x, z, world.getBlockState(top).getBlock().getRegistryName()); }
                return true;
            }
            return false;
        }

        private Footing gradedFooting(int i, int across, int x, int z, BlockPos top) {
            boolean wet = world.getBlockState(top).getMaterial().isLiquid();
            for (int y = profile[i] - 1; !wet && y >= profile[i] - 8; y--) {
                IBlockState stood = world.getBlockState(at.setPos(x, y, z));
                if (stood.getMaterial().isLiquid()) { wet = true; }
                else if (stood.getMaterial().isSolid()) { break; }
            }
            boolean pier = false;
            if (wet && graded.held[i]) {
                pier = true;
                if (BeardRoadsDecks.onPiling(start + i, across, acrossLeast, acrossMost)) { filled += BeardBlocks.fillPier(world, at, x, z, profile[i] - 1, pierFloor(x, z), gravel); }
            }
            if (!bridged[i]) { BeardRoadsDecks.unrail(world, at, x, profile[i] + 1, z, planks); }
            else {
                paved += BeardRoadsDecks.deckBridge(world, box, alongX, start + i, across, acrossLeast, acrossMost, profile[i], planks, gravel, at, dock, crossed, legs[i], frames[i]);
                if (dock == null) { paved += BeardRoadsEnds.deadEndCap(cap, world, piece, alongX, start + i, across, acrossLeast, at.getY(), at); }
                return Footing.DONE;
            }
            if (wet && !pier) {
                filled += BeardBlocks.fillPier(world, at, x, z, profile[i] - 1, pierFloor(x, z), gravel);
                at.setPos(x, profile[i], z);
                if (!BeardKeep.holds(x, profile[i], z)) {
                    IBlockState piered = chosenSurface ? path : BeardRoads.pathForGround(world, x, z, path, gravel, true);
                    boolean joint = BeardRoadsSurface.squareAt(box, alongX, crossed, x, z);
                    IBlockState dressed = BeardRoadsSurface.dressSurface(world, piece, alongX, alongX ? x : z, alongX ? z : x, center, piered, planks, crossed);
                    world.setBlockState(at, dressed != null ? dressed : piered, 2);
                    paved++;
                    if (joint) { paved += BeardRoadsDecks.deckRail(world, box, alongX, start + i, across, acrossLeast, acrossMost, profile[i], planks, gravel, at, dock, crossed, false, false); }
                }
                roofOver(i, across, x, z, profile[i]);
                return Footing.DONE;
            }
            return pier ? Footing.PIER : Footing.GROUND;
        }

        private int pierFloor(int x, int z) { return BeardRails.boreFloor(world, bores, x, z); }

        private void surface(int i, int across, int x, int z, BlockPos top, boolean pier) {
            int target = profile[i] == Integer.MIN_VALUE ? graded.deck[i] : profile[i];
            at.setPos(x, target, z);
            IBlockState held = world.getBlockState(at);
            Block base = held.getBlock();
            if (standingInTheWay(held, base)) {
                if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} left {}, {}, {} unpaved because {} was already standing there and is not a surface a road may be laid over", box.minX, box.minZ, x, target, z, base.getRegistryName()); }
                return;
            }
            clearAbove(x, z, target, tunnels[i] ? target + BeardRoadsTunnels.BORE : Math.max(target + 4, top.getY() + 2));
            if (profile[i] != Integer.MIN_VALUE && !pier) { filled += BeardBlocks.fillUnder(world, at, x, z, target - 1, Math.max(target - FILL_UNDER, pierFloor(x, z))); }
            at.setPos(x, target, z);
            if (profile[i] == Integer.MIN_VALUE) {
                deckCell(i, across, x, z, target);
                return;
            }
            boolean earthy = base == Blocks.GRASS || base == Blocks.DIRT || base == Blocks.MYCELIUM || base == Blocks.GRASS_PATH || base == Blocks.AIR || !world.getBlockState(at).getMaterial().isSolid();
            IBlockState natural = chosenSurface ? path : BeardRoads.pathForGround(world, x, z, path, gravel, earthy && !pier);
            if (piece instanceof MergePiece) {
                mergeCell(i, across, x, z, target, natural);
                return;
            }
            boolean joint = BeardRoadsSurface.squareAt(box, alongX, crossed, x, z);
            IBlockState dressed = BeardRoadsSurface.dressSurface(world, piece, alongX, alongX ? x : z, alongX ? z : x, center, natural, planks, crossed);
            world.setBlockState(at, dressed != null ? dressed : natural, 2);
            paved++;
            roofOver(i, across, x, z, target);
            paved += BeardRoadsEnds.deadEndCap(cap, world, piece, alongX, start + i, across, acrossLeast, target, at);
            if (joint) { paved += BeardRoadsDecks.deckRail(world, box, alongX, start + i, across, acrossLeast, acrossMost, target, planks, gravel, at, dock, crossed, false, false); }
        }

        private void roofOver(int i, int across, int x, int z, int level) {
            if (!tunnels[i]) { return; }
            int roof = level + BeardRoadsTunnels.BORE + 1;
            lined += BeardRoadsTunnels.roofCell(world, at, x, z, roof, lit[i] && across == center && light.getBlock() != Blocks.AIR ? light : linings.pick(world, x, roof, z));
        }

        private boolean standingInTheWay(IBlockState held, Block base) {
            return held.getMaterial().isSolid() && held.getMaterial() != Material.WOOD && held.getMaterial() != Material.LEAVES && !BeardBlocks.terrainBlock(base) && base != path.getBlock() && base != gravel.getBlock() && base != planks.getBlock() && base != Blocks.GRASS_PATH && base != Blocks.PLANKS && base != Blocks.SANDSTONE && base != Blocks.RED_SANDSTONE && base != Blocks.HARDENED_CLAY && base != Blocks.STAINED_HARDENED_CLAY && base != Blocks.MYCELIUM;
        }

        private void clearAbove(int x, int z, int target, int clearTo) {
            for (int y = target + 1; y <= clearTo; y++) {
                at.setPos(x, y, z);
                IBlockState above = world.getBlockState(at);
                Block up = above.getBlock();
                if (up == Blocks.AIR) { continue; }
                if (BeardKeep.holds(x, y, z) || BeardRails.railBlock(above)) { continue; }
                if (above.getMaterial().isLiquid()) { break; }
                if (BeardBlocks.terrainBlock(up) || up == Blocks.GRASS_PATH || up == Blocks.SANDSTONE || up == Blocks.MYCELIUM || above.getMaterial() == Material.WOOD || above.getMaterial() == Material.LEAVES || !above.getMaterial().isSolid()) {
                    BeardBlocks.note(world, at, "Paving the road");
                    world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
                    cut++;
                    continue;
                }
                break;
            }
        }

        private void deckCell(int i, int across, int x, int z, int target) {
            IBlockState decked = BeardRoadsDecks.deckState(world, box, alongX, start + i, across, acrossLeast, acrossMost, planks, dock, crossed);
            if (decked == null) { return; }
            if (BeardRoadsDecks.bridgeDress(decked, planks)) { paved += BeardRoadsDecks.deckRail(world, box, alongX, start + i, across, acrossLeast, acrossMost, target, planks, gravel, at, dock, crossed, legs[i], frames[i]); }
            else {
                BeardRoadsDecks.unrail(world, at, x, target + 1, z, planks);
                at.setPos(x, target, z);
            }
            if (!BeardKeep.holds(at.getX(), at.getY(), at.getZ())) {
                world.setBlockState(at, decked, 2);
                paved++;
            }
            if (dock == null) { paved += BeardRoadsEnds.deadEndCap(cap, world, piece, alongX, start + i, across, acrossLeast, target, at); }
        }

        private void mergeCell(int i, int across, int x, int z, int target, IBlockState natural) {
            int merged = ((MergePiece) piece).centerAt(start + i);
            if (Math.abs(across - merged) > (BeardRoads.pathFullWidth() - 1) / 2) {
                filled += vergeFill(world, piece, x, z, target, at);
                at.setPos(x, target, z);
                if (world.getBlockState(at).getMaterial().isReplaceable() && !BeardKeep.holds(x, target, z)) {
                    at.setPos(x, target - 1, z);
                    IBlockState footing = world.getBlockState(at);
                    if (footing.getMaterial().isSolid() && !footing.getMaterial().isLiquid()) {
                        at.setPos(x, target, z);
                        IBlockState turf = BeardBlocks.fillGround(world, x, z);
                        world.setBlockState(at, turf.getBlock() == Blocks.DIRT ? Blocks.GRASS.getDefaultState() : turf, 2);
                        filled++;
                    }
                }
                return;
            }
            world.setBlockState(at, BeardRoadsSurface.mergeSurface(alongX, x, z, start + i, merged, natural), 2);
            paved++;
        }
    }

    public static List<StructureBoundingBox> repairRoads(World world, StructureStart start) {
        List<StructureBoundingBox> repaved = new ArrayList<>();
        if (!(world.getChunkProvider() instanceof ChunkProviderServer)) { return repaved; }
        ChunkProviderServer provider = (ChunkProviderServer) world.getChunkProvider();
        for (StructureComponent piece : start.getComponents()) {
            if (!(piece instanceof StructureVillagePieces.Path) || !(piece instanceof IRoadLayout)) { continue; }
            BeardRoads.Grade grade = ((IRoadLayout) piece).rdpl$layout();
            if (grade == null) { continue; }
            StructureBoundingBox box = piece.getBoundingBox();
            boolean alongX = BeardPlots.roadAlongX(piece);
            if (grade.start != (alongX ? box.minX : box.minZ) || grade.rows() != (alongX ? box.maxX - box.minX : box.maxZ - box.minZ) + 1) { continue; }
            int acrossLeast = (alongX ? box.minZ : box.minX) - 1;
            int acrossMost = (alongX ? box.maxZ : box.maxX) + 1;
            for (int i = 0; i < grade.covered.length; i++) {
                if (grade.covered[i]) { continue; }
                int from = i;
                while (i + 1 < grade.covered.length && !grade.covered[i + 1]) { i++; }
                for (int row = grade.start + from; row <= grade.start + i; ) {
                    int band = Math.min(grade.start + i, row | 15);
                    int minX = alongX ? row : acrossLeast;
                    int maxX = alongX ? band : acrossMost;
                    int minZ = alongX ? acrossLeast : row;
                    int maxZ = alongX ? acrossMost : band;
                    if (populatedOver(provider, minX, maxX, minZ, maxZ)) {
                        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} was never asked to build rows {} to {}, so they are laid now from its stored profile", box.minX, box.minZ, row, band); }
                        StructureBoundingBox patch = new StructureBoundingBox(minX, minZ, maxX, maxZ);
                        repaved.add(patch);
                        ContentBeard.building(start);
                        try {
                            ContentBeard.fellFor(start, piece, world, patch);
                            BeardKeep.watch(world, piece, patch);
                            ((IRoadLayout) piece).rdpl$repave(world, patch);
                            BeardKeep.learn(world);
                        }
                        finally { ContentBeard.building(null); }
                    }
                    row = band + 1;
                }
            }
        }
        return repaved;
    }

    private static boolean populatedOver(ChunkProviderServer provider, int minX, int maxX, int minZ, int maxZ) {
        for (int chunkX = minX >> 4; chunkX <= maxX >> 4; chunkX++) {
            for (int chunkZ = minZ >> 4; chunkZ <= maxZ >> 4; chunkZ++) {
                Chunk held = provider.getLoadedChunk(chunkX, chunkZ);
                if (held == null || !held.isTerrainPopulated()) { return false; }
            }
        }
        return true;
    }

    static int vergeFill(World world, StructureComponent piece, int x, int z, int level, BlockPos.MutableBlockPos at) {
        if (BeardRoads.insidePlaza(x, z)) { return 0; }
        StructureStart holder = ContentBeard.current();
        if (holder != null && BeardPlots.underAnother(holder, piece, x, z)) { return 0; }
        if (BeardKeep.holds(x, level, z)) { return 0; }
        at.setPos(x, level, z);
        IBlockState verge = world.getBlockState(at);
        if (verge.getMaterial().isSolid()) {
            at.setPos(x, level - 1, z);
            if (world.getBlockState(at).getMaterial().isSolid() || world.getBlockState(at).getMaterial().isLiquid()) { return 0; }
            return BeardBlocks.fillBank(world, at, x, z, level - 1, level - 6, false);
        }
        int bed = wetBed(world, at, x, z, level);
        if (bed != Integer.MIN_VALUE) { return BeardBlocks.fillUnder(world, at, x, z, level, bed + 1); }
        return BeardBlocks.fillBank(world, at, x, z, level, level - 5, false);
    }

    private static int wetBed(World world, BlockPos.MutableBlockPos at, int x, int z, int level) {
        boolean wet = false;
        for (int y = level; y >= level - VERGE_WET; y--) {
            at.setPos(x, y, z);
            IBlockState stood = world.getBlockState(at);
            if (stood.getMaterial().isLiquid()) { wet = true; }
            else if (stood.getMaterial().isSolid()) { return wet ? y : Integer.MIN_VALUE; }
        }
        return Integer.MIN_VALUE;
    }

    public static void paveMerge(MergePiece piece, World world, StructureBoundingBox clip) {
        BeardBiome.enter(world, (clip.minX + clip.maxX) / 2, (clip.minZ + clip.maxZ) / 2);
        try {
            pave(piece, world, clip,
                    BeardRoads.pathBlock("villagePathBlock", Config.worldgen.villagePathBlock, Blocks.GRASS_PATH.getDefaultState()),
                    BeardRoads.pathBlock("villagePathSupportBlock", Config.worldgen.villagePathSupportBlock, Blocks.GRAVEL.getDefaultState()),
                    BeardRoads.pathBlock("villagePathBridgeBlock", Config.worldgen.villagePathBridgeBlock, Blocks.PLANKS.getDefaultState()),
                    BeardRoads.pathChosen());
        }
        finally { BeardBiome.leave(); }
    }

    public static int roadAnchor(World world, boolean alongX, int row, int acrossLeast, int acrossMost, IBlockState path, IBlockState gravel) {
        for (int across = acrossLeast; across <= acrossMost; across++) {
            int x = alongX ? row : across;
            int z = alongX ? across : row;
            BlockPos spot = new BlockPos(x, 64, z);
            if (!world.isBlockLoaded(spot)) { continue; }
            BlockPos top = GroundLevel.inWindow(world, spot).down();
            IBlockState held = world.getBlockState(top);
            if (held == path || held == gravel) { return top.getY(); }
        }
        return Integer.MIN_VALUE;
    }
}
