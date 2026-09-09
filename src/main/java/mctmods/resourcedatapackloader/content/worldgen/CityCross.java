package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;

public final class CityCross {
    public static final int NONE = 0;
    public static final int WALK = 1;
    public static final int LINE = 2;
    public static final int CORE = 3;
    private final int core;
    private final int lines;
    private final int walk;

    private CityCross(int core, int lines, int walk) {
        this.core = core;
        this.lines = lines;
        this.walk = walk;
    }

    public static CityCross of(int width, boolean alley) {
        int bare = (width - 1) / 2;
        if (alley) { return new CityCross(bare, 0, 0); }
        int lines = ContentCity.lineBlock().isEmpty() ? 0 : 1;
        int walk = ContentCity.sidewalkBlock().isEmpty() ? 0 : Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathSidewalkWidth", Config.worldgen.villagePathSidewalkWidth()));
        if (bare - lines - walk < 0) { return new CityCross(bare, 0, 0); }
        return new CityCross(bare - lines - walk, lines, walk);
    }

    public int core() { return core; }

    public int curb() { return core + lines + walk; }

    public int lampOffset() { return walk > 0 ? curb() : curb() + 1; }

    public int role(int offset) {
        if (offset <= core) { return CORE; }
        if (offset <= core + lines) { return LINE; }
        if (offset <= curb()) { return WALK; }
        return NONE;
    }
}
