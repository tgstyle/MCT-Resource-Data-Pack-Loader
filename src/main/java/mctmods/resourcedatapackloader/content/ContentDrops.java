package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.def.DropDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.state.BlockState;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentDrops {
    private static final Set<String> WARNED = new HashSet<>();

    private ContentDrops() {}

    public static void release(ServerLevel level, BlockPos pos, BlockState state) {
        ContentRegistry.BlockEntry entry = ContentRegistry.entry(state.getBlock());
        if (entry == null) { return; }
        release(level, pos, entry.variant());
    }

    public static void release(ServerLevel level, BlockPos pos, BlockVariant variant) {
        List<DropDef> pool = new ArrayList<>();
        for (DropDef drop : variant.drops()) {
            if (!drop.isEntity()) { continue; }
            if (drop.weighted()) { pool.add(drop); }
            else { spawn(level, pos, drop); }
        }
        DropDef chosen = pick(pool, level.getRandom());
        if (chosen != null) { spawn(level, pos, chosen); }
    }

    @Nullable public static DropDef pick(List<DropDef> pool, RandomSource random) {
        int total = 0;
        for (DropDef drop : pool) { total += Math.max(1, drop.weight()); }
        if (total <= 0) { return null; }
        int roll = random.nextInt(total);
        for (DropDef drop : pool) {
            roll -= Math.max(1, drop.weight());
            if (roll < 0) { return drop; }
        }
        return null;
    }

    private static void spawn(ServerLevel level, BlockPos pos, DropDef drop) {
        if (drop.entity() == null || 1 + level.getRandom().nextInt(100) > drop.chance()) { return; }
        EntityType<?> type = EntityType.byString(drop.entity().toString()).orElse(null);
        if (type == null) {
            if (WARNED.add(drop.entity().toString())) { ContentLog.LOGGER.error("Drop entity {} is not registered, that drop is skipped", drop.entity()); }
            return;
        }
        int amount = drop.amount().fixed() ? drop.amount().least() : drop.amount().least() + level.getRandom().nextInt(drop.amount().most() - drop.amount().least() + 1);
        for (int i = 0; i < amount; i++) { type.spawn(level, pos, MobSpawnType.MOB_SUMMONED); }
    }
}
