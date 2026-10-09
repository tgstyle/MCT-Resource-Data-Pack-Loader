package mctmods.resourcedatapackloader.content.card;

import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.worldgen.ContentLocate;
import mctmods.resourcedatapackloader.util.BiomeNames;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Scores;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import java.util.List;
import javax.annotation.Nullable;

final class CardPlace {

    private CardPlace() {}

    static String dimensionId(String named) {
        if (named.isEmpty()) { return ""; }
        String id = ContentFormats.dimensionId(named);
        return id.contains(":") ? id : "minecraft:" + id;
    }

    static boolean elsewhere(String wanted, ServerPlayer player) { return !wanted.isEmpty() && !wanted.equals(dimensionName(player)); }

    static String dimensionName(ServerPlayer player) { return player.level().dimension().location().toString(); }

    static boolean biomeMatches(List<String> wanted, Holder<Biome> biome) {
        if (wanted.isEmpty()) { return true; }
        ResourceLocation name = biome.unwrapKey().map(ResourceKey::location).orElse(null);
        for (String one : wanted) {
            if (one.startsWith("#")) {
                TagKey<Biome> tag = BiomeNames.tag(one.substring(1));
                if (tag != null && biome.is(tag)) { return true; }
            }
            else if (name != null && (one.equals(name.toString()) || one.equals(name.getPath()))) { return true; }
        }
        return false;
    }

    static String biomeName(ServerPlayer player) { return player.level().getBiome(player.blockPosition()).unwrapKey().map(key -> key.location().toString()).orElse(""); }

    static boolean inStructure(ServerPlayer player, List<String> wanted, int radius) {
        ServerLevel level = player.serverLevel();
        BlockPos at = player.blockPosition();
        for (String name : wanted) {
            if (name.startsWith("#")) {
                ResourceLocation tag = Registered.tagId(name);
                if (tag != null && level.structureManager().getStructureWithPieceAt(at, TagKey.create(Registries.STRUCTURE, tag)).isValid()) { return true; }
                continue;
            }
            ResourceLocation id = ResourceLocation.tryParse(name);
            Structure structure = id == null ? null : level.registryAccess().registryOrThrow(Registries.STRUCTURE).get(id);
            if (structure != null) {
                if (level.structureManager().getStructureWithPieceAt(at, structure).isValid()) { return true; }
                continue;
            }
            BlockPos near = ContentLocate.nearest(level, name, at);
            if (near != null && near.distSqr(at) <= (double) radius * radius) { return true; }
        }
        return false;
    }

    @Nullable static Integer score(ServerPlayer player, String objective) {
        Scoreboard board = player.getScoreboard();
        Objective found = Scores.objective(board, objective);
        String owner = player.getScoreboardName();
        if (found == null || Scores.lacks(board, owner, found)) { return null; }
        return Scores.score(board, owner, found);
    }

    static long day(Level level) { return level.getDayTime() / Level.TICKS_PER_DAY; }

    static int timeOfDay(Level level) { return (int) (level.getDayTime() % Level.TICKS_PER_DAY); }
}
