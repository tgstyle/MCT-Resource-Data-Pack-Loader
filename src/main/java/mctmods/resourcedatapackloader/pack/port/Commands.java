package mctmods.resourcedatapackloader.pack.port;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraft.world.level.GameRules;
import java.io.Serial;
import java.io.StringReader;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Commands {
    static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    static final String MINECRAFT = "minecraft:";
    static final String SELF = "@s";
    private static final String[] EFFECTS = {"", "speed", "slowness", "haste", "mining_fatigue", "strength", "instant_health", "instant_damage", "jump_boost", "nausea", "regeneration", "resistance", "fire_resistance", "water_breathing", "invisibility", "blindness", "night_vision", "hunger", "weakness", "poison", "wither", "health_boost", "absorption", "saturation", "glowing", "levitation", "luck", "unluck"};
    static final Map<String, String> GAME_MODES = Map.ofEntries(
            Map.entry("0", "survival"), Map.entry("s", "survival"), Map.entry("survival", "survival"), Map.entry("1", "creative"), Map.entry("c", "creative"), Map.entry("creative", "creative"),
            Map.entry("2", "adventure"), Map.entry("a", "adventure"), Map.entry("adventure", "adventure"), Map.entry("3", "spectator"), Map.entry("sp", "spectator"), Map.entry("spectator", "spectator"));
    private static final Map<String, String> DIFFICULTIES = Map.ofEntries(
            Map.entry("0", "peaceful"), Map.entry("p", "peaceful"), Map.entry("peaceful", "peaceful"), Map.entry("1", "easy"), Map.entry("e", "easy"), Map.entry("easy", "easy"),
            Map.entry("2", "normal"), Map.entry("n", "normal"), Map.entry("normal", "normal"), Map.entry("3", "hard"), Map.entry("h", "hard"), Map.entry("hard", "hard"));
    private static final Map<String, String> PARTICLES = Map.ofEntries(
            Map.entry("explode", "poof"), Map.entry("largeexplode", "explosion"), Map.entry("hugeexplosion", "explosion_emitter"), Map.entry("fireworksSpark", "firework"),
            Map.entry("bubble", "bubble"), Map.entry("splash", "splash"), Map.entry("wake", "fishing"), Map.entry("suspended", "underwater"), Map.entry("depthsuspend", "underwater"),
            Map.entry("crit", "crit"), Map.entry("magicCrit", "enchanted_hit"), Map.entry("smoke", "smoke"), Map.entry("largesmoke", "large_smoke"), Map.entry("spell", "effect"),
            Map.entry("instantSpell", "instant_effect"), Map.entry("witchMagic", "witch"), Map.entry("dripWater", "dripping_water"), Map.entry("dripLava", "dripping_lava"),
            Map.entry("angryVillager", "angry_villager"), Map.entry("happyVillager", "happy_villager"), Map.entry("townaura", "mycelium"), Map.entry("note", "note"),
            Map.entry("portal", "portal"), Map.entry("enchantmenttable", "enchant"), Map.entry("flame", "flame"), Map.entry("lava", "lava"), Map.entry("cloud", "cloud"),
            Map.entry("snowballpoof", "item_snowball"), Map.entry("snowshovel", "item_snowball"), Map.entry("slime", "item_slime"), Map.entry("heart", "heart"), Map.entry("droplet", "rain"),
            Map.entry("mobappearance", "elder_guardian"), Map.entry("dragonbreath", "dragon_breath"), Map.entry("endRod", "end_rod"), Map.entry("damageIndicator", "damage_indicator"),
            Map.entry("sweepAttack", "sweep_attack"), Map.entry("totem", "totem_of_undying"), Map.entry("spit", "spit"));
    private static final String[][] SOUND_PREFIXES = {
            {"block.cloth.", "block.wool."}, {"block.enderchest.", "block.ender_chest."}, {"block.metal_pressureplate.", "block.metal_pressure_plate."},
            {"block.note.", "block.note_block."}, {"block.slime.", "block.slime_block."}, {"block.stone_pressureplate.", "block.stone_pressure_plate."},
            {"block.waterlily.", "block.lily_pad."}, {"block.wood_button.", "block.wooden_button."}, {"block.wood_pressureplate.", "block.wooden_pressure_plate."},
            {"entity.armorstand.", "entity.armor_stand."}, {"entity.bobber.", "entity.fishing_bobber."}, {"entity.enderdragon_fireball.", "entity.dragon_fireball."},
            {"entity.enderdragon.", "entity.ender_dragon."}, {"entity.endereye.", "entity.ender_eye."}, {"entity.endermen.", "entity.enderman."},
            {"entity.enderpearl.", "entity.ender_pearl."}, {"entity.evocation_fangs.", "entity.evoker_fangs."}, {"entity.evocation_illager.", "entity.evoker."},
            {"entity.firework.", "entity.firework_rocket."}, {"entity.illusion_illager.", "entity.illusioner."}, {"entity.irongolem.", "entity.iron_golem."},
            {"entity.itemframe.", "entity.item_frame."}, {"entity.leashknot.", "entity.leash_knot."}, {"entity.lightning.", "entity.lightning_bolt."},
            {"entity.lingeringpotion.", "entity.lingering_potion."}, {"entity.magmacube.", "entity.magma_cube."}, {"entity.snowman.", "entity.snow_golem."},
            {"entity.vindication_illager.", "entity.vindicator."}, {"entity.zombie_pig.", "entity.zombified_piglin."}, {"record.", "music_disc."},
            {"entity.parrot.imitate.enderdragon", "entity.parrot.imitate.ender_dragon"}, {"entity.parrot.imitate.evocation_illager", "entity.parrot.imitate.evoker"},
            {"entity.parrot.imitate.illusion_illager", "entity.parrot.imitate.illusioner"}, {"entity.parrot.imitate.magmacube", "entity.parrot.imitate.magma_cube"},
            {"entity.parrot.imitate.vindication_illager", "entity.parrot.imitate.vindicator"}};
    private static final String[][] SMALL_SOUNDS = {{"entity.small_magmacube.", "entity.magma_cube."}, {"entity.small_slime.", "entity.slime."}};
    private static final Map<String, String> SOUNDS = Map.of(
            "entity.polar_bear.baby_ambient", "entity.polar_bear.ambient_baby", "entity.villager.trading", "entity.villager.trade",
            "entity.zombie.attack_door_wood", "entity.zombie.attack_wooden_door", "entity.zombie.break_door_wood", "entity.zombie.break_wooden_door", "music.nether", "music.nether.nether_wastes");
    private static final Map<String, String> STRUCTURES = Map.of("Village", "#minecraft:village", "Mansion", "minecraft:mansion", "Monument", "minecraft:monument", "Stronghold", "minecraft:stronghold", "Mineshaft", "#minecraft:mineshaft", "EndCity", "minecraft:end_city", "Fortress", "minecraft:fortress");
    private static final Set<String> FILL_MODES = Set.of("destroy", "hollow", "keep", "outline");

    private Commands() {}

    static final class Kept extends RuntimeException {
        @Serial private static final long serialVersionUID = 1L;

        Kept(String why) { super(why, null, false, false); }
    }

    public static String function(String contents, String path, Ported pack) {
        String[] lines = contents.split("\r?\n", -1);
        StringBuilder out = new StringBuilder();
        int changed = 0;
        int kept = 0;
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) { out.append('\n'); }
            String line = lines[i].trim();
            if (line.isEmpty() || line.startsWith("#")) {
                out.append(lines[i]);
                continue;
            }
            try {
                String converted = command(line, pack);
                out.append(converted);
                if (!converted.equals(line)) {
                    changed++;
                    pack.rewrote();
                }
            }
            catch (Kept why) {
                kept++;
                out.append(line);
                pack.note("'" + path + "' line " + (i + 1) + " is kept as written, since " + why.getMessage() + ": " + line);
            }
        }
        if (pack.overworldShift() != 0 && changed > 0) { pack.note("The pack's overworld is a 1.12.2 flat world, whose floor sat at y 0, so every absolute y in its functions moves by " + pack.overworldShift() + " to the flat floor this version lays; a function that runs in another dimension needs its heights checked by hand"); }
        if (changed > 0 || kept > 0) { pack.note("'" + path + "' is a 1.12.2 function: " + changed + " command(s) rewritten for this version, " + kept + " kept as written"); }
        return out.toString();
    }

    private static String command(String line, Ported pack) {
        String[] args = line.split(" ");
        return switch (args[0]) {
            case "give" -> give(args, pack);
            case "clear" -> clear(args, pack);
            case "setblock" -> setblock(args, pack);
            case "fill" -> fill(args, pack);
            case "clone" -> clone(args, pack);
            case "testforblock" -> testforblock(args, pack);
            case "testforblocks" -> testforblocks(args, pack);
            case "testfor" -> testfor(args, pack);
            case "summon" -> summon(args, pack);
            case "tp" -> tp(args, pack);
            case "teleport" -> teleport(args, pack);
            case "effect" -> effect(args, pack);
            case "enchant" -> enchant(args, pack);
            case "playsound" -> playsound(args, pack);
            case "stopsound" -> stopsound(args, pack);
            case "particle" -> particle(args, pack);
            case "scoreboard" -> CommandScoreboard.scoreboard(args, pack);
            case "execute" -> execute(args, pack);
            case "tellraw" -> "tellraw " + CommandSelectors.target(at(args, 1), pack) + " " + text(from(args, 2), pack);
            case "title" -> title(args, pack);
            case "gamerule" -> gamerule(args, line);
            case "weather" -> "weather " + at(args, 1).toLowerCase(Locale.ROOT) + (args.length > 2 ? " " + number(args[2]) + "s" : "");
            case "function" -> functionCall(args, pack);
            case "advancement" -> advancement(args, pack);
            case "spreadplayers" -> spreadplayers(args, pack);
            case "difficulty" -> "difficulty " + named(DIFFICULTIES, at(args, 1), "difficulty");
            case "gamemode" -> "gamemode " + named(GAME_MODES, at(args, 1), "game mode") + (args.length > 2 ? " " + CommandSelectors.target(args[2], pack) : "");
            case "defaultgamemode" -> "defaultgamemode " + named(GAME_MODES, at(args, 1), "game mode");
            case "xp" -> xp(args, pack);
            case "blockdata" -> "data merge block " + position(args, 1, pack) + " " + CommandStacks.nbt(from(args, 4));
            case "setworldspawn" -> args.length > 3 ? "setworldspawn " + position(args, 1, pack) + (args.length > 4 ? " " + rest(args, 4) : "") : passed(args, pack);
            case "spawnpoint" -> args.length > 4 ? "spawnpoint " + CommandSelectors.target(args[1], pack) + " " + position(args, 2, pack) + (args.length > 5 ? " " + rest(args, 5) : "") : passed(args, pack);
            case "entitydata" -> "data merge entity " + CommandSelectors.target(at(args, 1), pack) + " " + CommandStacks.nbt(from(args, 2));
            case "replaceitem" -> replaceitem(args, pack);
            case "locate" -> locate(args);
            case "toggledownfall" -> throw new Kept("toggledownfall is gone and weather names the weather to set");
            case "stats" -> throw new Kept("stats is gone and execute store keeps a command's result now");
            default -> passed(args, pack);
        };
    }

    static void need(String[] args, int count) {
        if (args.length < count) { throw new Kept("it has fewer arguments than " + args[0] + " needs"); }
    }

    static String at(String[] args, int index) {
        need(args, index + 1);
        return args[index];
    }

    private static String from(String[] args, int index) {
        need(args, index + 1);
        return rest(args, index);
    }

    @Nullable static String arg(String[] args, int index) { return args.length > index ? args[index] : null; }

    static String rest(String[] args, int from) { return join(args, from, args.length); }

    private static String join(String[] args, int from, int to) { return String.join(" ", Arrays.copyOfRange(args, from, to)); }

    static int number(String text) {
        try { return Integer.parseInt(text); }
        catch (NumberFormatException failed) { throw new Kept("'" + text + "' is not a number"); }
    }

    static String named(Map<String, String> names, String given, String what) {
        String found = names.get(given.toLowerCase(Locale.ROOT));
        if (found == null) { throw new Kept("'" + given + "' is not a " + what); }
        return found;
    }

    static String namespaced(String name) {
        String lowered = name.trim().toLowerCase(Locale.ROOT);
        return lowered.indexOf(':') < 0 ? MINECRAFT + lowered : lowered;
    }

    static String height(String token, Ported pack) {
        if (token.startsWith("~") || token.startsWith("^")) { return token; }
        return Convert.shiftY(token, pack.overworldShift());
    }

    private static String position(String[] args, int from, Ported pack) {
        need(args, from + 3);
        return args[from] + " " + height(args[from + 1], pack) + " " + args[from + 2];
    }

    private static String passed(String[] args, Ported pack) {
        String[] out = args.clone();
        for (int i = 1; i < out.length; i++) { out[i] = CommandSelectors.target(out[i], pack); }
        return String.join(" ", out);
    }

    static String entity(String named, Ported pack) {
        String name = namespaced(named);
        return pack.owns(name) ? name : Ids.entity(name);
    }

    private static String give(String[] args, Ported pack) {
        need(args, 3);
        int meta = args.length > 4 ? number(args[4]) : 0;
        return "give " + CommandSelectors.target(args[1], pack) + " " + CommandStacks.item(args[2], meta, args.length > 5 ? rest(args, 5) : null, pack) + (args.length > 3 ? " " + number(args[3]) : "");
    }

    private static String clear(String[] args, Ported pack) {
        if (args.length < 2) { return "clear"; }
        StringBuilder out = new StringBuilder("clear ").append(CommandSelectors.target(args[1], pack));
        if (args.length < 3) { return out.toString(); }
        int meta = args.length > 3 ? number(args[3]) : -1;
        out.append(' ').append(CommandStacks.itemTest(args[2], meta, args.length > 5 ? rest(args, 5) : null, pack));
        int most = args.length > 4 ? number(args[4]) : -1;
        if (most >= 0) { out.append(' ').append(most); }
        return out.toString();
    }

    private static String setblock(String[] args, Ported pack) {
        need(args, 5);
        String mode = arg(args, 6);
        return "setblock " + position(args, 1, pack) + " " + CommandStacks.block(args[4], arg(args, 5), args.length > 7 ? rest(args, 7) : null, pack) + (mode == null ? "" : " " + ("destroy".equals(mode) || "keep".equals(mode) ? mode : "replace"));
    }

    private static String fill(String[] args, Ported pack) {
        need(args, 8);
        String data = arg(args, 8);
        String mode = arg(args, 9);
        String out = "fill " + position(args, 1, pack) + " " + position(args, 4, pack) + " ";
        if ("replace".equals(mode) && args.length > 10 && !args[10].startsWith("{")) { return out + CommandStacks.block(args[7], data, null, pack) + " replace " + CommandStacks.blockTest(args[10], arg(args, 11), null, pack); }
        String placed = CommandStacks.block(args[7], data, args.length > 10 ? rest(args, 10) : null, pack);
        if (mode == null) { return out + placed; }
        return out + placed + " " + (FILL_MODES.contains(mode) ? mode : "replace");
    }

    private static String clone(String[] args, Ported pack) {
        need(args, 10);
        String out = "clone " + position(args, 1, pack) + " " + position(args, 4, pack) + " " + position(args, 7, pack);
        if (args.length == 10) { return out; }
        String mode = args.length > 11 ? " " + args[11] : "";
        if ("filtered".equals(args[10])) { return out + " filtered " + CommandStacks.blockTest(at(args, 12), arg(args, 13), null, pack) + mode; }
        return out + " " + args[10] + mode;
    }

    private static String testforblock(String[] args, Ported pack) {
        need(args, 5);
        return "execute if block " + position(args, 1, pack) + " " + CommandStacks.blockTest(args[4], arg(args, 5), args.length > 6 ? rest(args, 6) : null, pack);
    }

    private static String testforblocks(String[] args, Ported pack) {
        need(args, 10);
        return "execute if blocks " + position(args, 1, pack) + " " + position(args, 4, pack) + " " + position(args, 7, pack) + " " + ("masked".equals(arg(args, 10)) ? "masked" : "all");
    }

    private static String teleport(String[] args, Ported pack) {
        need(args, 5);
        return "teleport " + CommandSelectors.target(args[1], pack) + " " + position(args, 2, pack) + (args.length > 5 ? " " + rest(args, 5) : "");
    }

    private static String testfor(String[] args, Ported pack) {
        need(args, 2);
        String target = CommandSelectors.target(args[1], pack);
        return "execute if entity " + (args.length > 2 ? CommandSelectors.narrowed(target, "nbt=" + CommandStacks.nbt(rest(args, 2))) : target);
    }

    private static String summon(String[] args, Ported pack) {
        need(args, 2);
        String legacy = namespaced(args[1]);
        StringBuilder out = new StringBuilder("summon ").append(entity(legacy, pack));
        if (args.length > 4) { out.append(' ').append(position(args, 2, pack)); }
        if (args.length > 5) {
            String nbt = rest(args, 5);
            String fixed = Ids.entityData(legacy, nbt);
            if (fixed == null) { throw new Kept("the entity nbt " + nbt + " could not be carried through the data fixers"); }
            out.append(' ').append(fixed);
        }
        return out.toString();
    }

    private static String tp(String[] args, Ported pack) {
        return switch (args.length - 1) {
            case 1 -> "tp " + CommandSelectors.target(args[1], pack);
            case 2 -> "tp " + CommandSelectors.target(args[1], pack) + " " + CommandSelectors.target(args[2], pack);
            case 3 -> "tp " + position(args, 1, pack);
            case 4, 6 -> moved(CommandSelectors.target(args[1], pack), position(args, 2, pack) + (args.length > 5 ? " " + rest(args, 5) : ""));
            case 5 -> "tp " + SELF + " " + position(args, 1, pack) + " " + rest(args, 4);
            default -> throw new Kept("tp takes a target and a place, or a place with a turn");
        };
    }

    private static String moved(String target, String place) {
        if (place.indexOf('~') < 0 || SELF.equals(target)) { return "tp " + target + " " + place; }
        return "execute as " + target + " at " + SELF + " run tp " + SELF + " " + place;
    }

    private static String effect(String[] args, Ported pack) {
        need(args, 3);
        String target = CommandSelectors.target(args[1], pack);
        if ("clear".equals(args[2])) { return "effect clear " + target; }
        String effect = effectName(args[2]);
        if (args.length > 3 && number(args[3]) == 0) { return "effect clear " + target + " " + effect; }
        StringBuilder out = new StringBuilder("effect give ").append(target).append(' ').append(effect);
        if (args.length > 3) { out.append(' ').append(number(args[3])); }
        if (args.length > 4) { out.append(' ').append(number(args[4])); }
        if (args.length > 5) { out.append(' ').append("true".equalsIgnoreCase(args[5])); }
        return out.toString();
    }

    private static String effectName(String given) {
        try {
            int id = Integer.parseInt(given);
            if (id < 1 || id >= EFFECTS.length) { throw new Kept("the effect number " + id + " was not a 1.12.2 effect"); }
            return MINECRAFT + EFFECTS[id];
        }
        catch (NumberFormatException named) { return namespaced(given); }
    }

    private static String enchant(String[] args, Ported pack) {
        need(args, 3);
        String enchantment = Ids.enchantment(args[2]);
        if (enchantment == null) { throw new Kept("the enchantment " + args[2] + " could not be carried through the data fixers"); }
        return "enchant " + CommandSelectors.target(args[1], pack) + " " + enchantment + (args.length > 3 ? " " + number(args[3]) : "");
    }

    static String sound(String given) {
        String name = given.trim();
        String namespace = "";
        if (name.startsWith(MINECRAFT)) {
            namespace = MINECRAFT;
            name = name.substring(MINECRAFT.length());
        }
        else if (name.indexOf(':') >= 0) { return given; }
        String exact = SOUNDS.get(name);
        if (exact != null) { return namespace + exact; }
        for (String[] small : SMALL_SOUNDS) {
            if (name.startsWith(small[0])) { return namespace + small[1] + name.substring(small[0].length()) + "_small"; }
        }
        for (String[] prefix : SOUND_PREFIXES) {
            if (name.startsWith(prefix[0])) { return namespace + prefix[1] + name.substring(prefix[0].length()); }
        }
        return given;
    }

    private static String playsound(String[] args, Ported pack) {
        need(args, 4);
        if (args.length == 5 || args.length == 6) { throw new Kept("it gives only part of a position"); }
        return "playsound " + sound(args[1]) + " " + args[2] + " " + CommandSelectors.target(args[3], pack) + (args.length > 4 ? " " + position(args, 4, pack) : "") + (args.length > 7 ? " " + rest(args, 7) : "");
    }

    private static String stopsound(String[] args, Ported pack) {
        need(args, 2);
        return "stopsound " + CommandSelectors.target(args[1], pack) + (args.length > 2 ? " " + args[2] : "") + (args.length > 3 ? " " + sound(args[3]) : "");
    }

    private static String particle(String[] args, Ported pack) {
        need(args, 9);
        int[] extra = new int[Math.max(0, args.length - 12)];
        for (int i = 0; i < extra.length; i++) { extra[i] = number(args[12 + i]); }
        StringBuilder out = new StringBuilder("particle ").append(particleName(args[1], extra)).append(' ').append(position(args, 2, pack)).append(' ').append(join(args, 5, 9)).append(' ').append(args.length > 9 ? number(args[9]) : 0);
        if (args.length > 10) { out.append(' ').append("force".equals(args[10]) ? "force" : "normal"); }
        if (args.length > 11) { out.append(' ').append(CommandSelectors.target(args[11], pack)); }
        return out.toString();
    }

    static String particleId(String given) {
        String simple = PARTICLES.get(given.trim());
        return simple == null ? given : MINECRAFT + simple;
    }

    private static String particleName(String given, int[] extra) {
        String simple = PARTICLES.get(given);
        if (simple != null) { return MINECRAFT + simple; }
        return switch (given) {
            case "mobSpell" -> "minecraft:entity_effect";
            case "mobSpellAmbient" -> "minecraft:ambient_entity_effect";
            case "reddust" -> "minecraft:dust 1.0 0.0 0.0 1.0";
            case "barrier" -> "minecraft:block_marker minecraft:barrier";
            case "iconcrack" -> {
                String item = Ids.item(Ids.numberedItem(extra.length > 0 ? extra[0] : 0), extra.length > 1 ? extra[1] : 0);
                if ("minecraft:air".equals(item)) { throw new Kept("iconcrack names no item"); }
                yield "minecraft:item " + item;
            }
            case "blockcrack", "blockdust", "fallingdust" -> {
                int id = extra.length > 0 ? extra[0] : 0;
                Ids.Block block = Ids.numberedBlock(id & 4095, id >> 12);
                yield ("fallingdust".equals(given) ? "minecraft:falling_dust " : "minecraft:block ") + block.name() + CommandStacks.state(block.properties());
            }
            default -> throw new Kept("the particle " + given + " has no twin on this version");
        };
    }

    private static String title(String[] args, Ported pack) {
        need(args, 3);
        String target = CommandSelectors.target(args[1], pack);
        return switch (args[2]) {
            case "title", "subtitle", "actionbar" -> "title " + target + " " + args[2] + " " + text(from(args, 3), pack);
            default -> "title " + target + " " + rest(args, 2);
        };
    }

    private static String text(String json, Ported pack) {
        JsonElement parsed;
        try {
            JsonReader reader = new JsonReader(new StringReader(json));
            reader.setLenient(true);
            parsed = JsonParser.parseReader(reader);
        }
        catch (JsonParseException failed) { throw new Kept("its text " + json + " is not JSON"); }
        textWalk(parsed, pack);
        return GSON.toJson(parsed);
    }

    private static void textWalk(JsonElement element, Ported pack) {
        if (element.isJsonArray()) {
            for (JsonElement inner : element.getAsJsonArray()) { textWalk(inner, pack); }
            return;
        }
        if (!element.isJsonObject()) { return; }
        JsonObject held = element.getAsJsonObject();
        if (held.has("selector") && held.get("selector").isJsonPrimitive()) { held.addProperty("selector", CommandSelectors.target(held.get("selector").getAsString(), pack)); }
        if (held.has("score") && held.get("score").isJsonObject() && held.getAsJsonObject("score").has("name") && held.getAsJsonObject("score").get("name").isJsonPrimitive()) {
            JsonObject score = held.getAsJsonObject("score");
            score.addProperty("name", CommandSelectors.target(score.get("name").getAsString(), pack));
        }
        if (held.has("clickEvent") && held.get("clickEvent").isJsonObject()) {
            JsonObject click = held.getAsJsonObject("clickEvent");
            String action = click.has("action") && click.get("action").isJsonPrimitive() ? click.get("action").getAsString() : "";
            String value = click.has("value") && click.get("value").isJsonPrimitive() ? click.get("value").getAsString() : "";
            if (("run_command".equals(action) || "suggest_command".equals(action)) && value.startsWith("/") && value.length() > 1) {
                String converted = command(value.substring(1), pack);
                if (converted.indexOf('\n') < 0) { click.addProperty("value", "/" + converted); }
            }
        }
        for (Map.Entry<String, JsonElement> entry : held.entrySet()) {
            if (!"clickEvent".equals(entry.getKey())) { textWalk(entry.getValue(), pack); }
        }
    }

    private static String gamerule(String[] args, String line) {
        need(args, 2);
        Set<String> rules = new LinkedHashSet<>();
        GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
            @Override public <T extends GameRules.Value<T>> void visit(@Nonnull GameRules.Key<T> key, @Nonnull GameRules.Type<T> type) { rules.add(key.getId()); }
        });
        if (Convert.GAME_LOOP.equals(args[1])) { throw new Kept("the gameLoopFunction rule is gone and the #minecraft:tick function tag runs a function every tick"); }
        if (!rules.contains(args[1])) { throw new Kept("this version has no game rule named " + args[1]); }
        return line;
    }

    private static String functionCall(String[] args, Ported pack) {
        need(args, 2);
        String named = namespaced(args[1]);
        if (args.length == 2) { return "function " + named; }
        if (args.length != 4 || !("if".equals(args[2]) || "unless".equals(args[2]))) { throw new Kept("function takes a name, and if or unless with a selector"); }
        return "execute " + args[2] + " entity " + CommandSelectors.target(args[3], pack) + " run function " + named;
    }

    private static String advancement(String[] args, Ported pack) {
        need(args, 4);
        if (!"test".equals(args[1])) { return "advancement " + args[1] + " " + CommandSelectors.target(args[2], pack) + " " + rest(args, 3); }
        String advancement = namespaced(args[3]);
        return "execute if entity " + CommandSelectors.narrowed(CommandSelectors.target(args[2], pack), "advancements={" + advancement + "=" + (args.length > 4 ? "{" + args[4] + "=true}" : "true") + "}");
    }

    private static String spreadplayers(String[] args, Ported pack) {
        need(args, 7);
        if (args.length > 7) { throw new Kept("spreadplayers takes one target on this version"); }
        return "spreadplayers " + join(args, 1, 6) + " " + CommandSelectors.target(args[6], pack);
    }

    private static String xp(String[] args, Ported pack) {
        need(args, 2);
        String amount = args[1];
        boolean levels = amount.length() > 1 && (amount.endsWith("l") || amount.endsWith("L"));
        if (levels) { amount = amount.substring(0, amount.length() - 1); }
        if (amount.startsWith("-")) { throw new Kept("xp with a negative amount took nothing away on 1.12.2"); }
        return "xp add " + (args.length > 2 ? CommandSelectors.target(args[2], pack) : SELF) + " " + number(amount) + (levels ? " levels" : "");
    }

    private static String replaceitem(String[] args, Ported pack) {
        need(args, 2);
        boolean block = "block".equals(args[1]);
        int slot = block ? 5 : 3;
        need(args, slot + 2);
        String holder = block ? "block " + position(args, 2, pack) : "entity " + CommandSelectors.target(args[2], pack);
        String named = args[slot].startsWith("slot.") ? args[slot].substring("slot.".length()) : args[slot];
        int meta = args.length > slot + 3 ? number(args[slot + 3]) : 0;
        String item = CommandStacks.item(args[slot + 1], meta, args.length > slot + 4 ? rest(args, slot + 4) : null, pack);
        return "item replace " + holder + " " + named + " with " + item + (args.length > slot + 2 ? " " + number(args[slot + 2]) : "");
    }

    private static String locate(String[] args) {
        need(args, 2);
        String structure = STRUCTURES.get(args[1]);
        if (structure == null) { throw new Kept("the structure " + args[1] + " has no single twin on this version"); }
        return "locate structure " + structure;
    }

    private static String execute(String[] args, Ported pack) {
        need(args, 6);
        String who = CommandSelectors.target(args[1], pack);
        String place = position(args, 2, pack);
        StringBuilder out = new StringBuilder("execute");
        if (!SELF.equals(who)) { out.append(" as ").append(who); }
        out.append(" at ").append(SELF);
        if (!"~ ~ ~".equals(place)) { out.append(" positioned ").append(place); }
        int from = 5;
        if ("detect".equals(args[5]) && args.length > 11) {
            out.append(" if block ").append(position(args, 6, pack)).append(' ').append(CommandStacks.blockTest(args[9], args[10], null, pack));
            from = 11;
        }
        String inner = rest(args, from);
        String converted = command(inner.startsWith("/") ? inner.substring(1) : inner, pack);
        if (converted.indexOf('\n') >= 0) { throw new Kept("the command it runs becomes several commands on this version"); }
        if (converted.startsWith("execute ")) { return out + converted.substring("execute".length()); }
        return out + " run " + converted;
    }
}
