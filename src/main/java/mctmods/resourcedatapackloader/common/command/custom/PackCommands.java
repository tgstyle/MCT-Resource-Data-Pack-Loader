package mctmods.resourcedatapackloader.common.command.custom;

import mctmods.resourcedatapackloader.common.pack.definitions.scripts.ScriptRef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.util.entity.Functions;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.command.CommandHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public final class PackCommands {
    private static final Pattern NAME = Pattern.compile("[a-z0-9_]+");
    private static final List<String> COMMAND_KEYS = Collections.unmodifiableList(Arrays.asList("requires", "runs", "script", "scriptFile", "name", "permission", "args"));
    private static final List<String> ARG_KEYS = Collections.unmodifiableList(Arrays.asList("name", "optional", "type", "default"));
    private static final List<String> TYPES = Arrays.asList(CommandArg.PLAYER, CommandArg.INT, CommandArg.NUMBER, CommandArg.STRING, CommandArg.TEXT);

    private PackCommands() {}

    public static void register(FMLServerStartingEvent event) {
        CommandHandler handler = (CommandHandler) event.getServer().getCommandManager();
        Json.eachObject(PackManager.COMMANDS, "command file", (key, json) -> read(event, handler, key, json));
    }

    private static void read(FMLServerStartingEvent event, CommandHandler handler, ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, "the top level", COMMAND_KEYS);
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        String runs = Json.getString(json, "runs", "").trim();
        boolean inline = !Json.getString(json, "script", "").trim().isEmpty();
        boolean file = !Json.getString(json, "scriptFile", "").trim().isEmpty();
        boolean hasScript = inline || file;
        int bodies = (runs.isEmpty() ? 0 : 1) + (inline ? 1 : 0) + (file ? 1 : 0);
        if (bodies == 0) {
            ContentLog.LOGGER.error("Command file {} names no function to run, ignoring it", key);
            return;
        }
        if (bodies > 1) {
            ContentLog.LOGGER.error("Command file {} has more than one of runs, script and scriptFile, ignoring it", key);
            return;
        }
        ScriptRef script = hasScript ? ScriptRef.read(key, json) : null;
        if (hasScript && script == null) { return; }
        String name = Json.getString(json, "name", key.getPath()).trim().toLowerCase(Locale.ROOT);
        if (!NAME.matcher(name).matches()) {
            ContentLog.LOGGER.error("Command file {} has the name '{}', which is not lowercase letters, digits and underscores, ignoring it", key, name);
            return;
        }
        if (handler.getCommands().containsKey(name)) {
            ContentLog.LOGGER.error("Command file {} names the command /{}, which another command already uses, ignoring it", key, name);
            return;
        }
        List<CommandArg> args = arguments(key, json, hasScript);
        if (!hasScript) { warnPlaceholders(key, runs, args); }
        event.registerServerCommand(new PackCommand(name, permission(key, json), args, hasScript ? null : runs, script));
    }

    private static int permission(ResourceLocation key, JsonObject json) {
        int level = Json.getInt(json, "permission", 2);
        if (level >= 0 && level <= 4) { return level; }
        ContentLog.LOGGER.error("Command file {} has permission {}, which is not 0 to 4, using 2", key, level);
        return 2;
    }

    private static List<CommandArg> arguments(ResourceLocation key, JsonObject json, boolean script) {
        List<CommandArg> args = new ArrayList<>();
        Set<String> names = new HashSet<>();
        JsonArray array = Json.getJsonArray(json, "args", new JsonArray());
        boolean optionalSeen = false;
        for (int i = 0; i < array.size(); i++) {
            JsonElement element = array.get(i);
            JsonObject object = element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
            Json.unknownKeys(object, "args", ARG_KEYS);
            String name = Json.getString(object, "name", "").trim();
            if (!NAME.matcher(name).matches()) {
                ContentLog.LOGGER.error("Command file {} has an argument with no usable name, ignoring that argument", key);
                continue;
            }
            if (script && ("rdpl".equals(name) || "sender".equals(name))) {
                ContentLog.LOGGER.error("Command file {} has an argument named {}, which a script command uses itself, ignoring that argument", key, name);
                continue;
            }
            if (!names.add(name)) {
                ContentLog.LOGGER.error("Command file {} has two arguments named {}, ignoring the second", key, name);
                continue;
            }
            boolean optional = Json.getBoolean(object, "optional", false);
            if (!optional && optionalSeen) {
                ContentLog.LOGGER.error("Command file {} has the required argument {} after an optional one, making it optional", key, name);
                optional = true;
            }
            optionalSeen |= optional;
            args.add(new CommandArg(name, type(key, object, i == array.size() - 1, name), optional, Json.getString(object, "default", "")));
        }
        return args;
    }

    private static String type(ResourceLocation key, JsonObject object, boolean last, String name) {
        String type = Json.getString(object, "type", CommandArg.STRING).trim().toLowerCase(Locale.ROOT);
        if (!TYPES.contains(type)) {
            ContentLog.LOGGER.error("Command file {} has the argument type '{}', which is not one of player, int, number, string and text, using string", key, type);
            return CommandArg.STRING;
        }
        if (CommandArg.TEXT.equals(type) && !last) {
            ContentLog.LOGGER.error("Command file {} has the text argument {} before the last one, using string", key, name);
            return CommandArg.STRING;
        }
        return type;
    }

    private static void warnPlaceholders(ResourceLocation key, String runs, List<CommandArg> args) {
        ResourceLocation function = new ResourceLocation(runs);
        String text = PackManager.get().read(function.getNamespace(), function.getPath(), PackManager.FUNCTIONS, PackManager.MCFUNCTION);
        if (text == null) { return; }
        Set<String> known = new HashSet<>();
        for (CommandArg arg : args) { known.add(arg.name); }
        Set<String> warned = new HashSet<>();
        Matcher matcher = Functions.PLACEHOLDER.matcher(text);
        while (matcher.find()) {
            String used = matcher.group(1);
            if (!known.contains(used) && warned.add(used)) {
                ContentLog.LOGGER.error("Command file {} runs the function {}, which uses $({}) but the command has no argument by that name, so it is left as written", key, runs, used);
            }
        }
    }
}
