package mctmods.resourcedatapackloader.content.card;

import mctmods.resourcedatapackloader.content.worldgen.ContentLocate;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.DimensionManager;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;

final class CardPlace {
    private static final long DAY_TICKS = 24000L;
    private static final List<String> VANILLA_STRUCTURES = Arrays.asList("Village", "Temple", "Mansion", "Monument", "Mineshaft", "Stronghold", "Fortress", "EndCity");

    private CardPlace() {}

    static boolean inDimension(String wanted, int dimension) {
        if (wanted.isEmpty()) { return true; }
        try { return Integer.parseInt(wanted) == dimension; }
        catch (NumberFormatException ignored) { return bare(wanted).equals(bare(dimensionName(dimension))); }
    }

    static String dimensionName(int dimension) {
        if (!DimensionManager.isDimensionRegistered(dimension)) { return Integer.toString(dimension); }
        DimensionType type = DimensionManager.getProviderType(dimension);
        return type == null ? Integer.toString(dimension) : type.getName();
    }

    private static String bare(String name) {
        String lowered = name.trim().toLowerCase(Locale.ROOT);
        int colon = lowered.indexOf(':');
        return colon < 0 ? lowered : lowered.substring(colon + 1);
    }

    static boolean biomeMatches(List<String> wanted, Biome biome) {
        if (wanted.isEmpty()) { return true; }
        ResourceLocation name = biome.getRegistryName();
        for (String one : wanted) {
            if (one.startsWith("#")) {
                if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.getType(one.substring(1).toUpperCase(Locale.ROOT)))) { return true; }
            }
            else if (name != null && (one.equals(name.toString()) || one.equals(name.getPath()))) { return true; }
        }
        return false;
    }

    static String biomeName(EntityPlayerMP player) {
        ResourceLocation name = player.world.getBiome(player.getPosition()).getRegistryName();
        return name == null ? "" : name.toString();
    }

    static boolean inStructure(EntityPlayerMP player, List<String> wanted, int radius) {
        if (!(player.world instanceof WorldServer)) { return false; }
        WorldServer world = (WorldServer) player.world;
        BlockPos at = player.getPosition();
        for (String name : wanted) {
            if (VANILLA_STRUCTURES.contains(name)) {
                if (world.getChunkProvider().isInsideStructure(world, name, at)) { return true; }
                continue;
            }
            BlockPos near = ContentLocate.nearest(world, name, at);
            if (near != null && near.distanceSq(at) <= (double) radius * radius) { return true; }
        }
        return false;
    }

    @Nullable static Integer score(EntityPlayerMP player, String objective) {
        Scoreboard board = player.world.getScoreboard();
        ScoreObjective found = board.getObjective(objective);
        if (found == null || !board.entityHasObjective(player.getName(), found)) { return null; }
        return board.getOrCreateScore(player.getName(), found).getScorePoints();
    }

    static long day(World world) { return world.getWorldTime() / DAY_TICKS; }

    static int timeOfDay(World world) { return (int) (world.getWorldTime() % DAY_TICKS); }
}
