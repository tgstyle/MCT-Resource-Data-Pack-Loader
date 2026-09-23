package mctmods.resourcedatapackloader.util;

import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import java.util.Locale;
import javax.annotation.Nullable;

public final class Advancements {
    private Advancements() {}

    @Nullable public static Advancement find(MinecraftServer server, String name) {
        ResourceLocation id = ResourceLocation.tryParse(name.trim().toLowerCase(Locale.ROOT));
        return id == null ? null : server.getAdvancements().getAdvancement(id);
    }

    public static boolean has(@Nullable Player player, String name) {
        if (player == null || name.isEmpty()) { return false; }
        if (player instanceof ServerPlayer held) {
            Advancement advancement = find(held.server, name);
            return advancement != null && held.getAdvancements().getOrStartProgress(advancement).isDone();
        }
        return FMLEnvironment.dist == Dist.CLIENT && ClientEarned.has(name.trim().toLowerCase(Locale.ROOT));
    }

    public static boolean grant(ServerPlayer player, String name) {
        Advancement advancement = find(player.server, name);
        if (advancement == null) { return false; }
        for (String criterion : advancement.getCriteria().keySet()) { player.getAdvancements().award(advancement, criterion); }
        return true;
    }

    public static String title(MinecraftServer server, String name) {
        Advancement advancement = find(server, name);
        return advancement == null ? name : advancement.getDisplay() == null ? name : advancement.getDisplay().getTitle().getString();
    }
}
