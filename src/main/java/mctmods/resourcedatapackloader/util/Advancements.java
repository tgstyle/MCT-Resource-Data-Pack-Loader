package mctmods.resourcedatapackloader.util;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import java.util.Locale;
import javax.annotation.Nullable;

public final class Advancements {
    private Advancements() {}

    @Nullable public static AdvancementHolder find(MinecraftServer server, String name) {
        ResourceLocation id = ResourceLocation.tryParse(name.trim().toLowerCase(Locale.ROOT));
        return id == null ? null : server.getAdvancements().get(id);
    }

    public static boolean has(@Nullable Player player, String name) {
        if (player == null || name.isEmpty()) { return false; }
        if (player instanceof ServerPlayer held) {
            AdvancementHolder advancement = find(held.server, name);
            return advancement != null && held.getAdvancements().getOrStartProgress(advancement).isDone();
        }
        return FMLEnvironment.dist == Dist.CLIENT && ClientEarned.has(name.trim().toLowerCase(Locale.ROOT));
    }

    public static boolean grant(ServerPlayer player, String name) {
        AdvancementHolder advancement = find(player.server, name);
        if (advancement == null) { return false; }
        for (String criterion : advancement.value().criteria().keySet()) { player.getAdvancements().award(advancement, criterion); }
        return true;
    }

    public static String title(MinecraftServer server, String name) {
        AdvancementHolder advancement = find(server, name);
        return advancement == null ? name : advancement.value().display().map(shown -> shown.getTitle().getString()).orElse(name);
    }
}
