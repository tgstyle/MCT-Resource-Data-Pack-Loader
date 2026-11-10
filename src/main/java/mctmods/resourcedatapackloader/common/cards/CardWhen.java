package mctmods.resourcedatapackloader.common.cards;

import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.environment.ContentSeasons;
import mctmods.resourcedatapackloader.common.environment.SeasonState;
import mctmods.resourcedatapackloader.common.portways.Portways;
import mctmods.resourcedatapackloader.common.util.entity.Advancements;
import mctmods.resourcedatapackloader.common.util.entity.Standing;
import mctmods.resourcedatapackloader.common.util.worldgen.BiomeNames;

import java.util.Collections;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class CardWhen {
    public List<String> biomes = List.of();
    public List<String> environments = List.of();
    public List<String> seasons = List.of();
    public String dimension = "";
    public int timeFrom = -1;
    public int timeTo = -1;
    public long dayAtLeast = -1L;
    public String advancement = "";
    public String gameMode = "";
    public String portway = "";
    public String network = "";
    public Standing standing = Standing.NONE;

    public boolean fails(ServerPlayer player) {
        if (!biomes.isEmpty() && !BiomeNames.matches(biomes, player.level().getBiome(player.blockPosition()))) { return true; }
        if (!environments.isEmpty() && Collections.disjoint(environments, ContentEnvironments.active(player.level().dimension().location()))) { return true; }
        if (!seasons.isEmpty() && !inSeason(player)) { return true; }
        if (timeFrom >= 0 && timeTo >= 0 && !CardPlace.within(CardPlace.timeOfDay(player.level()), timeFrom, timeTo)) { return true; }
        if (dayAtLeast >= 0L && CardPlace.day(player.level()) < dayAtLeast) { return true; }
        if (!advancement.isEmpty() && !Advancements.has(player, advancement)) { return true; }
        if (!gameMode.isEmpty() && !gameMode.equalsIgnoreCase(player.gameMode.getGameModeForPlayer().getName())) { return true; }
        if ((!portway.isEmpty() || !network.isEmpty()) && Portways.unknown(player, portway, network)) { return true; }
        return !standing.met(player);
    }

    private boolean inSeason(ServerPlayer player) {
        ResourceLocation id = dimension.isEmpty() ? player.level().dimension().location() : ResourceLocation.tryParse(CardPlace.dimensionId(dimension));
        SeasonState state = id == null ? null : ContentSeasons.state(id);
        if (state == null) { return false; }
        for (String season : seasons) {
            if (season.equalsIgnoreCase(state.name())) { return true; }
        }
        return false;
    }
}
