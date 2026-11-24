package mctmods.resourcedatapackloader.common.villages;

import mctmods.resourcedatapackloader.common.worldgen.structure.city.plot.BeardPlots;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.rail.BeardRails;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.rail.BeardRailsGrade;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.rail.BeardRailsLay;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.road.BeardLinks;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.road.BeardRoads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.template.TemplateManager;

public class RailPiece extends RoadPiece {
    public static final String ID = "RdplRail";
    private static final String RISING = "RdplRising";
    private int line;
    private boolean subway;
    private final List<StructureBoundingBox> stations = new ArrayList<>();
    private int[] stationTops = new int[0];
    @Nullable private BeardRoads.Grade grade;
    @Nullable private int[] rising;
    private boolean risingKnown = false;
    @Nullable private int[] trunk;
    private int[] ends = new int[0];

    @SuppressWarnings("unused") public RailPiece() {}

    public RailPiece(StructureBoundingBox box, boolean alongX, boolean subway) {
        lay(box, alongX);
        this.subway = subway;
    }

    public RailPiece(StructureVillagePieces.Start start, StructureBoundingBox box, boolean alongX, int line, boolean subway) {
        super(start);
        lay(box, alongX);
        this.line = line;
        this.subway = subway;
    }

    @Override public boolean alongX() { return trunk != null ? BeardLinks.Trunk.of(trunk).alongX() : boundingBox.maxX - boundingBox.minX >= boundingBox.maxZ - boundingBox.minZ; }

    public int line() { return line; }

    public boolean subway() { return subway; }

    @Nullable public int[] trunk() { return trunk; }

    public void trunk(int[] packed) { trunk = packed; }

    public int[] ends() { return ends; }

    public void end(int[] packed) {
        int[] grown = Arrays.copyOf(ends, ends.length + packed.length);
        System.arraycopy(packed, 0, grown, ends.length, packed.length);
        ends = grown;
    }

    private int gradeLeast() { return trunk != null ? BeardLinks.Trunk.of(trunk).least() : rowLeast(); }

    private int gradeRows() { return trunk != null ? BeardLinks.Trunk.of(trunk).most() - gradeLeast() + 1 : rowMost() - rowLeast() + 1; }

    public List<StructureBoundingBox> stations() { return stations; }

    @Nullable public StructureBoundingBox station() { return stations.isEmpty() ? null : stations.get(0); }

    public void addStation(StructureBoundingBox box) {
        stations.add(box);
        stationTops = Arrays.copyOf(stationTops, stations.size());
        stationTops[stations.size() - 1] = Integer.MIN_VALUE;
    }

    public void dropStation(int which) {
        stations.remove(which);
        int[] kept = new int[stations.size()];
        System.arraycopy(stationTops, 0, kept, 0, which);
        System.arraycopy(stationTops, which + 1, kept, which, kept.length - which);
        stationTops = kept;
    }

    public int stationTop(int which) { return which >= 0 && which < stationTops.length ? stationTops[which] : Integer.MIN_VALUE; }

    public void stationTop(int which, int y) { if (which >= 0 && which < stationTops.length) { stationTops[which] = y; } }

    public int acrossLeast() { return BeardPlots.acrossLeast(boundingBox, alongX()); }

    public int acrossMost() { return BeardPlots.acrossMost(boundingBox, alongX()); }

    @Nullable public BeardRoads.Grade grade(World world) {
        if (grade != null && (grade.start() != gradeLeast() || grade.rows() != gradeRows())) { regrade(); }
        if (grade == null) { grade = BeardRailsGrade.profile(world, this); }
        return grade;
    }

    @Nullable public int[] rising(World world) {
        if (!risingKnown) {
            rising = BeardRails.surfacing(world, this);
            risingKnown = true;
        }
        return rising;
    }

    @Nullable public int[] risingKnown() { return risingKnown ? rising : null; }

    public void regrade() {
        grade = null;
        risingKnown = false;
        rising = null;
    }

    @Override protected void writeStructureToNBT(@Nonnull NBTTagCompound tag) {
        super.writeStructureToNBT(tag);
        tag.setInteger("RdplLine", line);
        tag.setBoolean("RdplSubway", subway);
        if (!stations.isEmpty()) {
            int[] held = new int[stations.size() * 6];
            for (int at = 0; at < stations.size(); at++) { System.arraycopy(stations.get(at).toNBTTagIntArray().getIntArray(), 0, held, at * 6, 6); }
            tag.setIntArray("RdplStations", held);
            tag.setIntArray("RdplStationTops", stationTops);
        }
        if (risingKnown) { tag.setIntArray(RISING, rising == null ? new int[0] : rising); }
        if (trunk != null) { tag.setIntArray("RdplTrunk", trunk); }
        if (ends.length > 0) { tag.setIntArray("RdplLinkEnds", ends); }
        if (grade != null) { grade.write(tag); }
    }

    @Override protected void readStructureFromNBT(@Nonnull NBTTagCompound tag, @Nonnull TemplateManager templates) {
        super.readStructureFromNBT(tag, templates);
        line = tag.getInteger("RdplLine");
        subway = tag.getBoolean("RdplSubway");
        stations.clear();
        int[] held = tag.hasKey("RdplStations") ? tag.getIntArray("RdplStations") : tag.getIntArray("RdplStation");
        for (int at = 0; at + 5 < held.length; at += 6) { stations.add(new StructureBoundingBox(Arrays.copyOfRange(held, at, at + 6))); }
        stationTops = new int[stations.size()];
        Arrays.fill(stationTops, Integer.MIN_VALUE);
        int[] tops = tag.hasKey("RdplStationTops") ? tag.getIntArray("RdplStationTops")
                : tag.hasKey("RdplStationTop") ? new int[] {tag.getInteger("RdplStationTop")} : new int[0];
        System.arraycopy(tops, 0, stationTops, 0, Math.min(tops.length, stationTops.length));
        risingKnown = tag.hasKey(RISING);
        int[] rose = tag.getIntArray(RISING);
        rising = rose.length == 2 ? rose : null;
        trunk = tag.hasKey("RdplTrunk") ? tag.getIntArray("RdplTrunk") : null;
        ends = tag.getIntArray("RdplLinkEnds");
        grade = BeardRoads.Grade.read(tag);
    }

    @Override public boolean addComponentParts(@Nonnull World world, @Nonnull Random random, @Nonnull StructureBoundingBox clip) {
        BeardRailsLay.lay(this, world, clip);
        return true;
    }
}
