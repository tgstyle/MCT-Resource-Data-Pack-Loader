package mctmods.resourcedatapackloader.common.pack.definitions.entities;

import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Particles;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class HeartsDef {
    public static final float VANILLA = 10.0F;
    public static final List<String> STYLES = Arrays.asList("bar", "chat");
    public static final List<String> LOSS_HEALTH = Arrays.asList("keep", "scale");
    private static final List<String> HEARTS_KEYS = Collections.unmodifiableList(Arrays.asList("startHearts", "cap", "floor", "keepOnDeath", "floorDebt", "floorKills", "style", "lossHealth", "sound", "particle",
            "lossSound", "lossParticle", "heartColors", "rewards", "losses"));
    private static final Pattern COLOR = Pattern.compile("#[0-9A-Fa-f]{6}");
    public final ResourceLocation file;
    @Nullable public final Float startHearts;
    @Nullable public final Float cap;
    @Nullable public final Float floor;
    @Nullable public final Boolean keepOnDeath;
    @Nullable public final Boolean floorDebt;
    @Nullable public final Boolean floorKills;
    @Nullable public final String style;
    @Nullable public final String lossHealth;
    @Nullable public final String sound;
    @Nullable public final String particle;
    @Nullable public final String lossSound;
    @Nullable public final String lossParticle;
    @Nullable public final int[] colors;
    public final List<HeartReward> rewards;
    public final List<HeartReward> losses;

    private HeartsDef(ResourceLocation file, JsonObject json) {
        this.file = file;
        this.startHearts = hearts(json, "startHearts");
        this.cap = hearts(json, "cap");
        this.floor = hearts(json, "floor");
        this.keepOnDeath = json.has("keepOnDeath") ? flag(json, "keepOnDeath") : null;
        this.floorDebt = json.has("floorDebt") ? flag(json, "floorDebt") : null;
        this.floorKills = json.has("floorKills") ? flag(json, "floorKills") : null;
        this.style = json.has("style") ? Json.choice(json, "style", null, "Hearts file " + file, STYLES) : null;
        this.lossHealth = json.has("lossHealth") ? Json.choice(json, "lossHealth", null, "Hearts file " + file, LOSS_HEALTH) : null;
        this.sound = json.has("sound") ? sound(json, "sound") : null;
        this.particle = json.has("particle") ? particle(json, "particle") : null;
        this.lossSound = json.has("lossSound") ? sound(json, "lossSound") : null;
        this.lossParticle = json.has("lossParticle") ? particle(json, "lossParticle") : null;
        this.colors = json.has("heartColors") ? colors(json) : null;
        this.rewards = rules(json, "rewards", false);
        this.losses = rules(json, "losses", true);
    }

    public static HeartsDef parse(ResourceLocation file, JsonObject json) {
        Json.unknownKeys(json, "the top level", HEARTS_KEYS);
        return new HeartsDef(file, json);
    }

    @Nullable private Float hearts(JsonObject json, String name) {
        return Json.optional(json, name, owner(), "a number of 0.5 or more in steps of 0.5", element -> {
            float value = Json.getFloat(element, name);
            return value >= 0.5F && HeartReward.halves(value) ? value : null;
        });
    }

    @Nullable private Boolean flag(JsonObject json, String name) {
        return Json.optional(json, name, owner(), "true or false", element -> Json.getBoolean(element, name));
    }

    @Nullable private String sound(JsonObject json, String key) {
        try {
            String name = Json.getString(json, key).trim();
            if (name.isEmpty() || ForgeRegistries.SOUND_EVENTS.containsKey(new ResourceLocation(name))) { return name; }
            ContentLog.LOGGER.error("Hearts file {} names the sound '{}', which nothing registers, so it plays none", file, name);
        }
        catch (RuntimeException ex) { ContentLog.LOGGER.error("Hearts file {} gives {} as something other than text, ignoring it", file, key); }
        return null;
    }

    @Nullable private String particle(JsonObject json, String key) {
        try {
            String name = Json.getString(json, key).trim();
            if (name.isEmpty()) { return name; }
            if (Particles.simple(name) != null) { return name; }
            ContentLog.LOGGER.error("Hearts file {} names the particle '{}', which is not a simple particle Minecraft knows, so it shows none", file, name);
        }
        catch (RuntimeException ex) { ContentLog.LOGGER.error("Hearts file {} gives {} as something other than text, ignoring it", file, key); }
        return null;
    }

    private String owner() { return "Hearts file " + file; }

    @Nullable private int[] colors(JsonObject json) {
        return Json.optional(json, "heartColors", owner(), "a list of #RRGGBB colors", element -> {
            List<String> names = Json.texts(Json.getJsonArray(element, "heartColors"), "heartColors");
            int[] values = new int[names.size()];
            for (int i = 0; i < values.length; i++) {
                if (!COLOR.matcher(names.get(i).trim()).matches()) { throw new IllegalArgumentException(names.get(i)); }
                values[i] = Integer.parseInt(names.get(i).trim().substring(1), 16);
            }
            return values.length > 0 ? values : null;
        });
    }

    private List<HeartReward> rules(JsonObject json, String name, boolean loss) {
        List<HeartReward> made = new ArrayList<>();
        if (!json.has(name)) { return made; }
        List<String> seen = new ArrayList<>();
        String noun = loss ? "loss" : "reward";
        try {
            for (JsonElement element : Json.getJsonArray(json, name)) {
                try { made.add(HeartReward.parse(file, Json.getJsonObject(element, noun), seen, loss)); }
                catch (RuntimeException ex) { ContentLog.LOGGER.error("Hearts file {} skips a {}: {}", file, noun, ex.getMessage()); }
            }
        }
        catch (RuntimeException ex) { ContentLog.LOGGER.error("Hearts file {} gives {} as something other than a list: {}", file, name, ex.getMessage()); }
        return made;
    }
}
