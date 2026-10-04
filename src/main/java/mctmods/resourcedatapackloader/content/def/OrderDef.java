package mctmods.resourcedatapackloader.content.def;

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

    public OrderDef(String name, Job job, String blocks, int area, int[] areaByTier, boolean deliversToSelf, int limit, int standing, int workers, int priority, List<String> takers, String sign, float speed, String tool) {
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
    }

    public int widest() {
        int widest = area;
        for (int radius : areaByTier) { widest = Math.max(widest, radius); }
        return widest;
    }

    public int reach(int tier) {
        if (areaByTier.length == 0) { return area; }
        return areaByTier[Math.max(0, Math.min(tier, areaByTier.length - 1))];
    }

    public enum Job { GATHER, MINE, FARM, HAUL }
}
