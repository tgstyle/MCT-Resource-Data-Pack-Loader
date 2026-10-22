package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.*;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Settings;
import static mctmods.resourcedatapackloader.util.Json.strings;

import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.BossInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentParserGames {
    private static final List<String> RESET_LEADS = Collections.unmodifiableList(Arrays.asList(RoundResetDef.NONE, RoundResetDef.NOW, RoundResetDef.VOTE));
    private static final List<String> RESET_PLAYERS = Collections.unmodifiableList(Arrays.asList(RoundResetDef.NONE, RoundResetDef.VOTE));
    private static final List<String> OPENS = Collections.unmodifiableList(Arrays.asList("auto", "leader"));
    private static final List<String> LEADS = Collections.unmodifiableList(Arrays.asList(TeamDef.LEAD_NONE, TeamDef.LEAD_FIRST, TeamDef.LEAD_TOP_SCORE, TeamDef.LEAD_APPOINTED, TeamDef.LEAD_VOTE, TeamDef.LEAD_CLAIM));

    private ContentParserGames() {}

    @Nullable public static ScoreDef scoreFile(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "score file");
        if (json == null) { return null; }
        String name = Json.getString(json, "name", key.getPath()).trim();
        if (name.isEmpty() || name.length() > 16) {
            ContentLog.LOGGER.error("Score file {} names the objective '{}', and an objective name is 1 to 16 characters, so it is left out", key, name);
            return null;
        }
        String wanted = Json.getString(json, "criterion", "dummy").trim();
        IScoreCriteria criterion = IScoreCriteria.INSTANCES.get(wanted);
        if (criterion == null) {
            ContentLog.LOGGER.error("Objective {} scores on '{}', which is not a criterion the game knows, so it is left out. dummy, deathCount, playerKillCount, totalKillCount, health and any stat. or achievement. name are the ones there are", name, wanted);
            return null;
        }
        String slot = Json.getString(json, "display", "").trim();
        if (!slot.isEmpty() && Scoreboard.getObjectiveDisplaySlotNumber(slot) < 0) {
            ContentLog.LOGGER.error("Objective {} asks to be shown in '{}', which is not list, sidebar, belowName or sidebar.team.<color>, so it is not shown", name, slot);
            slot = "";
        }
        IScoreCriteria.EnumRenderType render = null;
        if (json.has("render")) {
            render = IScoreCriteria.EnumRenderType.getByName(Json.getString(json, "render", "integer").trim());
        }
        JsonObject results = Json.getJsonObject(json, "results", new JsonObject());
        JsonObject points = Json.getJsonObject(json, "points", new JsonObject());
        JsonObject ends = Json.getJsonObject(json, "ends", new JsonObject());
        JsonObject opens = Json.getJsonObject(json, "opens", new JsonObject());
        Map<String, Integer> kills = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : Json.getJsonObject(points, "kill", new JsonObject()).entrySet()) {
            kills.put(entry.getKey().trim(), Json.asInt(entry.getValue(), entry.getKey(), 0));
        }
        return new ScoreDef(name, Json.getString(json, "displayName", name), criterion, slot, render,
                Json.getBoolean(json, "teamTotals", true),
                Json.getBoolean(json, "individuals", false),
                kills,
                Json.getInt(points, "death", 0),
                Json.getInt(ends, "atScore", 0),
                Json.getInt(ends, "afterMinutes", 0),
                Json.getInt(ends, "afterRounds", 0),
                Json.getBoolean(results, "card", false),
                Json.getString(results, "title", name + " results"),
                Json.getString(results, "icon", "").trim(),
                Json.getString(results, "image", "").trim(),
                cardColor(results, name),
                Math.max(20, Json.getInt(results, "seconds", 8) * 20),
                Json.getBoolean(json, "carries", false),
                Json.getBoolean(ends, "resets", false),
                Math.max(0, Json.getInt(ends, "intermissionSeconds", 10)),
                Json.getString(json, "awardsTo", "").trim(),
                Json.getBoolean(ends, "locksTeams", true),
                Json.getInt(points, "ownKill", 0),
                Json.getString(ends, "intermissionSays", "Round cooldown {seconds}"),
                Json.getString(ends, "startsSays", "Round starting in {seconds}"),
                opensBy(json, key),
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
                Json.getBoolean(points, "raid", false),
                Json.getBoolean(points, "order", false));
    }

    @Nullable private static TurnsDef turns(ResourceLocation key, JsonObject json) {
        if (!json.has("turns")) { return null; }
        JsonObject turns = Json.getJsonObject(json, "turns");
        String order = Json.choice(turns, "order", TurnsDef.FIXED, "Score file " + key + " turns", TurnsDef.ORDERS);
        String held = Json.choice(turns, "held", TurnsDef.FROZEN, "Score file " + key + " turns", TurnsDef.HOLDS);
        return new TurnsDef(order, Math.max(1, Json.getInt(turns, "seconds", 60)), Math.max(0, Json.getInt(turns, "gapSeconds", 3)), held,
                Math.max(0, Json.getInt(turns, "endsAtScore", 0)), Math.max(0, Json.getInt(turns, "cycles", 0)), Json.names(turns, "mobTags"), Json.names(turns, "mobTypes"));
    }

    private static RoundResetDef roundReset(ResourceLocation key, JsonObject reset) {
        String owner = "Score file " + key + " reset";
        String lead = Json.choice(reset, "lead", RoundResetDef.NONE, owner, RESET_LEADS);
        String players = Json.choice(reset, "players", RoundResetDef.NONE, owner, RESET_PLAYERS);
        return new RoundResetDef(lead, RoundResetDef.VOTE.equals(players), Json.names(reset, "teams"),
                MathHelper.clamp(Json.getInt(reset, "passPercent", 51), 1, 100),
                Math.max(5, Json.getInt(reset, "voteSeconds", 30)),
                Math.max(0, Json.getInt(reset, "cooldownSeconds", 60)),
                Json.getString(reset, "leadSays", "{player} reset the round"),
                Json.getString(reset, "voteSays", "{player} calls a vote to reset the round: /rdpl round vote yes or no, {seconds} seconds"),
                Json.getString(reset, "tallySays", "Reset the round? {yes} yes, {no} no, {seconds}"),
                Json.getString(reset, "passSays", "The vote passed, so the round is reset"),
                Json.getString(reset, "failSays", "The vote failed, so the round goes on"));
    }

    private static String opensBy(JsonObject json, ResourceLocation key) {
        return Json.choice(Json.getJsonObject(json, "opens", new JsonObject()), "by", "auto", "Score file " + key + " opens", OPENS);
    }

    private static int cardColor(JsonObject results, String name) {
        String asked = Json.getString(results, "background", "").trim();
        return asked.isEmpty() ? 0x1E2630 : ContentTypes.color(asked, name + " results background") & 0xFFFFFF;
    }

    @Nullable public static RaidDef raidFile(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "raid file");
        if (json == null) { return null; }
        String omen = Json.getString(json, "omen", "").trim();
        if (omen.isEmpty()) {
            ContentLog.LOGGER.error("Raid {} names no omen effect, and nothing else starts a raid, so it is left out", key);
            return null;
        }
        String colorName = Json.getString(json, "color", "red").trim().toUpperCase(Locale.ROOT);
        BossInfo.Color color = BossInfo.Color.RED;
        try { color = BossInfo.Color.valueOf(colorName); }
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
                if (!groups.isEmpty()) { waves.add(groups); }
            }
        }
        if (waves.isEmpty()) {
            ContentLog.LOGGER.error("Raid {} has no wave with anyone in it, so it is left out", key);
            return null;
        }
        return new RaidDef(key, omen,
                Json.getString(json, "name", "Raid"),
                color,
                waves,
                Math.max(1, Json.getInt(json, "waveDelay", 300)),
                Math.max(8, Json.getInt(json, "spawnDistance", 32)),
                Math.max(16, Json.getInt(json, "reach", 96)),
                Math.max(0, Json.getInt(json, "timeout", 48000)),
                Json.getString(json, "sound", "").trim(),
                Json.getString(json, "wins", "").trim(),
                Json.getString(json, "loses", "").trim(),
                bells(json),
                Json.getInt(json, "points", 0),
                Json.getString(json, "loot", "").trim(),
                Json.getBoolean(json, "anywhere", false));
    }

    private static List<String> bells(JsonObject json) {
        if (!json.has("bell")) { return Collections.emptyList(); }
        if (json.get("bell").isJsonArray()) { return strings(json, "bell"); }
        return Collections.singletonList(Json.getString(json, "bell", "").trim());
    }

    @Nullable public static TeamDef teamFile(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "team file");
        if (json == null) { return null; }
        String name = Json.getString(json, "name", key.getPath()).trim();
        if (name.isEmpty() || name.length() > 16) {
            ContentLog.LOGGER.error("Team file {} names the team '{}', and a team name is 1 to 16 characters, so the team is left out", key, name);
            return null;
        }
        TextFormatting color = TextFormatting.getValueByName(Settings.lower(Json.getString(json, "color", "white")));
        if (color == null || !color.isColor()) {
            ContentLog.LOGGER.error("Team {} asks for the color '{}', which is not one of the sixteen text colors, so it is white", name, Json.getString(json, "color", ""));
            color = TextFormatting.WHITE;
        }
        return new TeamDef(name,
                Json.getString(json, "displayName", name),
                color,
                Json.getString(json, "prefix", ""),
                Json.getString(json, "suffix", ""),
                Json.getBoolean(json, "friendlyFire", false),
                Json.getBoolean(json, "mobFriendlyFire", Json.getBoolean(json, "friendlyFire", false)),
                Json.getBoolean(json, "seeFriendlyInvisibles", true),
                visible(json, "nameTags", key),
                visible(json, "deathMessages", key),
                collision(json, key),
                Json.names(json, "entities"),
                Json.names(json, "players"),
                box(json, key),
                Json.getBoolean(json, "joinable", true),
                leadWay(json, name),
                Json.getString(json, "leadOn", "").trim(),
                Json.getString(json, "leadIs", "").trim(),
                Json.getString(json, "leadSays", "You are the current round leader"),
                Json.getString(json, "leadRuns", "").trim(),
                Json.getBoolean(json, "balance", false),
                Json.getBoolean(json, "scoreboard", true),
                Math.max(0, Json.getInt(json, "picks", 0)),
                Json.names(json, "picksFrom"),
                gives(key, json),
                standIn(json), standInAt(key, json), spawnAt(key, json));
    }

    @Nullable private static int[] lobbyAt(ResourceLocation key, JsonObject opens) {
        String asked = Json.getString(opens, "lobby", "").trim();
        int colon = asked.indexOf(':');
        int dimension = 0;
        if (colon > 0) {
            try { dimension = Integer.parseInt(asked.substring(0, colon).trim()); }
            catch (NumberFormatException notADimension) {
                ContentLog.LOGGER.error("Score file {} gives opens.lobby '{}', whose dimension is not a whole number, so the lobby has no area of its own", key, asked);
                return null;
            }
            asked = asked.substring(colon + 1);
        }
        int[] at = point(key, asked, "Score file {} gives opens.lobby '{}', which is not three whole numbers x,y,z, so the lobby has no area of its own");
        return at == null ? null : new int[] { dimension, at[0], at[1], at[2] };
    }

    @Nullable private static int[] spawnAt(ResourceLocation key, JsonObject json) { return point(key, Json.getString(json, "spawn", ""), "Team file {} gives spawn '{}', which is not three whole numbers x,y,z, so the side has no spawn of its own"); }

    @Nullable private static int[] point(ResourceLocation key, String asked, String refused) {
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
        if (!json.has("standIn") || !json.get("standIn").isJsonObject()) { return ""; }
        return Json.getString(Json.getJsonObject(json, "standIn"), "entity", "").trim();
    }

    @Nullable private static int[] standInAt(ResourceLocation key, JsonObject json) {
        if (!json.has("standIn") || !json.get("standIn").isJsonObject()) { return null; }
        String at = Json.getString(Json.getJsonObject(json, "standIn"), "at", "");
        int[] found = point(key, at, "Team file {} gives standIn an 'at' of '{}', which is not three whole numbers x,y,z, so no stand-in is kept");
        if (found == null && at.trim().isEmpty()) { ContentLog.LOGGER.error("Team file {} gives standIn no 'at', so no stand-in is kept", key); }
        return found;
    }

    private static List<ItemGiveDef> gives(ResourceLocation key, JsonObject json) {
        List<ItemGiveDef> values = new ArrayList<>();
        if (!json.has("gives")) { return values; }
        for (JsonElement held : Json.getJsonArray(json, "gives")) {
            if (held.isJsonPrimitive()) { values.add(new ItemGiveDef(held.getAsString().trim(), 1, false)); }
            else if (held.isJsonObject()) { values.add(new ItemGiveDef(Json.getString(held.getAsJsonObject(), "item", "").trim(), Math.max(1, Json.getInt(held.getAsJsonObject(), "count", 1)), Json.getBoolean(held.getAsJsonObject(), "unbreakable", false))); }
            else { ContentLog.LOGGER.error("Team file {} lists something under gives that is neither an item name nor an object, skipping it", key); }
        }
        return values;
    }

    private static String leadWay(JsonObject json, String team) {
        return Json.choice(json, "lead", TeamDef.LEAD_NONE, "Team " + team, LEADS);
    }

    private static Team.EnumVisible visible(JsonObject json, String field, ResourceLocation key) {
        String asked = Json.getString(json, field, "always").trim();
        for (Team.EnumVisible held : Team.EnumVisible.values()) {
            if (held.internalName.equalsIgnoreCase(asked)) { return held; }
        }
        ContentLog.LOGGER.error("Team file {} sets {} to '{}', which is not always, never, hideForOtherTeams or hideForOwnTeam, so it is always", key, field, asked);
        return Team.EnumVisible.ALWAYS;
    }

    private static Team.CollisionRule collision(JsonObject json, ResourceLocation key) {
        String asked = Json.getString(json, "collision", "always").trim();
        for (Team.CollisionRule held : Team.CollisionRule.values()) {
            if (held.name.equalsIgnoreCase(asked)) { return held; }
        }
        ContentLog.LOGGER.error("Team file {} sets collision to '{}', which is not always, never, pushOtherTeams or pushOwnTeam, so it is always", key, asked);
        return Team.CollisionRule.ALWAYS;
    }

    @Nullable private static int[] box(JsonObject json, ResourceLocation key) {
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

    @Nullable public static DiceDef diceFile(ResourceLocation key, String contents) {
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
