package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.def.BossBarDef;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.util.BossBars;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.WorldServer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

final class ContentEntityBars {
    private static final long EVERY = 10L;
    private static final long STALE = 40L;
    private static final Map<UUID, Held> HELD = new HashMap<>();

    private static final class Held {
        final BossInfoServer bar;
        long seen;

        Held(BossInfoServer bar, long seen) {
            this.bar = bar;
            this.seen = seen;
        }
    }

    private ContentEntityBars() {}

    static void tick(EntityLivingBase living, EntityVariantDef def) {
        BossBarDef spec = def.bossBar;
        if (spec == null || !(living.world instanceof WorldServer)) { return; }
        UUID id = living.getUniqueID();
        if (!living.isEntityAlive()) {
            drop(id);
            return;
        }
        long now = living.world.getTotalWorldTime();
        Held held = HELD.get(id);
        boolean fresh = held == null;
        if (fresh) {
            held = new Held(BossBars.create(spec, living.getDisplayName()), now);
            HELD.put(id, held);
        }
        held.seen = now;
        if (!fresh && now % EVERY != 0L) { return; }
        BossBars.progress(held.bar, living.getHealth() / living.getMaxHealth());
        BossBars.sync(held.bar, BossBars.near((WorldServer) living.world, living.posX, living.posY, living.posZ, def.trackingRange));
    }

    static void sweep(long now) {
        Iterator<Held> each = HELD.values().iterator();
        while (each.hasNext()) {
            Held held = each.next();
            if (now - held.seen < STALE) { continue; }
            BossBars.clear(held.bar);
            each.remove();
        }
    }

    private static void drop(UUID id) {
        Held held = HELD.remove(id);
        if (held != null) { BossBars.clear(held.bar); }
    }
}
