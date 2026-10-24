package mctmods.resourcedatapackloader.content.card;

import mctmods.resourcedatapackloader.content.util.Portways;
import mctmods.resourcedatapackloader.util.Advancements;
import mctmods.resourcedatapackloader.util.BiomeNames;
import mctmods.resourcedatapackloader.util.Standing;

import net.minecraft.server.level.ServerPlayer;
import java.util.List;

public final class CardWhen {
    List<String> biomes = List.of();
    int timeFrom = -1;
    int timeTo = -1;
    long dayAtLeast = -1L;
    String advancement = "";
    String gameMode = "";
    String portway = "";
    String network = "";
    Standing standing = Standing.NONE;

    public boolean fails(ServerPlayer player) {
        if (!biomes.isEmpty() && !BiomeNames.matches(biomes, player.level().getBiome(player.blockPosition()))) { return true; }
        if (timeFrom >= 0 && timeTo >= 0 && !CardPlace.within(CardPlace.timeOfDay(player.level()), timeFrom, timeTo)) { return true; }
        if (dayAtLeast >= 0L && CardPlace.day(player.level()) < dayAtLeast) { return true; }
        if (!advancement.isEmpty() && !Advancements.has(player, advancement)) { return true; }
        if (!gameMode.isEmpty() && !gameMode.equalsIgnoreCase(player.gameMode.getGameModeForPlayer().getName())) { return true; }
        if ((!portway.isEmpty() || !network.isEmpty()) && Portways.unknown(player, portway, network)) { return true; }
        return !standing.met(player);
    }
}
