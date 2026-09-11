package mctmods.resourcedatapackloader.util;

import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;

public final class Advancements {
    private Advancements() {}

    public static boolean has(EntityPlayer player, String name) {
        if (name.isEmpty()) { return false; }
        if (player instanceof EntityPlayerMP) {
            MinecraftServer server = player.getServer();
            if (server == null) { return false; }
            Advancement advancement = server.getAdvancementManager().getAdvancement(new ResourceLocation(name));
            return advancement != null && ((EntityPlayerMP) player).getAdvancements().getProgress(advancement).isDone();
        }
        return FMLCommonHandler.instance().getSide().isClient() && ClientAdvancements.has(name);
    }
}
