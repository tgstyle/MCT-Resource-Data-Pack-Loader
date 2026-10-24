package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.card.CardPlace;
import mctmods.resourcedatapackloader.content.def.SpawnGateDef;
import mctmods.resourcedatapackloader.util.Advancements;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public final class ContentSpawnGate {
    private static final double REACH = 128.0D * 128.0D;

    private ContentSpawnGate() {}

    public static boolean barred(Entity entity) {
        SpawnGateDef gate = ContentEntities.spawnGate(entity);
        if (gate != SpawnGateDef.NONE && fails(gate, entity)) { return true; }
        for (SpawnGateDef lock : SpawnLocks.gates(entity)) {
            if (fails(lock, entity)) { return true; }
        }
        return false;
    }

    private static boolean fails(SpawnGateDef gate, Entity entity) {
        World world = entity.world;
        if (!gate.dimensions.isEmpty() && !CardPlace.inAny(gate.dimensions, world.provider.getDimension())) { return true; }
        if (gate.timed() && !CardPlace.within(CardPlace.timeOfDay(world), gate.timeFrom, gate.timeTo)) { return true; }
        if (gate.dayAtLeast >= 0L && CardPlace.day(world) < gate.dayAtLeast) { return true; }
        if (!gate.structures.isEmpty() && !CardPlace.inStructure(world, entity.getPosition(), gate.structures, gate.radius)) { return true; }
        return gate.needsPlayer() && !playerNear(gate, entity);
    }

    private static boolean playerNear(SpawnGateDef gate, Entity entity) {
        for (EntityPlayer player : entity.world.playerEntities) {
            if (player.isSpectator() || player.getDistanceSq(entity) > REACH) { continue; }
            if (!gate.advancement.isEmpty() && !Advancements.has(player, gate.advancement)) { continue; }
            if (gate.standing.met(player)) { return true; }
        }
        return false;
    }
}
