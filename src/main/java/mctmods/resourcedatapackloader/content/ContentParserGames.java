package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.compat.Compat;
import mctmods.resourcedatapackloader.content.def.DiceDef;
import mctmods.resourcedatapackloader.content.def.ItemGiveDef;
import mctmods.resourcedatapackloader.content.def.RoundResetDef;
import mctmods.resourcedatapackloader.content.def.TeamDef;
import mctmods.resourcedatapackloader.content.def.TurnsDef;
import mctmods.resourcedatapackloader.content.def.RaidDef;
import mctmods.resourcedatapackloader.content.def.ScoreDef;
import mctmods.resourcedatapackloader.pack.port.Ids;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;

import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.SharedConstants;
import net.minecraft.util.Mth;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.world.scores.Team;
import net.minecraft.world.BossEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.util.GsonHelper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;

public final class ContentParserGames {
    private ContentParserGames() {}

    @Nullable public static ScoreDef scoreFile(Identifier key, String contents) {
        JsonObject json = Json.object(key, contents, "score file");
        if (json == null) { return null; }
        String name = Json.getString(json, "name", key.getPath()).trim();
        if (name.isEmpty() || name.length() > 16) {
            ContentLog.LOGGER.error("Score file {} names the objective '{}', and an objective name is 1 to 16 characters, so it is left out", key, name);
            return null;
        }
        String wanted = Json.getString(json, "criterion", "dummy").trim();
        ObjectiveCriteria criterion = ObjectiveCriteria.byName(Ids.criterion(wanted)).orElse(null);
        if (criterion == null) {
            ContentLog.LOGGER.error("Objective {} scores on '{}', which is not a criterion the game knows, so it is left out. dummy, deathCount, playerKillCount, totalKillCount, health and any stat name are the ones there are", name, wanted);
            return null;
        }
        String slot = Json.getString(json, "display", "").trim();
        slot = "belowName".equals(slot) ? "below_name" : slot;
        if (!slot.isEmpty() && displaySlotUnknown(slot)) {
            ContentLog.LOGGER.error("Objective {} asks to be shown in '{}', which is not list, sidebar, belowName, below_name or sidebar.team.<color>, so it is not shown", name, slot);
            slot = "";
        }
        ObjectiveCriteria.RenderType render = json.has("render") ? ObjectiveCriteria.RenderType.byId(Json.getString(json, "render", "integer").trim()) : null;
        JsonObject results = Json.getJsonObject(json, "results", new JsonObject());
        JsonObject points = Json.getJsonObject(json, "points", new JsonObject());
        JsonObject ends = Json.getJsonObject(json, "ends", new JsonObject());
        JsonObject opens = Json.getJsonObject(json, "opens", new JsonObject());
        Map<String, Integer> kills = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : Json.getJsonObject(points, "kill", new JsonObject()).entrySet()) { kills.put(entry.getKey().trim(), Json.asInt(entry.getValue(), entry.getKey(), 0)); }
        return new ScoreDef(name, Json.getString(json, "displayName", name), criterion, slot, render,
                Json.getBoolean(json, "teamTotals", true),
                Json.getBoolean(json, "individuals", false),
                Map.copyOf(kills),
                Json.getInt(points, "death", 0),
                Json.getInt(ends, "atScore", 0),
                Json.getInt(ends, "afterMinutes", 0),
                Json.getInt(ends, "afterRounds", 0),
                Json.getBoolean(results, "card", false),
                Json.getString(results, "title", name + " results"),
                Json.getString(results, "icon", "").trim(),
                Json.getString(results, "image", "").trim(),
                cardColor(results, name),
                Math.max(SharedConstants.TICKS_PER_SECOND, Json.getInt(results, "seconds", 8) * SharedConstants.TICKS_PER_SECOND),
                Json.getBoolean(json, "carries", false),
                Json.getBoolean(ends, "resets", false),
                Math.max(0, Json.getInt(ends, "intermissionSeconds", 10)),
                Json.getString(json, "awardsTo", "").trim(),
                Json.getBoolean(ends, "locksTeams", true),
                Json.getInt(points, "ownKill", 0),
                Json.getString(ends, "intermissionSays", "Round cooldown {seconds}"),
                Json.getString(ends, "startsSays", "Round starting in {seconds}"),
                opensBy(opens, key),
                Json.getString(opens, "says", "Waiting for {leader} to start the round"),
                Json.getString(opens, "leaderSays", "Type /rdpl round start"),
                lobbyAt(key, opens),
                Json.getBoolean(opens, "lobbyJoins", false),
                Json.getString(opens, "joinsSays", "Round is in progress, you can join after it ends"),
                Json.getBoolean(ends, "lastStanding", false),
                Json.getString(ends, "outSays", "You are out until the round ends"),
                roundReset(key, Json.getJsonObject(json, "reset", new JsonObject())),
                Json.getBoolean(json, "tiebreak", false),
                turns(key, json),
                Json.getBoolean(points, "raid", false));
    }

    @Nullable private static TurnsDef turns(Identifier key, JsonObject json) {
        if (!json.has("turns")) { return null; }
        JsonObject turns = Json.getJsonObject(json, "turns");
        String order = Json.choice(turns, "order", TurnsDef.FIXED, "Score file " + key + " turns", TurnsDef.ORDERS);
        String held = Json.choice(turns, "held", TurnsDef.FROZEN, "Score file " + key + " turns", TurnsDef.HOLDS);
        return new TurnsDef(order, Math.max(1, Json.getInt(turns, "seconds", 60)), Math.max(0, Json.getInt(turns, "gapSeconds", 3)), held,
                Math.max(0, Json.getInt(turns, "endsAtScore", 0)), Math.max(0, Json.getInt(turns, "cycles", 0)), Json.names(turns, "mobTags"), Json.names(turns, "mobTypes"));
    }

    private static RoundResetDef roundReset(Identifier key, JsonObject reset) {
        String owner = "Score file " + key + " reset";
        String lead = Json.choice(reset, "lead", RoundResetDef.NONE, owner, List.of(RoundResetDef.NONE, RoundResetDef.NOW, RoundResetDef.VOTE));
        String players = Json.choice(reset, "players", RoundResetDef.NONE, owner, List.of(RoundResetDef.NONE, RoundResetDef.VOTE));
        return new RoundResetDef(lead, RoundResetDef.VOTE.equals(players), Json.strings(reset, "teams"),
                Mth.clamp(Json.getInt(reset, "passPercent", 51), 1, 100),
                Math.max(5, Json.getInt(reset, "voteSeconds", 30)),
                Math.max(0, Json.getInt(reset, "cooldownSeconds", 60)),
                Json.getString(reset, "leadSays", "{player} reset the round"),
                Json.getString(reset, "voteSays", "{player} calls a vote to reset the round: /rdpl round vote yes or no, {seconds} seconds"),
                Json.getString(reset, "tallySays", "Reset the round? {yes} yes, {no} no, {seconds}"),
                Json.getString(reset, "passSays", "The vote passed, so the round is reset"),
                Json.getString(reset, "failSays", "The vote failed, so the round goes on"));
    }

    private static String opensBy(JsonObject opens, Identifier key) { return Json.choice(opens, "by", ScoreDef.AUTO, "Score file " + key + " opens", List.of(ScoreDef.AUTO, ScoreDef.LEADER)); }

    @Nullable private static <T> T named(T[] values, Function<T, String> name, String asked) {
        for (T held : values) {
            if (name.apply(held).equalsIgnoreCase(asked)) { return held; }
        }
        return null;
    }

    @Nullable private static ScoreDef.Place lobbyAt(Identifier key, JsonObject opens) {
        String asked = Json.getString(opens, "lobby", "").trim();
        ResourceKey<Level> dimension = Level.OVERWORLD;
        int comma = asked.indexOf(',');
        int colon = comma < 0 ? -1 : asked.lastIndexOf(':', comma);
        if (colon > 0) {
            String named = asked.substring(0, colon).trim();
            Identifier id = Identifier.tryParse(ContentFormats.dimensionId(named));
            if (id == null) {
                ContentLog.LOGGER.error("Score file {} gives opens.lobby '{}', whose dimension is not a dimension id, so the lobby has no area of its own", key, asked);
                return null;
            }
            dimension = ResourceKey.create(Registries.DIMENSION, id);
            asked = asked.substring(colon + 1);
        }
        int[] at = point(key, asked, "Score file {} gives opens.lobby '{}', which is not three whole numbers x,y,z, so the lobby has no area of its own");
        return at == null ? null : new ScoreDef.Place(dimension, at[0], at[1], at[2]);
    }

    private static int cardColor(JsonObject results, String name) {
        String asked = Json.getString(results, "background", "").trim();
        return asked.isEmpty() ? 0x1E2630 : ContentParser.color(asked, name + " results background") & 0xFFFFFF;
    }

    @Nullable public static RaidDef raidFile(Identifier key, String contents) {
        JsonObject json = Json.object(key, contents, "raid file");
        if (json == null) { return null; }
        String omen = Json.getString(json, "omen", "").trim();
        if (omen.isEmpty()) {
            ContentLog.LOGGER.error("Raid {} names no omen effect, and nothing else starts a raid, so it is left out", key);
            return null;
        }
        String colorName = Json.getString(json, "color", "red").trim().toUpperCase(Locale.ROOT);
        BossEvent.BossBarColor color = BossEvent.BossBarColor.RED;
        try { color = BossEvent.BossBarColor.valueOf(colorName); }
        catch (IllegalArgumentException notABarColor) { ContentLog.LOGGER.error("Raid {} asks for the bar color '{}', which is not pink, blue, red, green, yellow, purple or white, so it is red", key, colorName); }
        List<List<RaidDef.Group>> waves = new ArrayList<>();
        if (json.has("waves")) {
            for (JsonElement wave : Json.getJsonArray(json, "waves")) {
                if (!wave.isJsonArray()) {
                    ContentLog.LOGGER.error("Raid {} has a wave that is not a list of groups, skipping it", key);
                    continue;
                }
                List<RaidDef.Group> groups = new ArrayList<>();
                for (JsonElement group : wave.getAsJsonArray()) {
                    if (!group.isJsonObject() || Json.getString(group.getAsJsonObject(), "entity", "").trim().isEmpty()) {
                        ContentLog.LOGGER.error("Raid {} has a group in wave {} that names no entity, skipping it", key, waves.size() + 1);
                        continue;
                    }
                    groups.add(new RaidDef.Group(Json.getString(group.getAsJsonObject(), "entity", "").trim(), ContentParser.amount(group.getAsJsonObject(), "count", 1, 1)));
                }
                if (!groups.isEmpty()) { waves.add(List.copyOf(groups)); }
            }
        }
        if (waves.isEmpty()) {
            ContentLog.LOGGER.error("Raid {} has no wave with anyone in it, so it is left out", key);
            return null;
        }
        return new RaidDef(key, omen, Json.getString(json, "name", "Raid"), color, List.copyOf(waves),
                Math.max(1, Json.getInt(json, "waveDelay", 300)),
                Math.max(8, Json.getInt(json, "spawnDistance", 32)),
                Math.max(16, Json.getInt(json, "reach", 96)),
                Math.max(0, Json.getInt(json, "timeout", 48000)),
                Json.getString(json, "sound", "").trim(),
                Json.getString(json, "wins", "").trim(),
                Json.getString(json, "loses", "").trim(),
                Json.strings(json, "bell"),
                Json.getInt(json, "points", 0),
                Json.getString(json, "loot", "").trim());
    }

    @Nullable public static TeamDef teamFile(Identifier key, String contents) {
        JsonObject json = Json.object(key, contents, "team file");
        if (json == null) { return null; }
        String name = Json.getString(json, "name", key.getPath()).trim();
        if (name.isEmpty() || name.length() > 16) {
            ContentLog.LOGGER.error("Team file {} names the team '{}', and a team name is 1 to 16 characters, so the team is left out", key, name);
            return null;
        }
        ChatFormatting color = Compat.teamColor(Json.getString(json, "color", "white"));
        if (color == null) {
            ContentLog.LOGGER.error("Team {} asks for the color '{}', which is not one of the sixteen text colors, so it is white", name, Json.getString(json, "color", ""));
            color = ChatFormatting.WHITE;
        }
        boolean friendlyFire = Json.getBoolean(json, "friendlyFire", false);
        return new TeamDef(name, Json.getString(json, "displayName", name), color,
                Json.getString(json, "prefix", ""), Json.getString(json, "suffix", ""),
                friendlyFire, Json.getBoolean(json, "mobFriendlyFire", friendlyFire),
                Json.getBoolean(json, "seeFriendlyInvisibles", true),
                visible(json, "nameTags", key), visible(json, "deathMessages", key), collision(json, key),
                Json.names(json, "entities"), Json.names(json, "players"), box(json, key),
                Json.getBoolean(json, "joinable", true),
                leadWay(json, name), Json.getString(json, "leadOn", "").trim(), Json.getString(json, "leadIs", "").trim(),
                Json.getString(json, "leadSays", "You are the current round leader"), Json.getString(json, "leadRuns", "").trim(),
                Json.getBoolean(json, "balance", false), Json.getBoolean(json, "scoreboard", true),
                Math.max(0, Json.getInt(json, "picks", 0)), Json.names(json, "picksFrom"), gives(key, json),
                standIn(json), standInAt(key, json), point(key, Json.getString(json, "spawn", ""), "Team file {} gives spawn '{}', which is not three whole numbers x,y,z, so the side has no spawn of its own"));
    }

    @Nullable static int[] point(Identifier key, String asked, String refused) {
        String at = asked.trim();
        if (at.isEmpty()) { return null; }
        String[] parts = at.split(",");
        if (parts.length == 3) {
            try { return new int[] { Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()), Integer.parseInt(parts[2].trim()) }; }
            catch (NumberFormatException notNumbers) { ContentLog.LOGGER.debug("{} holds {}, which does not read as whole numbers", key, at); }
        }
        ContentLog.LOGGER.error(refused, key, at);
        return null;
    }

    private static String standIn(JsonObject json) {
        if (!GsonHelper.isObjectNode(json, "standIn")) { return ""; }
        return Json.getString(Json.getJsonObject(json, "standIn"), "entity", "").trim();
    }

    @Nullable private static int[] standInAt(Identifier key, JsonObject json) {
        if (!GsonHelper.isObjectNode(json, "standIn")) { return null; }
        String at = Json.getString(Json.getJsonObject(json, "standIn"), "at", "");
        int[] found = point(key, at, "Team file {} gives standIn an 'at' of '{}', which is not three whole numbers x,y,z, so no stand-in is kept");
        if (found == null && at.trim().isEmpty()) { ContentLog.LOGGER.error("Team file {} gives standIn no 'at', so no stand-in is kept", key); }
        return found;
    }

    private static List<ItemGiveDef> gives(Identifier key, JsonObject json) {
        List<ItemGiveDef> values = new ArrayList<>();
        if (!json.has("gives")) { return values; }
        for (JsonElement held : Json.getJsonArray(json, "gives")) {
            if (held.isJsonPrimitive()) { values.add(new ItemGiveDef(held.getAsString().trim(), 1, false)); }
            else if (held.isJsonObject()) { values.add(new ItemGiveDef(Json.getString(held.getAsJsonObject(), "item", "").trim(), Math.max(1, Json.getInt(held.getAsJsonObject(), "count", 1)), Json.getBoolean(held.getAsJsonObject(), "unbreakable", false))); }
            else { ContentLog.LOGGER.error("Team file {} lists something under gives that is neither an item name nor an object, skipping it", key); }
        }
        return values;
    }

    private static String leadWay(JsonObject json, String team) { return Json.choice(json, "lead", TeamDef.NONE, "Team " + team, List.of(TeamDef.NONE, TeamDef.FIRST, TeamDef.TOP_SCORE, TeamDef.APPOINTED, TeamDef.VOTE, TeamDef.CLAIM)); }

    private static Team.Visibility visible(JsonObject json, String field, Identifier key) {
        String asked = Json.getString(json, field, "always").trim();
        Team.Visibility held = named(Team.Visibility.values(), visibility -> visibility.name, asked);
        if (held != null) { return held; }
        ContentLog.LOGGER.error("Team file {} sets {} to '{}', which is not always, never, hideForOtherTeams or hideForOwnTeam, so it is always", key, field, asked);
        return Team.Visibility.ALWAYS;
    }

    private static Team.CollisionRule collision(JsonObject json, Identifier key) {
        String asked = Json.getString(json, "collision", "always").trim();
        Team.CollisionRule held = named(Team.CollisionRule.values(), rule -> rule.name, asked);
        if (held != null) { return held; }
        ContentLog.LOGGER.error("Team file {} sets collision to '{}', which is not always, never, pushOtherTeams or pushOwnTeam, so it is always", key, asked);
        return Team.CollisionRule.ALWAYS;
    }

    @Nullable private static int[] box(JsonObject json, Identifier key) {
        if (!json.has("spawnBox")) { return null; }
        if (!json.get("spawnBox").isJsonArray() || json.getAsJsonArray("spawnBox").size() != 6) {
            ContentLog.LOGGER.error("Team file {} has a spawnBox that is not six whole numbers, x y z to x y z, so nothing joins by where it spawns", key);
            return null;
        }
        int[] box = new int[6];
        int at = 0;
        for (JsonElement entry : json.getAsJsonArray("spawnBox")) { box[at++] = Json.asInt(entry, "spawnBox", 0); }
        for (int side = 0; side < 3; side++) {
            if (box[side] > box[side + 3]) {
                int swap = box[side];
                box[side] = box[side + 3];
                box[side + 3] = swap;
            }
        }
        return box;
    }

    private static boolean displaySlotUnknown(String slot) { return DisplaySlot.CODEC.byName(slot) == null; }

    @Nullable public static DiceDef diceFile(Identifier key, String contents) {
        JsonObject json = Json.object(key, contents, "dice file");
        if (json == null) { return null; }
        Map<String, String> says = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : Json.getJsonObject(json, "says", new JsonObject()).entrySet()) {
            if (entry.getValue().isJsonPrimitive()) { says.put(entry.getKey().trim(), entry.getValue().getAsString()); }
            else { ContentLog.LOGGER.error("Dice file {} gives the wording '{}' something that is not text, so it is left out", key, entry.getKey()); }
        }
        Map<String, Map<String, Integer>> dice = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> die : Json.getJsonObject(json, "dice", new JsonObject()).entrySet()) {
            if (!die.getValue().isJsonObject()) {
                ContentLog.LOGGER.error("Dice file {} gives the die '{}' something that is not a set of faces and weights, so it is left out", key, die.getKey());
                continue;
            }
            Map<String, Integer> faces = new LinkedHashMap<>();
            for (Map.Entry<String, JsonElement> face : die.getValue().getAsJsonObject().entrySet()) {
                int weight = face.getValue().isJsonPrimitive() && face.getValue().getAsJsonPrimitive().isNumber() ? face.getValue().getAsInt() : 0;
                if (weight < 1) { ContentLog.LOGGER.error("Dice file {} weighs the face '{}' of '{}' at something that is not a whole number of 1 or more, so the face is left out", key, face.getKey(), die.getKey()); }
                else { faces.put(face.getKey(), weight); }
            }
            if (faces.isEmpty()) { ContentLog.LOGGER.error("Dice file {} gives the die '{}' no faces, so it is left out", key, die.getKey()); }
            else { dice.put(die.getKey().trim(), faces); }
        }
        Map<String, List<String>> decks = new LinkedHashMap<>();
        JsonObject held = Json.getJsonObject(json, "decks", new JsonObject());
        Set<String> fixed = new HashSet<>();
        for (Map.Entry<String, JsonElement> deck : held.entrySet()) {
            boolean object = deck.getValue().isJsonObject();
            List<String> cards = object ? Json.names(deck.getValue().getAsJsonObject(), "cards") : Json.names(held, deck.getKey());
            if (cards.isEmpty()) { ContentLog.LOGGER.error("Dice file {} gives the deck '{}' no cards, so it is left out", key, deck.getKey()); }
            else {
                decks.put(deck.getKey().trim(), cards);
                if (object && !Json.getBoolean(deck.getValue().getAsJsonObject(), "reshuffle", true)) { fixed.add(deck.getKey().trim()); }
            }
        }
        return new DiceDef(Json.getString(json, "audience", "").trim(), says, dice, decks, fixed);
    }
}
