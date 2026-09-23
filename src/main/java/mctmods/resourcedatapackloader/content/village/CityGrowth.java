package mctmods.resourcedatapackloader.content.village;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoads;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IStructureStartGrow;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;

import java.util.*;
import javax.annotation.Nullable;

public final class CityGrowth {
    public static final int MARCH = 112;
    private static final int TRIES = 32;
    static final int VERGE = 6;
    private static final int PER_MARCH = 96;
    static boolean laying;
    static boolean bulbLaying;
    private static boolean alleyLaying;
    private static int give;

    private CityGrowth() {}

    public static boolean laying() { return laying; }

    public static boolean bulbLaying() { return bulbLaying; }

    public static void alleyLaying(boolean narrow) { alleyLaying = narrow; }

    public static boolean alleyLaying() { return alleyLaying; }

    public static int give() { return give; }

    public static int march() {
        int least = ContentVillages.plotsLeast();
        int wide = Math.max(13, ContentVillages.largestPlot());
        double filled = (double) least / PER_MARCH * ((double) (wide * wide) / (13 * 13));
        if (filled <= 1.0) { return MARCH; }
        return (int) (MARCH * Math.sqrt(filled));
    }

    public static int chunkRange() { return (march() + 112 + 16) >> 4; }

    public static void grow(StructureStart held, World world, Random rand, int size) {
        int least = ContentVillages.plotsLeast();
        List<StructureComponent> components = held.getComponents();
        if (components.isEmpty()) { return; }
        if (least <= 0) {
            backRows(held, rand);
            return;
        }
        backRowsFrom(components, 0, rand);
        int built = ContentVillages.plots(components);
        if (built >= least) { return; }
        int sizeFor = size + Math.max(16, least / 32);
        Set<Long> tried = new HashSet<>();
        int districts = 0;
        int attempts = 0;
        int allowed = Math.max(8 * TRIES, least + least / 2);
        while (built < least && attempts < allowed) {
            long site = CityDistricts.site(components, tried);
            if (site == Long.MIN_VALUE) { break; }
            attempts++;
            tried.add(site);
            if (CityDistricts.settle(world, rand, components, (int) (site >> 32), (int) site, sizeFor)) {
                districts++;
                built = ContentVillages.plots(components);
                tried.clear();
            }
        }
        if (built < least && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("District growth drained after {} attempt(s): {} sites too near a well, {} on unreadable ground, {} on built ground, {} beside another village", attempts, CityDistricts.settledCrowded, CityDistricts.settledUngrounded, CityDistricts.settledTaken, CityDistricts.settledNeighbored); }
        CityDistricts.settledCrowded = CityDistricts.settledUngrounded = CityDistricts.settledTaken = CityDistricts.settledNeighbored = 0;
        if (built < least) { built = infill(rand, components, least, sizeFor); }
        ((IStructureStartGrow) held).rdpl$updateBoundingBox();
        ContentLog.LOGGER.debug("The village at chunk {}, {} grew {} district(s) to {} plot(s) against the asked minimum of {}", held.getChunkPosX(), held.getChunkPosZ(), districts, built, least);
    }

    public static void backRows(StructureStart held, Random rand) {
        List<StructureComponent> components = held.getComponents();
        int seated = backRowsFrom(components, 0, rand);
        if (seated > 0) { ((IStructureStartGrow) held).rdpl$updateBoundingBox(); }
        ContentLog.LOGGER.debug("The village at chunk {}, {} seated {} plot(s) behind its front plots in a second pass, {} plot(s) in all", held.getChunkPosX(), held.getChunkPosZ(), seated, ContentVillages.plots(components));
    }

    static int backRowsFrom(List<StructureComponent> components, int from, Random rand) {
        if (!ContentControl.flag(ContentControl.VILLAGES, "villagePlotsBackRow", Config.worldgen.villagePlotsBackRow)) { return 0; }
        int most = ContentVillages.plotsMost();
        int built = ContentVillages.plots(components);
        int seated = 0;
        for (StructureComponent piece : components.subList(from, components.size()).toArray(new StructureComponent[0])) {
            if (!(piece instanceof ContentVillagePiece)) { continue; }
            if (most > 0 && built >= most) { break; }
            StructureVillagePieces.Start start = nearestStart(components, piece.getBoundingBox());
            if (start == null) { continue; }
            ContentVillagePiece back = ContentVillages.behind(start, components, rand, (ContentVillagePiece) piece);
            if (back == null) { continue; }
            components.add(back);
            built++;
            seated++;
        }
        return seated;
    }


