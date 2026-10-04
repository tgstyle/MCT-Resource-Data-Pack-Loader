package mctmods.resourcedatapackloader.content.def;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import java.util.List;
import javax.annotation.Nullable;

public record OrderDef(String name, Job job, @Nullable TagKey<Item> blocks, int area, List<Integer> areaByTier, boolean deliversToSelf, int limit, int standing, int workers, int priority, List<String> takers, String sign,
                       float speed, String tool) {
    public int widest() {
        int widest = area;
        for (int radius : areaByTier) { widest = Math.max(widest, radius); }
        return widest;
    }

    public int reach(int tier) {
        if (areaByTier.isEmpty()) { return area; }
        return areaByTier.get(Math.clamp(tier, 0, areaByTier.size() - 1));
    }

    public enum Job { GATHER, MINE, FARM, HAUL }
}
