package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.card.CardPlace;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.def.SpawnGateDef;
import mctmods.resourcedatapackloader.util.Advancements;
import mctmods.resourcedatapackloader.util.Standing;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class ContentSpawnGate {
    private static final double REACH = 128.0D * 128.0D;

    private ContentSpawnGate() {}

    public static boolean barred(Entity entity) {
        if (!(entity.level() instanceof ServerLevel level)) { return false; }
        EntityVariantDef def = ContentEntities.def(entity);
        if (def != null && def.gate() != SpawnGateDef.NONE && fails(def.gate(), level, entity)) { return true; }
        for (SpawnGateDef lock : SpawnLocks.gates(entity)) {
            if (fails(lock, level, entity)) { return true; }
        }
        return false;
    }

    private static boolean fails(SpawnGateDef gate, ServerLevel level, Entity entity) {
        if (!gate.dimensions().isEmpty() && !inDimension(gate, level)) { return true; }
        if (gate.timed() && !CardPlace.within(CardPlace.timeOfDay(level), gate.timeFrom(), gate.timeTo())) { return true; }
        if (gate.dayAtLeast() >= 0L && CardPlace.day(level) < gate.dayAtLeast()) { return true; }
        if (!gate.structures().isEmpty() && !CardPlace.inStructure(level, entity.blockPosition(), gate.structures(), gate.radius())) { return true; }
        return gate.needsPlayer() && !playerNear(gate, level, entity);
    }

    private static boolean inDimension(SpawnGateDef gate, ServerLevel level) {
        for (String wanted : gate.dimensions()) {
            if (!CardPlace.elsewhere(wanted, level)) { return true; }
        }
        return false;
    }

    private static boolean playerNear(SpawnGateDef gate, ServerLevel level, Entity entity) {
        for (ServerPlayer player : level.players()) {
            if (player.isSpectator() || player.distanceToSqr(entity) > REACH) { continue; }
            if (!gate.advancement().isEmpty() && !Advancements.has(player, gate.advancement())) { continue; }
            Integer least = gate.scoreAtLeast();
            if (least != null) {
                Integer score = Standing.score(player, gate.objective());
                if (score == null || score < least) { continue; }
            }
            return true;
        }
        return false;
    }
}
