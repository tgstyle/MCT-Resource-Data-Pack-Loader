package mctmods.resourcedatapackloader.common.util.entity;

import mctmods.resourcedatapackloader.client.util.ClientEarned;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

public final class Advancements {
    private Advancements() {}

    @Nullable private static Advancement find(MinecraftServer server, String name) {
        ResourceLocation id = ResourceLocation.tryParse(Settings.lower(name));
        return id == null ? null : server.getAdvancements().getAdvancement(id);
    }

    public static boolean defined(MinecraftServer server, String name) { return find(server, name) != null; }

    public static boolean has(@Nullable Player player, String name) {
        if (player == null || name.isEmpty()) { return false; }
        if (player instanceof ServerPlayer held) {
            Advancement advancement = find(held.server, name);
            return advancement != null && held.getAdvancements().getOrStartProgress(advancement).isDone();
        }
        return FMLEnvironment.dist == Dist.CLIENT && ClientEarned.has(Settings.lower(name));
    }

    public static void grant(ServerPlayer player, String name, String asking) {
        Advancement advancement = find(player.server, name);
        if (advancement == null) {
            ContentLog.LOGGER.error("{} grants the advancement {}, which no pack provides, so nothing is earned", asking, name);
            return;
        }
        for (String criterion : advancement.getCriteria().keySet()) { player.getAdvancements().award(advancement, criterion); }
    }

    public static String title(MinecraftServer server, String name) {
        Advancement advancement = find(server, name);
        return advancement == null ? name : advancement.getDisplay() == null ? name : advancement.getDisplay().getTitle().getString();
    }
}
