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
    public static final List<String> LOSS_HEALTH = Arrays.asList("keep", "scale");
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

    @Nullable private Boolean flag(JsonObject json, String name) {
        try { return Json.getBoolean(json, name); }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("Hearts file {} gives {} as something other than true or false, ignoring it", file, name);
            return null;
        }
    }

    @Nullable private String sound(JsonObject json, String key) {
        try {
            String name = Json.getString(json, key).trim();
            ResourceLocation id = ResourceLocation.tryParse(name);
            if (name.isEmpty() || id != null && ForgeRegistries.SOUND_EVENTS.containsKey(id)) { return name; }
            ContentLog.LOGGER.error("Hearts file {} names the sound '{}', which nothing registers, so it plays none", file, name);
        }
        catch (RuntimeException ex) { ContentLog.LOGGER.error("Hearts file {} gives {} as something other than text, ignoring it", file, key); }
        return null;
    }

    @Nullable private String particle(JsonObject json, String key) {
        try {
            String name = Json.getString(json, key).trim();
            if (name.isEmpty()) { return name; }
            ResourceLocation id = ResourceLocation.tryParse(name);
            ParticleType<?> type = id == null ? null : ForgeRegistries.PARTICLE_TYPES.getValue(id);
            if (type instanceof SimpleParticleType) { return name; }
            ContentLog.LOGGER.error("Hearts file {} names the particle '{}', which is not a simple particle Minecraft knows, so it shows none", file, name);
        }
        catch (RuntimeException ex) { ContentLog.LOGGER.error("Hearts file {} gives {} as something other than text, ignoring it", file, key); }
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
