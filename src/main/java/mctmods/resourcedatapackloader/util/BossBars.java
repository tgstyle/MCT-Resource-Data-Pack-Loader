package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.def.BossBackgroundDef;
import mctmods.resourcedatapackloader.content.def.BossBarDef;
import mctmods.resourcedatapackloader.network.RDPLNetwork;

import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;

public final class BossBars {
    public static final String KEY = "bossBar";

    private BossBars() {}

    @Nullable public static BossBarDef read(Object owner, JsonObject json) { return read(owner, json, BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS); }

    @Nullable public static BossBarDef read(Object owner, JsonObject json, BossEvent.BossBarColor fallbackColor, BossEvent.BossBarOverlay fallbackStyle) {
        if (!json.has(KEY)) { return null; }
        if (!json.get(KEY).isJsonObject()) {
            ContentLog.LOGGER.error("{} sets {} to something that is not an object with a name, color and style, so it has no boss bar", owner, KEY);
            return null;
        }
        JsonObject bar = json.getAsJsonObject(KEY);
        return new BossBarDef(Json.getString(bar, "name", "").trim(), pick(owner, bar, "color", BossEvent.BossBarColor.class, fallbackColor), pick(owner, bar, "style", BossEvent.BossBarOverlay.class, fallbackStyle), background(owner, bar));
    }

    @Nullable private static BossBackgroundDef background(Object owner, JsonObject bar) {
        if (!bar.has("background")) { return null; }
        if (!bar.get("background").isJsonObject()) {
            ContentLog.LOGGER.error("{} sets {} background to something that is not an object with a texture, so it has no background", owner, KEY);
            return null;
        }
        JsonObject background = bar.getAsJsonObject("background");
        String texture = Json.getString(background, "texture", "").trim();
        if (texture.isEmpty()) {
            ContentLog.LOGGER.error("{} sets {} background without a texture, so it has no background", owner, KEY);
            return null;
        }
        VanillaClients.clientDrawn(owner, KEY + ".background");
        return new BossBackgroundDef(texture, Json.getInt(background, "width", 0), Json.getInt(background, "height", 0), Json.getInt(background, "x", 0), Json.getInt(background, "y", 0));
    }

    private static final class Backed extends ServerBossEvent {
        final BossBackgroundDef background;

        Backed(Component name, BossEvent.BossBarColor color, BossEvent.BossBarOverlay style, BossBackgroundDef background) {
            super(name, color, style);
            this.background = background;
        }
    }

    private static <E extends Enum<E>> E pick(Object owner, JsonObject bar, String member, Class<E> type, E fallback) {
        if (!bar.has(member)) { return fallback; }
        String asked = Json.getString(bar, member, "");
        E found = Enums.byName(type, asked);
        if (found != null) { return found; }
        List<String> names = new ArrayList<>();
        for (E value : type.getEnumConstants()) { names.add(value.name().toLowerCase(Locale.ROOT)); }
        ContentLog.LOGGER.error("{} sets {} {} to '{}', which is not one of {}, so {} is used instead", owner, KEY, member, asked, names, fallback.name().toLowerCase(Locale.ROOT));
        return fallback;
    }

    public static ServerBossEvent create(Component name, BossEvent.BossBarColor color, BossEvent.BossBarOverlay style) { return new ServerBossEvent(name, color, style); }

    public static ServerBossEvent create(BossBarDef def, Component fallbackName) {
        Component name = def.name().isEmpty() ? fallbackName : Component.literal(def.name());
        if (def.background() == null) { return create(name, def.color(), def.style()); }
        return new Backed(name, def.color(), def.style(), def.background());
    }

    public static void progress(ServerBossEvent bar, float fraction) { bar.setProgress(Mth.clamp(fraction, 0.0F, 1.0F)); }

    public static void sync(ServerBossEvent bar, Collection<ServerPlayer> present) {
        for (ServerPlayer watching : new ArrayList<>(bar.getPlayers())) {
            if (!present.contains(watching)) { remove(bar, watching); }
        }
        for (ServerPlayer player : present) {
            if (bar instanceof Backed backed && !bar.getPlayers().contains(player)) { RDPLNetwork.sendBossBackground(player, bar.getId(), backed.background); }
            bar.addPlayer(player);
        }
    }

    private static void remove(ServerBossEvent bar, ServerPlayer player) {
        bar.removePlayer(player);
        if (bar instanceof Backed) { RDPLNetwork.sendBossBackground(player, bar.getId(), null); }
    }

    public static void clear(ServerBossEvent bar) {
        for (ServerPlayer watching : new ArrayList<>(bar.getPlayers())) { remove(bar, watching); }
    }

    public static List<ServerPlayer> near(ServerLevel level, double x, double y, double z, double reach) {
        List<ServerPlayer> present = new ArrayList<>();
        double limit = reach * reach;
        for (ServerPlayer player : level.players()) {
            if (player.isAlive() && !player.isSpectator() && player.distanceToSqr(x, y, z) < limit) { present.add(player); }
        }
        return present;
    }
}