    public static void roadsFirst(StructureStart held) {
        List<StructureComponent> components = held.getComponents();
        if (components.size() < 3 || !(components.get(0) instanceof StructureVillagePieces.Start)) { return; }
        List<StructureComponent> sorted = new ArrayList<>(components.size());
        sorted.add(components.get(0));
        for (StructureComponent piece : components) {
            if (piece != components.get(0) && piece instanceof StructureVillagePieces.Path) { sorted.add(piece); }
        }
        for (StructureComponent piece : components) {
            if (piece != components.get(0) && !(piece instanceof StructureVillagePieces.Path)) { sorted.add(piece); }
        }
        components.clear();
        components.addAll(sorted);
        ContentLog.LOGGER.debug("The village at chunk {}, {} builds its {} road(s) before its plots, so a plot settles onto ground the roads have already laid", held.getChunkPosX(), held.getChunkPosZ(), sorted.size());
    }

    public static boolean bulbWide(StructureComponent piece) {
        StructureBoundingBox box = piece.getBoundingBox();
        if (BeardRoads.drawnKeys(box) != null) { return false; }
        return Math.min(box.maxX - box.minX, box.maxZ - box.minZ) + 1 > BeardRoads.pathFullWidth();
    }

    public static int verge() { return VERGE; }

    static void drain(StructureVillagePieces.Start start, List<StructureComponent> components, Random rand) {
        while (!start.pendingRoads.isEmpty() || !start.pendingHouses.isEmpty()) {
            if (start.pendingRoads.isEmpty()) { start.pendingHouses.remove(rand.nextInt(start.pendingHouses.size())).buildComponent(start, components, rand); }
            else { start.pendingRoads.remove(rand.nextInt(start.pendingRoads.size())).buildComponent(start, components, rand); }
        }
    }

    private static int infill(Random rand, List<StructureComponent> components, int least, int sizeFor) {
        int built = ContentVillages.plots(components);
        laying = true;
        try {
            for (int round = 0; round < 16 && built < least; round++) {
                for (StructureComponent piece : components.toArray(new StructureComponent[0])) {
                    if (piece instanceof StructureVillagePieces.Start) { ((StructureVillagePieces.Start) piece).structureVillageWeightedPieceList = StructureVillagePieces.getStructureVillageWeightedPieceList(rand, sizeFor); }
                }
                for (StructureComponent piece : components.toArray(new StructureComponent[0])) {
                    if (!(piece instanceof StructureVillagePieces.Path)) { continue; }
                    StructureVillagePieces.Start near = nearestStart(components, piece.getBoundingBox());
                    if (near == null) { continue; }
                    piece.buildComponent(near, components, rand);
                    drain(near, components, rand);
                }
                int now = ContentVillages.plots(components);
                if (now == built) {
                    if (give >= 6) { break; }
                    give += 2;
                    ContentLog.LOGGER.debug("The village stands short at {} plot(s), so infill may now move {} block(s) more earth to terrace its lots", built, give);
                }
                else { ContentLog.LOGGER.debug("An infill round along the standing streets raised the village to {} plot(s) against the asked minimum of {}", now, least); }
                built = now;
            }
        }
        finally {
            laying = false;
            give = 0;
        }
        return built;
    }

    @Nullable private static StructureVillagePieces.Start nearestStart(List<StructureComponent> components, StructureBoundingBox box) {
        StructureVillagePieces.Start best = null;
        long bestAway = Long.MAX_VALUE;
        int x = (box.minX + box.maxX) / 2;
        int z = (box.minZ + box.maxZ) / 2;
        for (StructureComponent piece : components) {
            if (!(piece instanceof StructureVillagePieces.Start)) { continue; }
            StructureBoundingBox well = piece.getBoundingBox();
            int wx = (well.minX + well.maxX) / 2;
            int wz = (well.minZ + well.maxZ) / 2;
            long away = (long) (x - wx) * (x - wx) + (long) (z - wz) * (z - wz);
            if (away < bestAway) {
                bestAway = away;
                best = (StructureVillagePieces.Start) piece;
            }
        }
        return best;
    }
}
