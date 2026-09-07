package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.extra.ContentIntroPlay;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.Says;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent;
import java.util.List;
import javax.annotation.Nullable;

public final class ContentWelcome {
    private static final String KEY = "welcomeSays";

    private ContentWelcome() {}

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && !ContentPregen.welcomesLater(player) && ContentIntroPlay.skips(player)) { welcome(player); }
    }

    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        String greeting = greetingFor(player, event.getTo(), false);
        if (greeting != null) { send(player, greeting); }
    }

    private static List<String> entries() { return ContentControl.list(ContentControl.CHUNKS, KEY, Config.chunks.welcomeSays()); }

    public static void welcome(ServerPlayer player) {
        List<String> entries = entries();
        boolean atDefault = entries.size() == 1 && entries.get(0).trim().equals(Config.WELCOME);
        String greeting = atDefault ? Lang.tr(player, "rdpl.pregen.welcome") : greetingFor(player, player.level().dimension(), true);
        if (greeting != null && !greeting.isEmpty()) { send(player, greeting); }
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
        ResourceLocation id = ResourceLocation.tryParse(name);
        if (id == null) { return null; }
        ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, id);
        return player.serverLevel().getServer().getLevel(key) == null ? null : key;
    }

    private static void send(ServerPlayer player, String greeting) {
        if (Says.card()) {
            Says.tell(player, greeting, ChatFormatting.GREEN);
            return;
        }
        player.connection.send(new ClientboundSetTitlesAnimationPacket(10, 70, 20));
        player.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal(greeting).withStyle(ChatFormatting.GREEN)));
    }
}
