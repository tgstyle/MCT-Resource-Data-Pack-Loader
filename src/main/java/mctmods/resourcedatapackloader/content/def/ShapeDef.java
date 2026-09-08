package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.content.worldgen.ContentField;

import java.util.List;
import javax.annotation.Nullable;

public record ShapeDef(String type, AmountDef radius, AmountDef height, AmountDef width, String plane, boolean slim, String outline, String fill,
        List<String> surface, AmountDef stack, boolean seeSky, boolean checkStay, int scatterX, int scatterY, int scatterZ, String log, String leaves,
        boolean hanging, String structure, List<PickDef> structures, List<PickDef> turns, List<PickDef> mirrors, String taper, int integrity,
        int rarity, boolean perChunk, @Nullable ContentField field, float threshold, int fade, String lootTable, String locateAs, @Nullable int[] at,
        String pattern, float density, String rich, String poor) {
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
    public static final String VEIN = "vein";
    public static final String DEFAULT = "default";
    public static final String BANDED = "banded";
    public static final String TUBE = "tube";
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
    public static final int BELT_RARITY = 400;

    public static ShapeDef cluster() {
        return new ShapeDef(CLUSTER, AmountDef.of(6), AmountDef.of(1), AmountDef.of(12), CIRCLE, false, "", "", List.of(), AmountDef.of(1), true, true,
                8, 4, 8, "", "", false, "", List.of(), List.of(), List.of(), STRAIGHT, 100, 0, false, null, 0.5F, 0, "", "", null, DEFAULT, 1.0F, "", "");
    }

    public boolean isRound() { return CIRCLE.equals(plane); }

    public boolean isHollow() { return !fill.isEmpty(); }

    public boolean wholeChunk() { return BELT.equals(type) || FIELD.equals(type) || VEIN.equals(type); }

    @Nullable public int[] pinnedAt() { return at != null && at.length == 2 ? at : null; }
}
