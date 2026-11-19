package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.resources.Identifier;

final class RecipeTypeFilter {
    private final Set<String> named = new LinkedHashSet<>();
    private final Set<String> prefixes = new LinkedHashSet<>();

    void read(Identifier key, JsonObject json) {
        for (String type : Json.strings(json, "types")) {
            int star = type.indexOf('*');
            if (star >= 0 && star != type.length() - 1) {
                ContentLog.LOGGER.error("The recipe type '{}' in {} has a * that is not at its end, skipping it", type, key);
                continue;
            }
            boolean prefix = star >= 0;
            String name = prefix ? type.substring(0, star) : type;
            if (Identifier.tryParse(name) == null) { ContentLog.LOGGER.error("The recipe type '{}' in {} is not a valid name, skipping it", type, key); }
            else if (prefix) { prefixes.add(name); }
            else { named.add(name); }
        }
    }

    boolean isEmpty() { return named.isEmpty() && prefixes.isEmpty(); }

    boolean matches(String type) {
        if (named.contains(type)) { return true; }
        for (String prefix : prefixes) {
            if (type.startsWith(prefix)) { return true; }
        }
        return false;
    }
}
