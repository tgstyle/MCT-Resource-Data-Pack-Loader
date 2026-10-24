package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public final class HeartsDef {
    public static final float VANILLA = 10.0F;
    public static final List<String> STYLES = Arrays.asList("bar", "chat");
    private static final Pattern COLOR = Pattern.compile("#[0-9A-Fa-f]{6}");
    public final ResourceLocation file;
    @Nullable public final Float startHearts;
    @Nullable public final Float cap;
    @Nullable public final Boolean keepOnDeath;
    @Nullable public final String style;
    @Nullable public final String sound;
    @Nullable public final String particle;
    @Nullable public final int[] colors;
    public final List<HeartReward> rewards;

    private HeartsDef(ResourceLocation file, JsonObject json) {
        this.file = file;
        this.startHearts = hearts(json, "startHearts");
        this.cap = hearts(json, "cap");
        this.keepOnDeath = json.has("keepOnDeath") ? flag(json) : null;
        this.style = json.has("style") ? Json.choice(json, "style", null, "Hearts file " + file, STYLES) : null;
        this.sound = json.has("sound") ? sound(json) : null;
        this.particle = json.has("particle") ? particle(json) : null;
        this.colors = json.has("heartColors") ? colors(json) : null;
        this.rewards = rewards(json);
    }

    public static HeartsDef parse(ResourceLocation file, JsonObject json) { return new HeartsDef(file, json); }

    @Nullable private Float hearts(JsonObject json, String name) {
        if (!json.has(name)) { return null; }
        try {
            float value = Json.getFloat(json, name);
            if (value >= 0.5F && HeartReward.halves(value)) { return value; }
        }
        catch (RuntimeException ignored) {}
        ContentLog.LOGGER.error("Hearts file {} gives {} as something other than a number of 0.5 or more in steps of 0.5, ignoring it", file, name);
        return null;
    }

    @Nullable private Boolean flag(JsonObject json) {
        try { return Json.getBoolean(json, "keepOnDeath"); }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("Hearts file {} gives keepOnDeath as something other than true or false, ignoring it", file);
            return null;
        }
    }

    @Nullable private String sound(JsonObject json) {
        try {
            String name = Json.getString(json, "sound").trim();
            ResourceLocation id = ResourceLocation.tryParse(name);
            if (name.isEmpty() || id != null && ForgeRegistries.SOUND_EVENTS.containsKey(id)) { return name; }
            ContentLog.LOGGER.error("Hearts file {} names the sound '{}', which nothing registers, so it plays none", file, name);
        }
        catch (RuntimeException ex) { ContentLog.LOGGER.error("Hearts file {} gives sound as something other than text, ignoring it", file); }
        return null;
    }

    @Nullable private String particle(JsonObject json) {
        try {
            String name = Json.getString(json, "particle").trim();
            if (name.isEmpty()) { return name; }
            ResourceLocation id = ResourceLocation.tryParse(name);
            ParticleType<?> type = id == null ? null : ForgeRegistries.PARTICLE_TYPES.getValue(id);
            if (type instanceof SimpleParticleType) { return name; }
            ContentLog.LOGGER.error("Hearts file {} names the particle '{}', which is not a simple particle Minecraft knows, so it shows none", file, name);
        }
        catch (RuntimeException ex) { ContentLog.LOGGER.error("Hearts file {} gives particle as something other than text, ignoring it", file); }
        return null;
    }

    @Nullable private int[] colors(JsonObject json) {
        try {
            List<String> names = Json.texts(Json.getJsonArray(json, "heartColors"), "heartColors");
            int[] values = new int[names.size()];
            for (int i = 0; i < values.length; i++) {
                if (!COLOR.matcher(names.get(i).trim()).matches()) { throw new IllegalArgumentException(names.get(i)); }
                values[i] = Integer.parseInt(names.get(i).trim().substring(1), 16);
            }
            if (values.length > 0) { return values; }
        }
        catch (RuntimeException ignored) {}
        ContentLog.LOGGER.error("Hearts file {} gives heartColors as something other than a list of #RRGGBB colors, ignoring it", file);
        return null;
    }

    private List<HeartReward> rewards(JsonObject json) {
        List<HeartReward> made = new ArrayList<>();
        if (!json.has("rewards")) { return made; }
        List<String> seen = new ArrayList<>();
        try {
            for (JsonElement element : Json.getJsonArray(json, "rewards")) {
                try { made.add(HeartReward.parse(file, Json.getJsonObject(element, "reward"), seen)); }
                catch (RuntimeException ex) { ContentLog.LOGGER.error("Hearts file {} skips a reward: {}", file, ex.getMessage()); }
            }
        }
        catch (RuntimeException ex) { ContentLog.LOGGER.error("Hearts file {} gives rewards as something other than a list: {}", file, ex.getMessage()); }
        return made;
    }
}
