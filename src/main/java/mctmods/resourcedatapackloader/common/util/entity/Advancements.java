package mctmods.resourcedatapackloader.common.util.entity;

import mctmods.resourcedatapackloader.client.util.ClientEarned;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.LineCompat;

import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

public final class Advancements {
    private Advancements() {}

    @Nullable private static AdvancementHolder find(MinecraftServer server, String name) {
        Identifier id = Identifier.tryParse(Settings.lower(name));
        return id == null ? null : server.getAdvancements().get(id);
    }

    public static boolean defined(MinecraftServer server, String name) { return find(server, name) != null; }

    public static boolean has(@Nullable Player player, String name) {
        if (player == null || name.isEmpty()) { return false; }
        if (player instanceof ServerPlayer held) {
            AdvancementHolder advancement = find(held.level().getServer(), name);
            return advancement != null && held.getAdvancements().getOrStartProgress(advancement).isDone();
        }
        return FMLEnvironment.getDist() == Dist.CLIENT && ClientEarned.has(Settings.lower(name));
    }

    public static void grant(ServerPlayer player, String name, String asking) {
        AdvancementHolder advancement = find(player.level().getServer(), name);
        if (advancement == null) {
            ContentLog.LOGGER.error("{} grants the advancement {}, which no pack provides, so nothing is earned", asking, name);
            return;
        }
        for (String criterion : advancement.value().criteria().keySet()) { player.getAdvancements().award(advancement, criterion); }
    }

    public static String title(MinecraftServer server, String name) {
        AdvancementHolder advancement = find(server, name);
        return advancement == null ? name : advancement.value().display().map(shown -> LineCompat.title(shown).getString()).orElse(name);
    }
}
