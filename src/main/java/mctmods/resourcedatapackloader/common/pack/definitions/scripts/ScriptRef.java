package mctmods.resourcedatapackloader.common.pack.definitions.scripts;

import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public final class ScriptRef {
    public final String path;
    public final String inline;
    public final String namespace;
    private final String key;

    private ScriptRef(ResourceLocation owner, String path, String inline) {
        this.path = path;
        this.inline = inline;
        this.namespace = owner.getNamespace();
        this.key = path != null ? namespace + ":" + path : owner + "#" + Integer.toHexString(inline.hashCode());
    }

    public static ScriptRef read(ResourceLocation owner, JsonObject json) {
        boolean hasFile = json.has("scriptFile");
        boolean hasInline = json.has("script");
        if (!hasFile && !hasInline) { return null; }
        if (hasFile && hasInline) {
            ContentLog.LOGGER.error("{}: use either scriptFile or script, not both", owner);
            return null;
        }
        if (hasFile) {
            String path = Json.text(json, "scriptFile");
            if (!path.startsWith("scripts/") || !path.endsWith(".js") || path.contains("..")) {
                ContentLog.LOGGER.error("{}: scriptFile '{}' must be under scripts/, end in .js and hold no '..'", owner, path);
                return null;
            }
            return new ScriptRef(owner, path, null);
        }
        String inline = Json.text(json, "script");
        if (inline.isEmpty()) {
            ContentLog.LOGGER.error("{}: script is empty", owner);
            return null;
        }
        return new ScriptRef(owner, null, inline);
    }

    public String key() { return key; }
}
