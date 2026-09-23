package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;

public final class CityCross {
    public static final int NONE = 0;
    public static final int WALK = 1;
    public static final int LINE = 2;
    public static final int CORE = 3;
    public static final int WEST = 1;
    public static final int EAST = 2;
    public static final int NORTH = 4;
    public static final int SOUTH = 8;
    private final int core;
    private final int lines;
    private final int walk;

    private CityCross(int core, int lines, int walk) {
        this.core = core;
        this.lines = lines;
        this.walk = walk;
    }

    public static CityCross of(CityPlan.Line line) { return ContentControl.onRoad(line.keys(), () -> of(line.width(), line.alley())); }

    public static CityCross of(int width, boolean alley) {
        int bare = (width - 1) / 2;
        if (alley) { return new CityCross(bare, 0, 0); }
        int lines = ContentCity.lineBlock().isEmpty() ? 0 : 1;
        int walk = ContentCity.sidewalkBlock().isEmpty() ? 0 : Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathSidewalkWidth", Config.worldgen.villagePathSidewalkWidth()));
        if (bare - lines - walk < 0) { return new CityCross(bare, 0, 0); }
        return new CityCross(bare - lines - walk, lines, walk);
    }

    public static int arms(CityGround ground, CityPlan plan, CityPlan.Line ew, CityPlan.Line ns) {
        int found = 0;
        for (CityPlan held : ContentCityStructureSite.plansOver(ground, plan, ns.at() - 1, ns.last() + 1, ew.at() - 1, ew.last() + 1)) { found |= arms(held, ew, ns); }
        return found;
    }

    private static int arms(CityPlan plan, CityPlan.Line ew, CityPlan.Line ns) {
        int found = 0;
        for (CityPlan.Line line : plan.alongX()) {
            if (line.alley() || line.middle() < ew.at() || line.middle() > ew.last()) { continue; }
            if (line.from() < ns.at() && line.to() >= ns.at() - 1) { found |= WEST; }
            if (line.to() > ns.last() && line.from() <= ns.last() + 1) { found |= EAST; }
        }
        for (CityPlan.Line line : plan.alongZ()) {
            if (line.alley() || line.middle() < ns.at() || line.middle() > ns.last()) { continue; }
            if (line.from() < ew.at() && line.to() >= ew.at() - 1) { found |= NORTH; }
            if (line.to() > ew.last() && line.from() <= ew.last() + 1) { found |= SOUTH; }
        }
        return found;
    }

    public int core() { return core; }

    public int lines() { return lines; }

    public int walk() { return walk; }

    public int curb() { return core + lines + walk; }

    public int lampOffset() { return walk > 0 ? curb() : curb() + 1; }

    public int role(int offset) {
        if (offset <= core) { return CORE; }
        if (offset <= core + lines) { return LINE; }
        if (offset <= curb()) { return WALK; }
        return NONE;
    }
}
