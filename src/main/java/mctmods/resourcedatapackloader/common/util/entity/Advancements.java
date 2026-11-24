package mctmods.resourcedatapackloader.common.util.entity;

import mctmods.resourcedatapackloader.client.util.ClientAdvancements;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.SideUtils;

import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

public final class Advancements {
    private Advancements() {}

    @Nullable public static Advancement find(MinecraftServer server, String name) { return server.getAdvancementManager().getAdvancement(new ResourceLocation(name)); }

    public static boolean has(EntityPlayer player, String name) {
        if (name.isEmpty()) { return false; }
        if (player instanceof EntityPlayerMP) {
            MinecraftServer server = player.getServer();
            if (server == null) { return false; }
            Advancement advancement = find(server, name);
            return advancement != null && ((EntityPlayerMP) player).getAdvancements().getProgress(advancement).isDone();
        }
        return SideUtils.isClient() && ClientAdvancements.has(name);
    }

    public static void grant(EntityPlayerMP player, String name, String asking) {
        MinecraftServer server = player.getServer();
        if (server == null) { return; }
        Advancement advancement = find(server, name);
        if (advancement == null) {
            ContentLog.LOGGER.error("{} grants the advancement {}, which no pack provides, so nothing is earned", asking, name);
            return;
        }
        PlayerAdvancements progress = player.getAdvancements();
        for (String criterion : advancement.getCriteria().keySet()) { progress.grantCriterion(advancement, criterion); }
    }
}
