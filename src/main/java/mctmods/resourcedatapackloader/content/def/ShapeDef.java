package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.content.worldgen.ContentField;

import java.util.Collections;
import java.util.List;

public final class ShapeDef {
    public static final int MOST_REACH = 8;
    public static final String CLUSTER = "cluster";
    public static final String PLATE = "plate";
    public static final String GEODE = "geode";
    public static final String LARGEVEIN = "largevein";
    public static final String DECORATION = "decoration";
    public static final String TREE = "tree";
    public static final String VINES = "vines";
    public static final String BASIN = "basin";
    public static final String SPIRE = "spire";
    public static final String NODULE = "nodule";
    public static final String VENT = "vent";
    public static final String IMPRINT = "imprint";
    public static final String BELT = "belt";
    public static final String FIELD = "field";
    public static final String CIRCLE = "circle";
    public static final String SQUARE = "square";
    public static final String STRAIGHT = "straight";
    public static final String BELL = "bell";
    public static final String NEEDLE = "needle";
    public static final String NO_TURN = "none";
    public static final String QUARTER = "quarter";
    public static final String HALF = "half";
    public static final String THREEQUARTER = "threequarter";
    public static final String LEFTRIGHT = "leftright";
    public static final String FRONTBACK = "frontback";
    public final String type;
    public final AmountDef radius;
    public final AmountDef height;
    public final AmountDef width;
    public final String plane;
    public final boolean slim;
    public final String outline;
    public final String fill;
    public final List<String> surface;
    public final AmountDef stack;
    public final boolean seeSky;
    public final boolean checkStay;
    public final int scatterX;
    public final int scatterY;
    public final int scatterZ;
    public final String log;
    public final String leaves;
    public final boolean hanging;
    public final String structure;
    public String locateAs = "";
    public int[] at = null;
    public int fade = 0;
    public final List<PickDef> structures;
    public final List<PickDef> turns;
    public final List<PickDef> mirrors;
    public final String taper;
    public final int integrity;
    public final int rarity;
    public final boolean perChunk;
    public final ContentField field;
    public final float threshold;
    public final String lootTable;

    public ShapeDef(String type, AmountDef radius, AmountDef height, AmountDef width, String plane, boolean slim, String outline, String fill, List<String> surface, AmountDef stack, boolean seeSky, boolean checkStay, int scatterX, int scatterY, int scatterZ, String log, String leaves, boolean hanging, String structure, List<PickDef> structures, List<PickDef> turns, List<PickDef> mirrors, String taper, int integrity, int rarity, boolean perChunk, ContentField field, float threshold, String lootTable) {
        this.type = type;
        this.radius = radius;
        this.height = height;
        this.width = width;
        this.plane = plane;
        this.slim = slim;
        this.outline = outline;
        this.fill = fill;
        this.surface = surface;
        this.stack = stack;
        this.seeSky = seeSky;
        this.checkStay = checkStay;
        this.scatterX = scatterX;
        this.scatterY = scatterY;
        this.scatterZ = scatterZ;
        this.log = log;
        this.leaves = leaves;
        this.hanging = hanging;
        this.structure = structure;
        this.structures = structures;
        this.turns = turns;
        this.mirrors = mirrors;
        this.taper = taper;
        this.integrity = integrity;
        this.rarity = rarity;
        this.perChunk = perChunk;
        this.field = field;
        this.threshold = threshold;
        this.lootTable = lootTable;
    }

    public static ShapeDef cluster() {
        return new ShapeDef(CLUSTER, AmountDef.of(4), AmountDef.of(1), AmountDef.of(12), CIRCLE, false, "", "", Collections.emptyList(), AmountDef.of(1), true, true, 8, 4, 8, "", "", false, "", Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), STRAIGHT, 100, 0, false, null, 0.5F, "");
    }

    public boolean isRound() { return CIRCLE.equals(plane); }

    public boolean isHollow() { return !fill.isEmpty(); }
}
