package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.network.RDPLNetwork;
import mctmods.resourcedatapackloader.content.extra.ContentIntroPlay;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregenHold;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.Says;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public final class ContentWelcome {
    private static final String KEY = "welcomeSays";
    private static final Set<UUID> ARRIVED = new HashSet<>();
    private static final Pattern NUMERIC = Pattern.compile("-?\\d+");

    private ContentWelcome() {}

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && !ContentPregenHold.welcomesLater(player) && ContentIntroPlay.skips(player)) { welcome(player); }
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { ARRIVED.remove(event.getEntity().getUUID()); }

    public static boolean arrived(ServerPlayer player) { return ARRIVED.contains(player.getUUID()); }

    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (ContentPregen.busy() || !(event.getEntity() instanceof ServerPlayer player)) { return; }
        if (greetingFor(player, event.getTo(), false) != null) { welcome(player); }
    }

    private static List<String> entries() { return ContentControl.list(ContentControl.CHUNKS, KEY, Config.chunks.welcomeSays()); }

    public static void welcome(ServerPlayer player) {
        greet(player);
        if (!ARRIVED.add(player.getUUID())) { return; }
        mctmods.resourcedatapackloader.content.card.CardEvents.joined(player);
        ContentScoring.greet(player);
        ContentTeams.greet(player);
    }

    private static void greet(ServerPlayer player) {
        List<String> entries = entries();
        boolean atDefault = entries.size() == 1 && entries.getFirst().trim().equals(Config.WELCOME);
        String greeting = atDefault ? Lang.tr(player, "rdpl.pregen.welcome") : greetingFor(player, player.level().dimension(), true);
        if (greeting == null || greeting.isEmpty()) { return; }
        if (Says.card()) {
            Says.tell(player, greeting, ChatFormatting.GREEN);
            return;
        }
        show(player, greeting, ChatFormatting.GREEN);
    }

    @Nullable private static String greetingFor(ServerPlayer player, ResourceKey<Level> dimension, boolean fallBack) {
        String everywhere = null;
        for (String entry : entries()) {
            String[] parts = entry.split("=", 2);
            ResourceKey<Level> named = parts.length == 2 ? dimensionNamed(player, parts[0].trim()) : null;
            if (named == null) {
                if (everywhere == null) { everywhere = entry.trim(); }
            }
            else if (named.equals(dimension)) { return parts[1].trim(); }
        }
        return fallBack ? everywhere : null;
    }

    @Nullable private static ResourceKey<Level> dimensionNamed(ServerPlayer player, String name) {
        String named = ContentFormats.dimensionId(name);
        ResourceLocation id = ResourceLocation.tryParse(named);
        if (id == null) { return null; }
        ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, id);
        return named.contains(":") || NUMERIC.matcher(name.trim()).matches() || player.serverLevel().getServer().getLevel(key) != null ? key : null;
    }

    public static void show(ServerPlayer player, String said, ChatFormatting color) {
        if (said.isEmpty() || RDPLNetwork.sendNote(player, said)) { return; }
        Says.title(player, 10, 70, 20, "", said, color);
    }
}
