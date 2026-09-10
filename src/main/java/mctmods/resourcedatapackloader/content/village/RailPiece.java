package mctmods.resourcedatapackloader.content.village;

import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRails;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoads;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.template.TemplateManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RailPiece extends StructureVillagePieces.Road {
    public static final String ID = "RdplRail";
    private int line;
    private boolean subway;
    private final List<StructureBoundingBox> stations = new ArrayList<>();
    private int[] stationTops = new int[0];
    @Nullable private BeardRoads.Grade grade;
    @Nullable private int[] rising;
    private boolean risingKnown = false;

    @SuppressWarnings("unused") public RailPiece() {}

    public RailPiece(StructureVillagePieces.Start start, StructureBoundingBox box, boolean alongX, int line, boolean subway) {
        super(start, 0);
        setCoordBaseMode(alongX ? EnumFacing.EAST : EnumFacing.SOUTH);
        this.boundingBox = box;
        this.line = line;
        this.subway = subway;
    }

    public boolean alongX() { return boundingBox.maxX - boundingBox.minX >= boundingBox.maxZ - boundingBox.minZ; }

    public int line() { return line; }

    public boolean subway() { return subway; }

    public List<StructureBoundingBox> stations() { return stations; }

    @Nullable public StructureBoundingBox station() { return stations.isEmpty() ? null : stations.get(0); }

    public void addStation(StructureBoundingBox box) {
        stations.add(box);
        stationTops = Arrays.copyOf(stationTops, stations.size());
        stationTops[stations.size() - 1] = Integer.MIN_VALUE;
    }

    public int stationTop(int which) { return which >= 0 && which < stationTops.length ? stationTops[which] : Integer.MIN_VALUE; }

    public void stationTop(int which, int y) {
        if (which >= 0 && which < stationTops.length) { stationTops[which] = y; }
    }

    public int rowLeast() { return alongX() ? boundingBox.minX : boundingBox.minZ; }

    public int rowMost() { return alongX() ? boundingBox.maxX : boundingBox.maxZ; }

    public int acrossLeast() { return alongX() ? boundingBox.minZ : boundingBox.minX; }

    public int acrossMost() { return alongX() ? boundingBox.maxZ : boundingBox.maxX; }

    @Nullable public BeardRoads.Grade grade(World world) {
        if (grade != null && (grade.start() != rowLeast() || grade.rows() != rowMost() - rowLeast() + 1)) { regrade(); }
        if (grade == null) { grade = BeardRails.profile(world, this); }
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

    public void regrade() { grade = null; risingKnown = false; rising = null; }

    @Override protected void writeStructureToNBT(@Nonnull NBTTagCompound tag) {
        super.writeStructureToNBT(tag);
        tag.setInteger("RdplLine", line);
        tag.setBoolean("RdplSubway", subway);
        if (!stations.isEmpty()) {
            int[] held = new int[stations.size() * 6];
            for (int at = 0; at < stations.size(); at++) {
                StructureBoundingBox box = stations.get(at);
                held[at * 6] = box.minX;
                held[at * 6 + 1] = box.minY;
                held[at * 6 + 2] = box.minZ;
                held[at * 6 + 3] = box.maxX;
                held[at * 6 + 4] = box.maxY;
                held[at * 6 + 5] = box.maxZ;
            }
            tag.setIntArray("RdplStations", held);
            tag.setIntArray("RdplStationTops", stationTops);
        }
        if (risingKnown) { tag.setIntArray("RdplRising", rising == null ? new int[0] : rising); }
        if (grade != null) { grade.write(tag); }
    }

    @Override protected void readStructureFromNBT(@Nonnull NBTTagCompound tag, @Nonnull TemplateManager templates) {
        super.readStructureFromNBT(tag, templates);
        line = tag.getInteger("RdplLine");
        subway = tag.getBoolean("RdplSubway");
        stations.clear();
        int[] held = tag.hasKey("RdplStations") ? tag.getIntArray("RdplStations") : tag.getIntArray("RdplStation");
        for (int at = 0; at + 5 < held.length; at += 6) { stations.add(new StructureBoundingBox(held[at], held[at + 1], held[at + 2], held[at + 3], held[at + 4], held[at + 5])); }
        stationTops = new int[stations.size()];
        Arrays.fill(stationTops, Integer.MIN_VALUE);
        int[] tops = tag.hasKey("RdplStationTops") ? tag.getIntArray("RdplStationTops")
                : tag.hasKey("RdplStationTop") ? new int[] {tag.getInteger("RdplStationTop")} : new int[0];
        System.arraycopy(tops, 0, stationTops, 0, Math.min(tops.length, stationTops.length));
        risingKnown = tag.hasKey("RdplRising");
        int[] rose = tag.getIntArray("RdplRising");
        rising = rose.length == 2 ? rose : null;
        grade = BeardRoads.Grade.read(tag);
    }

    @Override public boolean addComponentParts(@Nonnull World world, @Nonnull Random random, @Nonnull StructureBoundingBox clip) {
        BeardRails.lay(this, world, clip);
        return true;
    }
}
