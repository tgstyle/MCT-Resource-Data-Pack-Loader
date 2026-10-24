package mctmods.resourcedatapackloader.content.card;

import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentLocate;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import java.util.Arrays;
import java.util.List;

public final class CardPlace {
    public static final int NEAR = 32;
    private static final List<String> VANILLA_STRUCTURES = Arrays.asList("Village", "Temple", "Mansion", "Monument", "Mineshaft", "Stronghold", "Fortress", "EndCity");

    private CardPlace() {}

    public static boolean elsewhere(String wanted, int dimension) {
        if (wanted.isEmpty()) { return false; }
        try { return Integer.parseInt(wanted) != dimension; }
        catch (NumberFormatException ignored) { return !bare(wanted).equals(bare(dimensionName(dimension))); }
    }

    public static boolean inAny(List<String> wanted, int dimension) {
        for (String name : wanted) {
            if (!elsewhere(name, dimension)) { return true; }
        }
        return false;
    }

    static String dimensionName(int dimension) {
        if (!DimensionManager.isDimensionRegistered(dimension)) { return Integer.toString(dimension); }
        DimensionType type = DimensionManager.getProviderType(dimension);
        return type == null ? Integer.toString(dimension) : type.getName();
    }

    private static String bare(String name) {
        String lowered = Settings.lower(name);
        int colon = lowered.indexOf(':');
        return colon < 0 ? lowered : lowered.substring(colon + 1);
    }

    static String biomeName(EntityPlayerMP player) {
        ResourceLocation name = player.world.getBiome(player.getPosition()).getRegistryName();
        return name == null ? "" : name.toString();
    }

    static boolean inStructure(EntityPlayerMP player, List<String> wanted, int radius) { return inStructure(player.world, player.getPosition(), wanted, radius); }

    public static boolean inStructure(World level, BlockPos at, List<String> wanted, int radius) {
        if (!(level instanceof WorldServer)) { return false; }
        WorldServer world = (WorldServer) level;
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

    public static long day(World world) { return world.getWorldTime() / DimensionTraitsDef.VANILLA_DAY; }

    public static int timeOfDay(World world) { return (int) (world.getWorldTime() % DimensionTraitsDef.VANILLA_DAY); }

    public static int tickOf(JsonObject json, String member) { return json.has(member) ? Math.floorMod(Json.getInt(json, member), (int) DimensionTraitsDef.VANILLA_DAY) : -1; }

    public static boolean within(int time, int from, int to) { return from <= to ? time >= from && time <= to : time >= from || time <= to; }
}
