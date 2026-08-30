package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.def.PathIntersectDef;
import mctmods.resourcedatapackloader.content.village.ContentVillages;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentPathIntersects;
import mctmods.resourcedatapackloader.content.worldgen.beard.interfaces.RoadLayout;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.MathUtil;
import mctmods.resourcedatapackloader.util.world.GroundLevel;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
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
        final boolean[] held;
        final int[] deck;
        final boolean[] covered;
        final int start;
        final int capped;
        Grade(int[] profile, int[] ground, boolean[] bridged, boolean[] held, int start, int capped) {
            this.profile = profile;
            this.ground = ground;
            this.bridged = bridged;
            this.held = held;
            this.start = start;
            this.capped = capped;
            this.deck = new int[profile.length];
            this.covered = new boolean[profile.length];
            for (int i = 0; i < profile.length; i++) { this.deck[i] = profile[i] == Integer.MIN_VALUE ? carried(profile, i) : Integer.MIN_VALUE; }
        }

        private Grade(int[] profile, int[] ground, boolean[] bridged, boolean[] held, int[] deck, boolean[] covered, int start, int capped) {
            this.profile = profile;
            this.ground = ground;
            this.bridged = bridged;
            this.held = held;
            this.deck = deck;
            this.covered = covered;
            this.start = start;
            this.capped = capped;
        }

        public int at(int row) { return profile[Math.max(0, Math.min(profile.length - 1, row - start))]; }

        public int rows() { return profile.length; }

        public int deckAt(int row) { return deck[Math.max(0, Math.min(deck.length - 1, row - start))]; }

        public void write(NBTTagCompound tag) {
            tag.setInteger("RdplStart", start);
            tag.setInteger("RdplCapped", capped);
            tag.setIntArray("RdplProfile", profile);
            tag.setIntArray("RdplGround", ground);
            tag.setIntArray("RdplDeck", deck);
            tag.setByteArray("RdplBridged", packed(bridged));
            tag.setByteArray("RdplHeld", packed(held));
            tag.setByteArray("RdplLaid", packed(covered));
        }

        @Nullable public static Grade read(NBTTagCompound tag) {
            if (!tag.hasKey("RdplProfile", 11)) { return null; }
            int[] profile = tag.getIntArray("RdplProfile");
            int[] ground = tag.getIntArray("RdplGround");
            int[] deck = tag.getIntArray("RdplDeck");
            if (profile.length == 0 || ground.length != profile.length || deck.length != profile.length) { return null; }
            boolean[] bridged = unpacked(tag.getByteArray("RdplBridged"), profile.length);
            boolean[] held = unpacked(tag.getByteArray("RdplHeld"), profile.length);
            boolean[] covered = unpacked(tag.getByteArray("RdplLaid"), profile.length);
            return new Grade(profile, ground, bridged, held, deck, covered, tag.getInteger("RdplStart"), tag.getInteger("RdplCapped"));
        }

        private static int carried(int[] profile, int i) {
            int before = Integer.MIN_VALUE;
            for (int back = i - 1; back >= 0; back--) {
                if (profile[back] == Integer.MIN_VALUE) { continue; }
                before = profile[back];
                break;
            }
            int after = Integer.MIN_VALUE;
            for (int on = i + 1; on < profile.length; on++) {
                if (profile[on] == Integer.MIN_VALUE) { continue; }
                after = profile[on];
                break;
            }
            if (before == Integer.MIN_VALUE) { return after; }
            if (after == Integer.MIN_VALUE) { return before; }
            return Math.max(before, after);
        }

        private static byte[] packed(boolean[] flags) {
            byte[] out = new byte[flags.length];
            for (int i = 0; i < flags.length; i++) { out[i] = (byte) (flags[i] ? 1 : 0); }
            return out;
        }

        private static boolean[] unpacked(byte[] bytes, int rows) {
            boolean[] out = new boolean[rows];
            for (int i = 0; i < rows && i < bytes.length; i++) { out[i] = bytes[i] != 0; }
            return out;
        }
    }

    @Nullable public static Grade roadProfile(World world, @Nullable StructureComponent piece, boolean alongX, int rowLeast, int rowMost, int acrossLeast, int acrossMost, boolean junctions) {
        int[] profile = BeardGrade.noiseProfile(world, alongX, rowLeast, rowMost, acrossLeast, acrossMost);
        if (profile == null) { return null; }
        int[] ground = profile.clone();
        BeardGrade.flatRuns(world, alongX, rowLeast, acrossLeast, acrossMost, profile);
        boolean[] bridged = BeardGrade.smooth(profile);
        boolean[] pinned = new boolean[profile.length];
        boolean[] plaza = new boolean[profile.length];
        boolean[] footed = new boolean[profile.length];
        int capped;
        if (junctions) {
            roadApron(world, piece, alongX, rowLeast, rowMost, acrossLeast, acrossMost, profile, pinned, footed, plaza, bridged);
            clampToWell(world, alongX, rowLeast, acrossLeast, acrossMost, profile, plaza);
            for (int i = 0; i < pinned.length; i++) { if (plaza[i]) { pinned[i] = true; } }
            frontHold(piece, alongX, rowLeast, acrossLeast, acrossMost, profile, pinned);
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
            boolean[] hold = new boolean[profile.length];
            for (int i = 0; i < hold.length; i++) { hold[i] = fixed[i] || (pinned[i] && (ground[i] == Integer.MIN_VALUE || (profile[i] <= ground[i] + BeardGrade.CAP && profile[i] >= ground[i] - BeardGrade.CAP))); }
            for (int i = 0; i < hold.length; i++) {
                if (hold[i] || bridged[i] || ground[i] == Integer.MIN_VALUE || profile[i] == Integer.MIN_VALUE) { continue; }
                int floor = ground[i];
                for (int near = Math.max(0, i - 2); near <= Math.min(ground.length - 1, i + 2); near++) {
                    if (ground[near] != Integer.MIN_VALUE && ground[near] < floor) { floor = ground[near]; }
                }
                if (profile[i] < floor - BeardGrade.CAP) { profile[i] = floor - BeardGrade.CAP; }
            }
            for (int round = 0; round < 4; round++) {
                rein(profile, hold, bridged);
                ramp(profile, bridged, plaza);
            }
            for (int i = 1; i < profile.length - 1; i++) {
                if (hold[i] || bridged[i] || profile[i] == Integer.MIN_VALUE) { continue; }
                if (profile[i - 1] == Integer.MIN_VALUE || profile[i + 1] == Integer.MIN_VALUE) { continue; }
                int flank = Math.max(profile[i - 1], profile[i + 1]);
                if (profile[i] > flank) { profile[i] = flank; }
            }
            for (int i = 0; i < footed.length; i++) { if (plaza[i]) { footed[i] = true; } }
            int width = acrossMost - acrossLeast + 1;
            int lowRun = 0;
            while (lowRun < profile.length && profile[lowRun] == Integer.MIN_VALUE) { lowRun++; }
            if (lowRun >= width && attachedAt(piece, alongX, rowLeast - 1, acrossLeast, acrossMost)) {
                for (int i = 0; i < width; i++) { footed[i] = true; }
            }
            int highRun = 0;
            while (highRun < profile.length && profile[profile.length - 1 - highRun] == Integer.MIN_VALUE) { highRun++; }
            if (highRun >= width && attachedAt(piece, alongX, rowMost + 1, acrossLeast, acrossMost)) {
                for (int i = 0; i < width; i++) { footed[profile.length - 1 - i] = true; }
            }
            BeardGrade.sag(profile, bridged, world.getSeaLevel());
            int held = BeardGrade.holdCauseway(profile, bridged, world.getSeaLevel());
            if (held > 0) { ContentLog.LOGGER.debug("Held {} short land row(s) of the road at {}, {} up to the deck crossing them, so the causeway stays level over its shoals", held, alongX ? rowLeast : acrossLeast, alongX ? acrossLeast : rowLeast); }
            int piers = 0;
            for (int i = 0; i < profile.length; i++) {
                if (!bridged[i] && ground[i] == Integer.MIN_VALUE && profile[i] != Integer.MIN_VALUE && profile[i] < world.getSeaLevel()) {
                    profile[i] = world.getSeaLevel();
                    piers++;
                }
            }
            if (piers > 0) { ContentLog.LOGGER.debug("Held {} pier row(s) of the road at {}, {} up to the water line, so they meet the decks either side", piers, alongX ? rowLeast : acrossLeast, alongX ? acrossLeast : rowLeast); }
        }
        else { capped = BeardGrade.capEmbankment(profile, ground, bridged, plaza); }
        return new Grade(profile, ground, bridged, footed, rowLeast, capped);
    }

    private static boolean attachedAt(@Nullable StructureComponent piece, boolean alongX, int row, int acrossLeast, int acrossMost) {
        List<StructureComponent> pieces = ContentBeard.components();
        if (pieces == null) { return false; }
        for (StructureComponent other : pieces) {
            if (other == piece) { continue; }
            StructureBoundingBox box = other.getBoundingBox();
            if (row < (alongX ? box.minX : box.minZ) || row > (alongX ? box.maxX : box.maxZ)) { continue; }
            if ((alongX ? box.maxZ : box.maxX) >= acrossLeast && (alongX ? box.minZ : box.minX) <= acrossMost) { return true; }
        }
        return false;
    }

    private static void rein(int[] profile, boolean[] held, boolean[] bridged) {
        for (int i = 1; i < profile.length; i++) {
            if (held[i] || bridged[i] || bridged[i - 1] || profile[i] == Integer.MIN_VALUE || profile[i - 1] == Integer.MIN_VALUE) { continue; }
            if (profile[i] > profile[i - 1] + 1) { profile[i] = profile[i - 1] + 1; }
        }
        for (int i = profile.length - 2; i >= 0; i--) {
            if (held[i] || bridged[i] || bridged[i + 1] || profile[i] == Integer.MIN_VALUE || profile[i + 1] == Integer.MIN_VALUE) { continue; }
            if (profile[i] > profile[i + 1] + 1) { profile[i] = profile[i + 1] + 1; }
        }
    }

    private static void ramp(int[] profile, boolean[] bridged, boolean[] fixed) {
        for (int i = 1; i < profile.length; i++) {
            if (fixed[i] || bridged[i] || bridged[i - 1] || profile[i] == Integer.MIN_VALUE || profile[i - 1] == Integer.MIN_VALUE) { continue; }
            if (profile[i] < profile[i - 1] - 1) { profile[i] = profile[i - 1] - 1; }
        }
        for (int i = profile.length - 2; i >= 0; i--) {
            if (fixed[i] || bridged[i] || bridged[i + 1] || profile[i] == Integer.MIN_VALUE || profile[i + 1] == Integer.MIN_VALUE) { continue; }
            if (profile[i] < profile[i + 1] - 1) { profile[i] = profile[i + 1] - 1; }
        }
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
        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} runs along {} from {} to {}, is asked for the patch of land from {}, {} to {}, {}, and so will lay rows {} to {}", box.minX, box.minZ, alongX ? "x" : "z", alongX ? box.minX : box.minZ, alongX ? box.maxX : box.maxZ, clip.minX, clip.minZ, clip.maxX, clip.maxZ, least, most); }
        int acrossLeast = alongX ? box.minZ : box.minX;
        int acrossMost = alongX ? box.maxZ : box.maxX;
        Grade graded = piece instanceof RoadLayout ? ((RoadLayout) piece).rdpl$layout() : null;
        if (graded != null && (graded.start != (alongX ? box.minX : box.minZ) || graded.rows() != (alongX ? box.maxX - box.minX : box.maxZ - box.minZ) + 1)) {
            if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The stored profile of the road at {}, {} no longer matches its box, so it is set aside and recomputed", box.minX, box.minZ); }
            graded = null;
        }
        boolean stored = graded != null;
        if (graded == null) { graded = roadProfile(world, piece, alongX, alongX ? box.minX : box.minZ, alongX ? box.maxX : box.maxZ, acrossLeast, acrossMost, true); }
        boolean computed = graded != null;
        if (graded == null) {
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
            if (before != Integer.MIN_VALUE && profile[0] != Integer.MIN_VALUE) { profile[0] = Math.max(before - 1, Math.min(before + 1, profile[0])); }
            int after = roadAnchor(world, alongX, most + 1, acrossLeast, acrossMost, path, gravel);
            if (after != Integer.MIN_VALUE && profile[rows - 1] != Integer.MIN_VALUE) { profile[rows - 1] = Math.max(after - 1, Math.min(after + 1, profile[rows - 1])); }
            int[] ground = profile.clone();
            boolean[] bridged = BeardGrade.smooth(profile);
            int capped = BeardGrade.capEmbankment(profile, ground, bridged, new boolean[profile.length]);
            graded = new Grade(profile, ground, bridged, new boolean[profile.length], least, capped);
        }
        int start = graded.start;
        int[] profile = graded.profile;
        int[] ground = graded.ground;
        boolean[] bridged = graded.bridged;
        int capped = graded.capped;
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
                if (!clip.isVecInside(spot)) {
                    if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} left {}, {} unpaved because it lies outside the patch of land it was asked for, {}, {} to {}, {}", box.minX, box.minZ, x, z, clip.minX, clip.minZ, clip.maxX, clip.maxZ); }
                    continue;
                }
                if (insidePlaza(x, z)) { continue; }
                BlockPos top = GroundLevel.inWindow(world, spot).down();
                if (top.getY() < world.getSeaLevel()) { top = new BlockPos(x, world.getSeaLevel() - 1, z); }
                if (profile[i] == Integer.MIN_VALUE) {
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
                        if (graded.held[i]) { filled += BeardBlocks.fillPier(world, at, x, z, deckAt - 1, gravel); }
                        paved += deckBridge(world, alongX, start + i, across, acrossLeast, acrossMost, deckAt, planks, at);
                        continue;
                    }
                    if (graded.deck[i] == Integer.MIN_VALUE) {
                        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} left {}, {} unpaved because no grade was worked out for that row and the ground under it is {}, not water to bridge", box.minX, box.minZ, x, z, world.getBlockState(top).getBlock().getRegistryName()); }
                        continue;
                    }
                }
                boolean pier = false;
                if (profile[i] != Integer.MIN_VALUE) {
                    boolean wet = world.getBlockState(top).getMaterial().isLiquid();
                    for (int y = profile[i] - 1; !wet && y >= profile[i] - 8; y--) {
                        IBlockState stood = world.getBlockState(at.setPos(x, y, z));
                        if (stood.getMaterial().isLiquid()) { wet = true; }
                        else if (stood.getMaterial().isSolid()) { break; }
                    }
                    if (wet && graded.held[i]) {
                        pier = true;
                        filled += BeardBlocks.fillPier(world, at, x, z, profile[i] - 1, gravel);
                    }
                    if (bridged[i]) {
                        paved += deckBridge(world, alongX, start + i, across, acrossLeast, acrossMost, profile[i], planks, at);
                        continue;
                    }
                    if (wet && !pier) {
                        filled += BeardBlocks.fillPier(world, at, x, z, profile[i] - 1, gravel);
                        at.setPos(x, profile[i], z);
                        if (!BeardKeep.holds(x, profile[i], z)) {
                            IBlockState piered = chosenSurface ? path : pathForGround(world, x, z, path, gravel, true);
                            IBlockState dressed = dressSurface(world, piece, alongX, alongX ? x : z, alongX ? z : x, (acrossLeast + acrossMost) / 2, piered);
                            world.setBlockState(at, dressed != null ? dressed : piered, 2);
                            paved++;
                        }
                        continue;
                    }
                }
                int target = profile[i] == Integer.MIN_VALUE ? graded.deck[i] : profile[i];
                at.setPos(x, target, z);
                IBlockState held = world.getBlockState(at);
                Block base = held.getBlock();
                if (held.getMaterial().isSolid() && held.getMaterial() != Material.WOOD && held.getMaterial() != Material.LEAVES && !BeardBlocks.terrainBlock(base) && base != path.getBlock() && base != gravel.getBlock() && base != planks.getBlock() && base != Blocks.GRASS_PATH && base != Blocks.PLANKS && base != Blocks.SANDSTONE && base != Blocks.RED_SANDSTONE && base != Blocks.HARDENED_CLAY && base != Blocks.STAINED_HARDENED_CLAY && base != Blocks.MYCELIUM) {
                    if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} left {}, {}, {} unpaved because {} was already standing there and is not a surface a road may be laid over", box.minX, box.minZ, x, target, z, base.getRegistryName()); }
                    continue;
                }
                int clearTo = Math.max(target + 4, top.getY() + 2);
                for (int y = target + 1; y <= clearTo; y++) {
                    at.setPos(x, y, z);
                    IBlockState above = world.getBlockState(at);
                    Block up = above.getBlock();
                    if (up == Blocks.AIR) { continue; }
                    if (BeardKeep.holds(x, y, z)) { continue; }
                    if (above.getMaterial().isLiquid()) { break; }
                    if (BeardBlocks.terrainBlock(up) || up == Blocks.GRASS_PATH || up == Blocks.SANDSTONE || up == Blocks.MYCELIUM || above.getMaterial() == Material.WOOD || above.getMaterial() == Material.LEAVES || !above.getMaterial().isSolid()) {
                        BeardBlocks.note(world, at, "Paving the road");
                        world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
                        cut++;
                        continue;
                    }
                    break;
                }
                if (profile[i] != Integer.MIN_VALUE) { filled += BeardBlocks.fillUnder(world, at, x, z, target - 1, target - 8); }
                at.setPos(x, target, z);
                if (profile[i] == Integer.MIN_VALUE) {
                    paved += deckRail(world, alongX, start + i, across, acrossLeast, acrossMost, target, planks, at);
                    if (!BeardKeep.holds(at.getX(), at.getY(), at.getZ())) {
                        world.setBlockState(at, deckState(alongX, start + i, across, acrossLeast, acrossMost, planks), 2);
                        paved++;
                    }
                    continue;
                }
                boolean earthy = base == Blocks.GRASS || base == Blocks.DIRT || base == Blocks.MYCELIUM || base == Blocks.GRASS_PATH || base == Blocks.AIR || !world.getBlockState(at).getMaterial().isSolid();
                IBlockState natural = chosenSurface ? path : pathForGround(world, x, z, path, gravel, earthy && !pier);
                IBlockState dressed = dressSurface(world, piece, alongX, alongX ? x : z, alongX ? z : x, (acrossLeast + acrossMost) / 2, natural);
                world.setBlockState(at, dressed != null ? dressed : natural, 2);
                paved++;
            }
            for (int side = 0; side < 2; side++) {
                int across = side == 0 ? acrossLeast - 1 : acrossMost + 1;
                int x = alongX ? start + i : across;
                int z = alongX ? across : start + i;
                if (profile[i] == Integer.MIN_VALUE || bridged[i]) { continue; }
                if (insidePlaza(x, z)) { continue; }
                at.setPos(x, profile[i], z);
                if (!clip.isVecInside(at)) { continue; }
                StructureStart holder = ContentBeard.current();
                if (holder != null && BeardPlots.underAnother(holder, piece, x, z)) { continue; }
                if (BeardKeep.holds(x, profile[i], z)) { continue; }
                IBlockState verge = world.getBlockState(at);
                if (verge.getMaterial().isLiquid()) { continue; }
                if (verge.getMaterial().isSolid()) {
                    at.setPos(x, profile[i] - 1, z);
                    if (world.getBlockState(at).getMaterial().isSolid() || world.getBlockState(at).getMaterial().isLiquid()) { continue; }
                    filled += BeardBlocks.fillBank(world, at, x, z, profile[i] - 1, profile[i] - 6, false);
                    continue;
                }
                filled += BeardBlocks.fillBank(world, at, x, z, profile[i], profile[i] - 5, false);
            }
        }
        if (stored && (alongX ? clip.minZ : clip.minX) <= acrossLeast - 1 && (alongX ? clip.maxZ : clip.maxX) >= acrossMost + 1) {
            for (int row = least; row <= most; row++) { graded.covered[row - start] = true; }
        }
        if ((cut + filled + paved > 0) && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Graded the road at {}, {} within its chunk: paved {} column(s), cut {} block(s) off bumps, filled {} into dips", box.minX, box.minZ, paved, cut, filled); }
    }

    public static void repairRoads(World world, StructureStart start) {
        if (!(world.getChunkProvider() instanceof ChunkProviderServer)) { return; }
        ChunkProviderServer provider = (ChunkProviderServer) world.getChunkProvider();
        for (StructureComponent piece : start.getComponents()) {
            if (!(piece instanceof StructureVillagePieces.Path) || !(piece instanceof RoadLayout)) { continue; }
            Grade grade = ((RoadLayout) piece).rdpl$layout();
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
                        ContentBeard.building(start);
                        try {
                            ContentBeard.fellFor(start, piece, world, patch);
                            BeardKeep.watch(world, piece, patch);
                            ((RoadLayout) piece).rdpl$repave(world, patch);
                            BeardKeep.learn(world);
                        }
                        finally { ContentBeard.building(null); }
                    }
                    row = band + 1;
                }
            }
        }
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
        for (int lift = 0; lift < 8; lift++) {
            IBlockState stood = world.getBlockState(at.setPos(alongX ? row : across, deckY, alongX ? across : row));
            if (!stood.getMaterial().isLiquid() && !(stood.getMaterial().isSolid() && clearable(stood))) { break; }
            deckY++;
        }
        at.setPos(alongX ? row : across, deckY, alongX ? across : row);
        if (world.getBlockState(at).getMaterial().isSolid()) { return 0; }
        int laid = deckRail(world, alongX, row, across, acrossLeast, acrossMost, deckY, planks, at);
        if (BeardKeep.holds(at.getX(), at.getY(), at.getZ())) { return laid; }
        world.setBlockState(at, deckState(alongX, row, across, acrossLeast, acrossMost, planks), 2);
        return laid + 1;
    }

    private static IBlockState deckState(boolean alongX, int row, int across, int acrossLeast, int acrossMost, IBlockState planks) {
        if (acrossMost - acrossLeast + 1 <= 3) { return planks; }
        int offset = Math.abs(across - (acrossLeast + acrossMost) / 2);
        if (offset == 0) {
            IBlockState center = pathBlock("villagePathCenterBlock", Config.worldgen.villagePathCenterBlock, planks);
            if (center != planks) {
                int dash = Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathCenterDash", Config.worldgen.villagePathCenterDash));
                if (dash <= 0 || Math.floorMod(row, dash + 1) != dash) { return ContentBeard.axised(center, alongX); }
            }
            return planks;
        }
        if (offset <= 1 + pathExtraWidth()) { return planks; }
        if (offset <= 1 + pathExtraWidth() + pathLineColumns()) { return ContentBeard.axised(pathBlock("villagePathLineBlock", Config.worldgen.villagePathLineBlock, planks), alongX); }
        IBlockState walk = pathBlock("villagePathSidewalkBlock", Config.worldgen.villagePathSidewalkBlock, planks);
        return pathBlock("villagePathBridgeSidewalkBlock", Config.worldgen.villagePathBridgeSidewalkBlock, walk);
    }

    private static int deckRail(World world, boolean alongX, int row, int across, int acrossLeast, int acrossMost, int deckY, IBlockState planks, BlockPos.MutableBlockPos at) {
        int span = acrossMost - acrossLeast + 1;
        if (span <= 3 || Math.abs(across - (acrossLeast + acrossMost) / 2) != (span - 1) / 2) { return 0; }
        IBlockState barrier = pathBlock("villagePathBridgeBarrierBlock", Config.worldgen.villagePathBridgeBarrierBlock, planks);
        if (barrier == planks) { return 0; }
        int height = Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villagePathBridgeBarrierHeight", Config.worldgen.villagePathBridgeBarrierHeight));
        int laid = 0;
        for (int y = deckY + 1; y <= deckY + height; y++) {
            at.setPos(alongX ? row : across, y, alongX ? across : row);
            if (world.getBlockState(at).getMaterial().isSolid()) { break; }
            world.setBlockState(at, barrier, 2);
            laid++;
        }
        at.setPos(alongX ? row : across, deckY, alongX ? across : row);
        return laid;
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
        if (role != 'r') {
            IBlockState tied = tieIn(world, piece, alongX, row, across, acrossCenter, core, path);
            if (tied != null) { return tied; }
        }
        if (role == 'c') {
            IBlockState center = pathBlock("villagePathCenterBlock", Config.worldgen.villagePathCenterBlock, path);
            if (center == path) { return path; }
            if (crossedBy(piece, alongX, row, acrossCenter)) { return path; }
            int dash = Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathCenterDash", Config.worldgen.villagePathCenterDash));
            if (dash > 0 && Math.floorMod(row, dash + 1) == dash) { return path; }
            return ContentBeard.axised(center, alongX);
        }
        if (role == 'l') { return ContentBeard.axised(pathBlock("villagePathLineBlock", Config.worldgen.villagePathLineBlock, path), alongX); }
        if (role == 's') { return pathBlock("villagePathSidewalkBlock", Config.worldgen.villagePathSidewalkBlock, path); }
        return path;
    }

    @Nullable private static IBlockState tieIn(World world, StructureComponent piece, boolean alongX, int row, int across, int acrossCenter, int core, IBlockState path) {
        List<StructureComponent> pieces = ContentBeard.components();
        if (pieces == null) { return null; }
        boolean outward = across > acrossCenter;
        for (StructureComponent other : pieces) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox road = other.getBoundingBox();
            boolean otherAlongX = BeardPlots.roadAlongX(road);
            if (otherAlongX == alongX) { continue; }
            int otherCore = 1 + pathExtraWidth();
            int otherCenter = alongX ? (road.minX + road.maxX) / 2 : (road.minZ + road.maxZ) / 2;
            int reach = Math.abs(row - otherCenter);
            if (reach > otherCore + pathLineColumns()) { continue; }
            int edge = alongX ? (outward ? road.minZ : road.maxZ) : (outward ? road.minX : road.maxX);
            int gap = outward ? edge - across : across - edge;
            if (gap < -1 || gap > 2 + pathLineColumns() + pathSidewalkWidth()) { continue; }
            if (reach <= otherCore) {
                PathIntersectDef def = ContentPathIntersects.forJunction(world, alongX ? otherCenter : acrossCenter, alongX ? acrossCenter : otherCenter);
                if (def != null) {
                    IBlockState fromMouth = mouthCell(def, across, row, otherCenter, otherCore, acrossCenter - core - 1, acrossCenter + core + 1, path);
                    if (fromMouth != null) { return fromMouth; }
                }
                return path;
            }
            return ContentBeard.axised(pathBlock("villagePathLineBlock", Config.worldgen.villagePathLineBlock, path), !alongX);
        }
        return null;
    }

    private static boolean crossedBy(StructureComponent piece, boolean alongX, int row, int acrossCenter) {
        List<StructureComponent> pieces = ContentBeard.components();
        if (pieces == null) { return false; }
        StructureBoundingBox own = piece.getBoundingBox();
        int reach = 2 + pathExtraWidth() + pathLineColumns() + pathSidewalkWidth();
        for (StructureComponent other : pieces) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox road = other.getBoundingBox();
            if (road.minX - 1 > own.maxX || own.minX - 1 > road.maxX || road.minZ - 1 > own.maxZ || own.minZ - 1 > road.maxZ) { continue; }
            boolean otherAlongX = BeardPlots.roadAlongX(road);
            if (otherAlongX == alongX) { continue; }
            int otherCenter = alongX ? (road.minX + road.maxX) / 2 : (road.minZ + road.maxZ) / 2;
            if (Math.abs(row - otherCenter) > 1 + pathExtraWidth()) { continue; }
            int least = alongX ? road.minZ : road.minX;
            int most = alongX ? road.maxZ : road.maxX;
            if (acrossCenter >= least - reach && acrossCenter <= most + reach) { return true; }
        }
        return false;
    }

    @Nullable public static IBlockState stampAt(World world, StructureComponent piece, boolean alongX, int row, int across, int acrossCenter, int core, IBlockState path) {
        List<StructureComponent> pieces = ContentBeard.components();
        if (pieces == null || Math.abs(across - acrossCenter) > core) { return null; }
        StructureBoundingBox own = piece.getBoundingBox();
        for (StructureComponent other : pieces) {
            if (other == piece || !(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox road = other.getBoundingBox();
            if (road.minX - 1 > own.maxX || own.minX - 1 > road.maxX || road.minZ - 1 > own.maxZ || own.minZ - 1 > road.maxZ) { continue; }
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
        return plazaMouth(world, alongX, row, across, acrossCenter, core, path);
    }

    @Nullable private static IBlockState plazaMouth(World world, boolean alongX, int row, int across, int acrossCenter, int core, IBlockState path) {
        int reach = ContentBeard.plazaReach();
        for (StructureBoundingBox well : BeardPlots.wellBoxes(ContentBeard.components())) {
            if (acrossCenter + core < (alongX ? well.minZ : well.minX) || acrossCenter - core > (alongX ? well.maxZ : well.maxX)) { continue; }
            int before = (alongX ? well.minX : well.minZ) - reach - 1;
            int after = (alongX ? well.maxX : well.maxZ) + reach + 1;
            if (row > before && row < after) { continue; }
            PathIntersectDef def = ContentPathIntersects.forJunction(world, (well.minX + well.maxX) / 2, (well.minZ + well.maxZ) / 2);
            if (def == null) { return null; }
            IBlockState fromMouth = mouthCell(def, row, across, acrossCenter, core, before, after, path);
            if (fromMouth != null) { return fromMouth; }
        }
        return null;
    }

    private static boolean insidePlaza(int x, int z) { return BeardPlots.insidePlaza(ContentBeard.components(), x, z); }

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

    @Nullable public static Grade chainGrade(World world, StructureComponent road, boolean alongX) {
        StructureBoundingBox box = road.getBoundingBox();
        return roadProfile(world, road, alongX, alongX ? box.minX : box.minZ, alongX ? box.maxX : box.maxZ, alongX ? box.minZ : box.minX, alongX ? box.maxZ : box.maxX, false);
    }

    public static int roadGradeBeside(World world, StructureBoundingBox box) {
        List<StructureComponent> pieces = ContentBeard.components();
        if (pieces == null) { return Integer.MIN_VALUE; }
        for (StructureComponent other : pieces) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox road = other.getBoundingBox();
            int gap = MathUtil.max(road.minX - box.maxX, box.minX - road.maxX, road.minZ - box.maxZ, box.minZ - road.maxZ);
            if (gap > 2) { continue; }
            boolean alongX = BeardPlots.roadAlongX(other);
            int start = alongX ? road.minX : road.minZ;
            Grade grade = other instanceof RoadLayout ? ((RoadLayout) other).rdpl$layout() : null;
            if (grade == null) { grade = roadProfile(world, other, alongX, start, alongX ? road.maxX : road.maxZ, alongX ? road.minZ : road.minX, alongX ? road.maxZ : road.maxX, true); }
            if (grade == null) { return Integer.MIN_VALUE; }
            int center = alongX ? (box.minX + box.maxX) / 2 : (box.minZ + box.maxZ) / 2;
            int row = Math.max(start, Math.min(start + grade.profile.length - 1, center));
            if (grade.profile[row - start] == Integer.MIN_VALUE) { continue; }
            return grade.profile[row - start] + 1;
        }
        return Integer.MIN_VALUE;
    }

    public static void roadApron(World world, @Nullable StructureComponent piece, boolean alongX, int start, int rowMost, int acrossLeast, int acrossMost, int[] profile, boolean[] held, boolean[] footed, boolean[] fixed, boolean[] bridged) {
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
            int otherLeast = alongX ? road.minX : road.minZ;
            int otherMost = alongX ? road.maxX : road.maxZ;
            int center = Math.max(start, Math.min(rowMost, (otherLeast + otherMost) / 2));
            boolean ending = otherLeast == rowMost + 1 || otherMost == start - 1;
            if (ending) {
                int crossRow = otherAlongX ? (own.minX + own.maxX) / 2 : (own.minZ + own.maxZ) / 2;
                Grade crossing = chainGrade(world, other, otherAlongX);
                int crossed = crossing == null ? Integer.MIN_VALUE : crossing.at(crossRow);
                int ownGrade = profile[center - start];
                int grade;
                if (crossed != Integer.MIN_VALUE) { grade = crossed; }
                else if (ownGrade != Integer.MIN_VALUE) { grade = ownGrade; }
                else { grade = Math.max(Grade.carried(profile, center - start), crossing == null ? Integer.MIN_VALUE : crossing.deckAt(crossRow)); }
                if (grade == Integer.MIN_VALUE) { continue; }
                if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} ends on the road at {}, {}, so its mouth rows around {} hold the junction's level, y {}", own.minX, own.minZ, road.minX, road.minZ, center, grade); }
                int reach = 1 + pathExtraWidth() + 3;
                for (int row = center - reach; row <= center + reach; row++) {
                    if (row < start || row > rowMost) { continue; }
                    profile[row - start] = grade;
                    held[row - start] = true;
                    fixed[row - start] = true;
                    bridged[row - start] = false;
                }
            }
            else {
                int grade = profile[center - start];
                if (grade == Integer.MIN_VALUE) { grade = Grade.carried(profile, center - start); }
                if (grade == Integer.MIN_VALUE) { continue; }
                if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The road at {}, {} flattens its junction with the road at {}, {} to one level, y {}, over rows {} to {}", own.minX, own.minZ, road.minX, road.minZ, grade, Math.max(start, otherLeast), Math.min(rowMost, otherMost)); }
                for (int row = otherLeast; row <= otherMost; row++) {
                    if (row < start || row > rowMost) { continue; }
                    profile[row - start] = grade;
                    held[row - start] = true;
                    fixed[row - start] = true;
                    bridged[row - start] = false;
                }
            }
            int squareLeast = alongX ? road.minX : road.minZ;
            int squareMost = alongX ? road.maxX : road.maxZ;
            for (int row = squareLeast; row <= squareMost; row++) {
                if (row < start || row > start + profile.length - 1) { continue; }
                footed[row - start] = true;
                bridged[row - start] = false;
            }
        }
    }

    public static void frontHold(@Nullable StructureComponent piece, boolean alongX, int start, int acrossLeast, int acrossMost, int[] profile, boolean[] held) {
        List<StructureComponent> pieces = ContentBeard.components();
        if (pieces == null) { return; }
        int rowMost = start + profile.length - 1;
        for (StructureComponent other : pieces) {
            if (other == piece || other instanceof StructureVillagePieces.Path) { continue; }
            StructureBoundingBox front = other.getBoundingBox();
            if ((alongX ? front.maxZ : front.maxX) < acrossLeast - 3 || (alongX ? front.minZ : front.minX) > acrossMost + 3) { continue; }
            int otherLeast = alongX ? front.minX : front.minZ;
            int otherMost = alongX ? front.maxX : front.maxZ;
            if (otherMost < start || otherLeast > rowMost) { continue; }
            int center = Math.max(start, Math.min(rowMost, (otherLeast + otherMost) / 2));
            int grade = profile[center - start];
            if (grade == Integer.MIN_VALUE) { continue; }
            int spanLo = Math.max(start, otherLeast);
            int spanHi = Math.min(rowMost, otherMost);
            boolean feasible = true;
            for (int j = 0; j < profile.length && feasible; j++) {
                if (!held[j] || profile[j] == Integer.MIN_VALUE) { continue; }
                int at = start + j;
                int away = at < spanLo ? spanLo - at : at > spanHi ? at - spanHi : 0;
                if (Math.abs(grade - profile[j]) > away) { feasible = false; }
            }
            if (!feasible) {
                if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The frontage of {} at {}, {} stands at y {}, which the road cannot reach at a walkable slope from its pinned rows, so it is not held to it", other.getClass().getSimpleName(), front.minX, front.minZ, grade); }
                continue;
            }
            int pinnedRows = 0;
            for (int row = spanLo; row <= spanHi; row++) {
                if (held[row - start] || profile[row - start] == Integer.MIN_VALUE) { continue; }
                profile[row - start] = grade;
                held[row - start] = true;
                pinnedRows++;
            }
            if (pinnedRows > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The frontage of {} at {}, {} holds {} row(s) of the road beside it to y {}, the grade it stands at", other.getClass().getSimpleName(), front.minX, front.minZ, pinnedRows, grade); }
        }
    }

    public static void clampToWell(World world, boolean alongX, int start, int acrossLeast, int acrossMost, int[] profile, boolean[] held) {
        int reach = ContentBeard.plazaReach();
        for (StructureBoundingBox well : BeardPlots.wellBoxes(ContentBeard.components())) {
            if (acrossMost < (alongX ? well.minZ : well.minX) - reach || acrossLeast > (alongX ? well.maxZ : well.maxX) + reach) { continue; }
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
            if (clamped > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Clamped {} road row(s) beside the well at {}, {} to its ground at y {}", clamped, well.minX, well.minZ, ground); }
        }
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
        return earthy || gravel.getBlock() == Blocks.CLAY ? path : gravel;
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
