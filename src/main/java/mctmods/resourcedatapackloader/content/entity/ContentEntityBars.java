package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.def.BossBarDef;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.util.BossBars;

import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

final class ContentEntityBars {
    private static final long EVERY = 10L;
    private static final Map<UUID, ServerBossEvent> HELD = new HashMap<>();

    private ContentEntityBars() {}

    static void tick(LivingEntity living, EntityVariantDef def) {
        BossBarDef spec = ContentEntityPhases.bar(living, def);
        if (spec == null || !(living.level() instanceof ServerLevel level)) { return; }
        if (!living.isAlive()) {
            drop(living);
            return;
        }
        ServerBossEvent bar = HELD.get(living.getUUID());
        boolean fresh = bar == null;
        if (fresh) {
            bar = BossBars.create(spec, living.getDisplayName());
            HELD.put(living.getUUID(), bar);
        }
        if (!fresh && level.getGameTime() % EVERY != 0L) { return; }
        BossBars.progress(bar, living.getHealth() / living.getMaxHealth());
        BossBars.sync(bar, BossBars.near(level, living.getX(), living.getY(), living.getZ(), def.tracking().range()));
    }

    static void drop(Entity entity) {
        ServerBossEvent bar = HELD.remove(entity.getUUID());
        if (bar != null) { BossBars.clear(bar); }
    }
}
