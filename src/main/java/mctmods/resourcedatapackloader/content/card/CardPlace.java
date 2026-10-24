package mctmods.resourcedatapackloader.content.card;

import mctmods.resourcedatapackloader.compat.LineCompat;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.worldgen.ContentLocate;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Registered;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.SharedConstants;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import java.util.List;

public final class CardPlace {
    public static final int NEAR = 32;

    private CardPlace() {}

    public static String dimensionId(String named) {
        if (named.isEmpty()) { return ""; }
        String id = ContentFormats.dimensionId(named);
        return id.contains(":") ? id : "minecraft:" + id;
    }

    static boolean elsewhere(String wanted, ServerPlayer player) { return elsewhere(wanted, player.level()); }

    public static boolean elsewhere(String wanted, Level level) { return !wanted.isEmpty() && !wanted.equals(dimensionName(level)); }

    public static boolean inAny(List<String> wanted, Level level) {
        for (String name : wanted) {
            if (!elsewhere(name, level)) { return true; }
        }
        return false;
    }

    static String dimensionName(ServerPlayer player) { return dimensionName(player.level()); }

    private static String dimensionName(Level level) { return level.dimension().identifier().toString(); }

    static String biomeName(ServerPlayer player) { return player.level().getBiome(player.blockPosition()).unwrapKey().map(key -> key.identifier().toString()).orElse(""); }

    static boolean inStructure(ServerPlayer player, List<String> wanted, int radius) { return inStructure(player.level(), player.blockPosition(), wanted, radius); }

    public static boolean inStructure(ServerLevel level, BlockPos at, List<String> wanted, int radius) {
        for (String name : wanted) {
            if (name.startsWith("#")) {
                Identifier tag = Registered.tagId(name);
                if (tag != null && level.structureManager().getStructureWithPieceAt(at, TagKey.create(Registries.STRUCTURE, tag)).isValid()) { return true; }
                continue;
            }
            Identifier id = Identifier.tryParse(name);
            Structure structure = id == null ? null : level.registryAccess().lookupOrThrow(Registries.STRUCTURE).getValue(id);
            if (structure != null) {
                if (LineCompat.pieceAt(level.structureManager(), at, structure).isValid()) { return true; }
                continue;
            }
            BlockPos near = ContentLocate.nearest(level, name, at);
            if (near != null && near.distSqr(at) <= (double) radius * radius) { return true; }
        }
        return false;
    }

    public static long day(Level level) { return level.getOverworldClockTime() / SharedConstants.TICKS_PER_GAME_DAY; }

    public static int timeOfDay(Level level) { return (int) (level.getOverworldClockTime() % SharedConstants.TICKS_PER_GAME_DAY); }

    public static int tickOf(JsonObject json, String member) { return json.has(member) ? Math.floorMod(Json.getInt(json, member), SharedConstants.TICKS_PER_GAME_DAY) : -1; }

    public static boolean within(int time, int from, int to) { return from <= to ? time >= from && time <= to : time >= from || time <= to; }
}
