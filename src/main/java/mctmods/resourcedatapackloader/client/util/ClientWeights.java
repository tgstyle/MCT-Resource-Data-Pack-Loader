package mctmods.resourcedatapackloader.client.util;

import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;
import mctmods.resourcedatapackloader.common.weights.ContentWeights;
import mctmods.resourcedatapackloader.core.network.WeightMessage;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public final class ClientWeights {
    private static final int PLAIN = 0xFFFFFF;
    private static boolean known;
    private static boolean exempt;
    private static int total;
    private static int limit;
    private static int tier = -1;

    private ClientWeights() {}

    public static void receive(WeightMessage message) {
        known = true;
        total = message.total();
        limit = message.limit();
        tier = message.tier();
        exempt = message.exempt();
    }

    public static boolean active() { return known && !exempt && limit > 0 && ContentWeights.any(); }

    public static int total() { return total; }

    public static int limit() { return limit; }

    public static boolean barShown() {
        if (!active()) { return false; }
        String show = ContentWeights.settings().hudShow;
        return WeightDef.HUD_ALWAYS.equals(show) || WeightDef.HUD_TIERS.equals(show) && tier >= 0;
    }

    public static float fill() {
        double ceiling = Math.max(1.0D, ContentWeights.settings().hardLimit);
        return (float) Math.min(1.0D, (double) total / limit / ceiling);
    }

    public static int color() {
        WeightDef.Tier now = current();
        return now == null ? PLAIN : now.color();
    }

    public static String format(long thousandths) {
        String text = String.format(Locale.ROOT, "%.2f", thousandths / 1000.0D);
        text = text.contains(".") ? text.replaceAll("0+$", "").replaceAll("\\.$", "") : text;
        String unit = ContentWeights.settings().unit;
        return unit.isEmpty() ? text : text + " " + unit;
    }

    @Nullable private static WeightDef.Tier current() {
        if (!active() || tier < 0 || tier >= ContentWeights.settings().tiers.size()) { return null; }
        return ContentWeights.settings().tiers.get(tier);
    }

    public static void clear() {
        known = false;
        exempt = false;
        total = 0;
        limit = 0;
        tier = -1;
    }

    public static void tick() {
        WeightDef.Tier now = current();
        LocalPlayer player = Minecraft.getInstance().player;
        if (now != null && !now.sprint() && player != null) { player.setSprinting(false); }
    }
}
