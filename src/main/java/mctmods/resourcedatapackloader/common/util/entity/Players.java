package mctmods.resourcedatapackloader.common.util.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.List;
import javax.annotation.Nullable;

public final class Players {
    private Players() {}

    public static MinecraftServer server() { return FMLCommonHandler.instance().getMinecraftServerInstance(); }

    public static List<EntityPlayerMP> online(MinecraftServer server) { return server.getPlayerList().getPlayers(); }

    public static List<EntityPlayerMP> online() { return online(server()); }

    @Nullable public static EntityPlayerMP serverPlayer(@Nullable Entity entity) { return entity instanceof EntityPlayerMP ? (EntityPlayerMP) entity : null; }
}
