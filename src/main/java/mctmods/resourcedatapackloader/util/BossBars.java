package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.def.BossBackgroundDef;
import mctmods.resourcedatapackloader.content.def.BossBarDef;
import mctmods.resourcedatapackloader.network.RDPLNetwork;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.WorldServer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;

public final class BossBars {
    public static final String KEY = "bossBar";

    private BossBars() {}

    @Nullable public static BossBarDef read(Object owner, JsonObject json) { return read(owner, json, BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS); }

    @Nullable public static BossBarDef read(Object owner, JsonObject json, BossInfo.Color fallbackColor, BossInfo.Overlay fallbackStyle) {
        if (!json.has(KEY)) { return null; }
        if (!json.get(KEY).isJsonObject()) {
            ContentLog.LOGGER.error("{} sets {} to something that is not an object with a name, color and style, so it has no boss bar", owner, KEY);
            return null;
        }
        JsonObject bar = json.getAsJsonObject(KEY);
        return new BossBarDef(Json.getString(bar, "name", "").trim(), pick(owner, bar, "color", BossInfo.Color.class, fallbackColor), pick(owner, bar, "style", BossInfo.Overlay.class, fallbackStyle), background(owner, bar));
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

    private static final class Backed extends BossInfoServer {
        final BossBackgroundDef background;

        Backed(ITextComponent name, BossInfo.Color color, BossInfo.Overlay style, BossBackgroundDef background) {
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

    public static BossInfoServer create(ITextComponent name, BossInfo.Color color, BossInfo.Overlay style) { return new BossInfoServer(name, color, style); }

    public static BossInfoServer create(BossBarDef def, ITextComponent fallbackName) {
        ITextComponent name = def.name.isEmpty() ? fallbackName : new TextComponentString(def.name);
        if (def.background == null) { return create(name, def.color, def.style); }
        return new Backed(name, def.color, def.style, def.background);
    }

    public static void progress(BossInfoServer bar, float fraction) { bar.setPercent(MathHelper.clamp(fraction, 0.0F, 1.0F)); }

    public static void sync(BossInfoServer bar, Collection<EntityPlayerMP> present) {
        for (EntityPlayerMP watching : new ArrayList<>(bar.getPlayers())) {
            if (!present.contains(watching)) { remove(bar, watching); }
        }
        for (EntityPlayerMP player : present) {
            if (bar instanceof Backed && !bar.getPlayers().contains(player)) { RDPLNetwork.sendBossBackground(player, bar.getUniqueId(), ((Backed) bar).background); }
            bar.addPlayer(player);
        }
    }

    private static void remove(BossInfoServer bar, EntityPlayerMP player) {
        bar.removePlayer(player);
        if (bar instanceof Backed) { RDPLNetwork.sendBossBackground(player, bar.getUniqueId(), null); }
    }

    public static void clear(BossInfoServer bar) {
        for (EntityPlayerMP watching : new ArrayList<>(bar.getPlayers())) { remove(bar, watching); }
    }

    public static List<EntityPlayerMP> near(WorldServer world, double x, double y, double z, double reach) {
        List<EntityPlayerMP> present = new ArrayList<>();
        double limit = reach * reach;
        for (EntityPlayer player : world.playerEntities) {
            if (player instanceof EntityPlayerMP && player.isEntityAlive() && !player.isSpectator() && player.getDistanceSq(x, y, z) < limit) { present.add((EntityPlayerMP) player); }
        }
        return present;
    }
}
