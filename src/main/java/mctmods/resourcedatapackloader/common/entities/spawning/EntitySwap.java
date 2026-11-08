package mctmods.resourcedatapackloader.common.entities.spawning;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.neoforged.neoforge.event.EventHooks;

public final class EntitySwap {
    private static final ThreadLocal<Boolean> SWAPPING = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private EntitySwap() {}

    public static boolean active() { return SWAPPING.get() == Boolean.TRUE; }

    public static boolean spawn(ServerLevel level, Entity was, EntityType<?> wanted, boolean needsRoom) {
        SWAPPING.set(Boolean.TRUE);
        try {
            Entity becomes = wanted.create(level);
            if (becomes == null) { return false; }
            becomes.moveTo(was.getX(), was.getY(), was.getZ(), was.getYRot(), was.getXRot());
            if (needsRoom && !level.noCollision(becomes)) { return false; }
            if (becomes instanceof Mob mob) { EventHooks.finalizeMobSpawn(mob, level, level.getCurrentDifficultyAt(becomes.blockPosition()), MobSpawnType.EVENT, null); }
            level.addFreshEntity(becomes);
            return true;
        }
        finally { SWAPPING.set(Boolean.FALSE); }
    }
}
