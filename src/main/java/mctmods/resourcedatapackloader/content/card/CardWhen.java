package mctmods.resourcedatapackloader.content.card;

import mctmods.resourcedatapackloader.util.Advancements;
import mctmods.resourcedatapackloader.util.Standing;

import net.minecraft.entity.player.EntityPlayerMP;
import java.util.Collections;
import java.util.List;

final class CardWhen {
    List<String> biomes = Collections.emptyList();
    int timeFrom = -1;
    int timeTo = -1;
    long dayAtLeast = -1L;
    String advancement = "";
    String gameMode = "";
    Standing standing = Standing.NONE;

    boolean fails(EntityPlayerMP player) {
        if (!biomes.isEmpty() && !CardPlace.biomeMatches(biomes, player.world.getBiome(player.getPosition()))) { return true; }
        if (timeFrom >= 0 && timeTo >= 0 && !CardPlace.within(CardPlace.timeOfDay(player.world), timeFrom, timeTo)) { return true; }
        if (dayAtLeast >= 0L && CardPlace.day(player.world) < dayAtLeast) { return true; }
        if (!advancement.isEmpty() && !Advancements.has(player, advancement)) { return true; }
        if (!gameMode.isEmpty() && !gameMode.equalsIgnoreCase(player.interactionManager.getGameType().getName())) { return true; }
        return !standing.met(player);
    }
}
