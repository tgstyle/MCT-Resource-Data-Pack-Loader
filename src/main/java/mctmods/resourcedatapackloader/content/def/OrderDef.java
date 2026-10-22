package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.math.MathHelper;
import java.util.List;

public final class OrderDef {
    public final String name;
    public final Job job;
    public final String blocks;
    public final int area;
    public final int[] areaByTier;
    public final boolean deliversToSelf;
    public final int limit;
    public final int standing;
    public final int workers;
    public final int priority;
    public final List<String> takers;
    public final String sign;
    public final float speed;
    public final String tool;
    public final String wins;
    public final int points;
    public final String loot;

    public OrderDef(String name, Job job, String blocks, int area, int[] areaByTier, boolean deliversToSelf, int limit, int standing, int workers, int priority, List<String> takers, String sign, float speed, String tool, String wins, int points, String loot) {
        this.name = name;
        this.job = job;
        this.blocks = blocks;
        this.area = area;
        this.areaByTier = areaByTier;
        this.deliversToSelf = deliversToSelf;
        this.limit = limit;
        this.standing = standing;
        this.workers = workers;
        this.priority = priority;
        this.takers = takers;
        this.sign = sign;
        this.speed = speed;
        this.tool = tool;
        this.wins = wins;
        this.points = points;
        this.loot = loot;
    }

    public int widest() {
        int widest = area;
        for (int radius : areaByTier) { widest = Math.max(widest, radius); }
        return widest;
    }

    public int reach(int tier) {
        if (areaByTier.length == 0) { return area; }
        return areaByTier[MathHelper.clamp(tier, 0, areaByTier.length - 1)];
    }

    public enum Job { GATHER, MINE, FARM, HAUL }
}
