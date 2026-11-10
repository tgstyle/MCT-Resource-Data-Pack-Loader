package mctmods.resourcedatapackloader.common.cards;

import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.environment.ContentSeasons;
import mctmods.resourcedatapackloader.common.environment.SeasonState;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.common.portways.Portways;
import mctmods.resourcedatapackloader.common.util.entity.Advancements;
import mctmods.resourcedatapackloader.common.util.entity.Standing;
import mctmods.resourcedatapackloader.common.util.worldgen.BiomeNames;

import java.util.Collections;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public final class CardWhen {
    public List<String> biomes = Collections.emptyList();
    public List<String> environments = Collections.emptyList();
    public List<String> seasons = Collections.emptyList();
    public String dimension = "";
    public int timeFrom = -1;
    public int timeTo = -1;
    public long dayAtLeast = -1L;
    public String advancement = "";
    public String gameMode = "";
    public String portway = "";
    public String network = "";
    public Standing standing = Standing.NONE;

    public boolean fails(EntityPlayerMP player) {
        if (!biomes.isEmpty() && !BiomeNames.matches(biomes, player.world.getBiome(player.getPosition()))) { return true; }
        if (!environments.isEmpty() && Collections.disjoint(environments, ContentEnvironments.active(player.dimension))) { return true; }
        if (!seasons.isEmpty() && !inSeason(player)) { return true; }
        if (timeFrom >= 0 && timeTo >= 0 && !CardPlace.within(CardPlace.timeOfDay(player.world), timeFrom, timeTo)) { return true; }
        if (dayAtLeast >= 0L && CardPlace.day(player.world) < dayAtLeast) { return true; }
        if (!advancement.isEmpty() && !Advancements.has(player, advancement)) { return true; }
        if (!gameMode.isEmpty() && !gameMode.equalsIgnoreCase(player.interactionManager.getGameType().getName())) { return true; }
        if ((!portway.isEmpty() || !network.isEmpty()) && Portways.unknown(player, portway, network)) { return true; }
        return !standing.met(player);
    }

    private boolean inSeason(EntityPlayerMP player) {
        int id = player.dimension;
        if (!dimension.isEmpty()) {
            DimensionDef def = ContentDimensions.all().get(new ResourceLocation(dimension));
            if (def == null) { return false; }
            id = def.id;
        }
        SeasonState state = ContentSeasons.state(id);
        if (state == null) { return false; }
        for (String season : seasons) {
            if (season.equalsIgnoreCase(state.name())) { return true; }
        }
        return false;
    }
}
