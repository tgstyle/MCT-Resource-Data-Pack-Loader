package mctmods.resourcedatapackloader.common.entities.spawning;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.event.ForgeEventFactory;

public final class EntitySwap {
    private static final ThreadLocal<Boolean> SWAPPING = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private EntitySwap() {}

    public static boolean active() { return SWAPPING.get() == Boolean.TRUE; }

    public static boolean spawn(ServerLevel level, EntityType<?> wanted, Entity at, boolean needsRoom) {
        SWAPPING.set(Boolean.TRUE);
        try {
            Entity becomes = wanted.create(level);
            if (becomes == null) { return false; }
            becomes.moveTo(at.getX(), at.getY(), at.getZ(), at.getYRot(), at.getXRot());
            if (needsRoom && !level.noCollision(becomes)) { return false; }
            if (becomes instanceof Mob mob) { ForgeEventFactory.onFinalizeSpawn(mob, level, level.getCurrentDifficultyAt(becomes.blockPosition()), MobSpawnType.EVENT, null, null); }
            return level.addFreshEntity(becomes);
        }
        finally { SWAPPING.set(Boolean.FALSE); }
    }
}
