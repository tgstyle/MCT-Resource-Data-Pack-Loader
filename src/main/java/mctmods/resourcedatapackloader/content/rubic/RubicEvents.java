package mctmods.resourcedatapackloader.content.rubic;

import mctmods.resourcedatapackloader.content.rubic.server.chunkio.interfaces.ICubeIO;
import mctmods.resourcedatapackloader.content.rubic.world.WorldSavedRubicData;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICubeProviderInternal;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldSettings;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldProvider;
import mctmods.resourcedatapackloader.network.MessageRubicWorldData;
import mctmods.resourcedatapackloader.network.RDPLNetwork;
import mctmods.resourcedatapackloader.util.IntRange;
import mctmods.resourcedatapackloader.util.ReflectionUtil;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import java.io.IOException;
import java.util.List;

public class RubicEvents {
    @SubscribeEvent public void onWorldAttachCapabilities(AttachCapabilitiesEvent<World> evt) {
        if (evt.getObject().isRemote || !(evt.getObject() instanceof WorldServer)) { return; }
        WorldServer world = (WorldServer) evt.getObject();

        WorldSavedRubicData savedData =
                (WorldSavedRubicData) evt.getObject().getPerWorldStorage().getOrLoadData(WorldSavedRubicData.class, "rdplRubicData");
        boolean savedRubic = savedData != null && savedData.isRubicWorld;
        if (savedRubic && mctmods.resourcedatapackloader.util.Config.content.vanillaClients) {
            mctmods.resourcedatapackloader.content.rubic.RubicWorldControl.standDownForVanillaClients("this world was made as a rubic world");
        }
        boolean claimed = mctmods.resourcedatapackloader.content.rubic.RubicWorldControl.claims(world.provider.getDimension());
        boolean rubicWorldInfo = ((IRubicWorldSettings) world.getWorldInfo()).rdpl$isRubic() && claimed && (savedData == null || savedData.isRubicWorld);
        boolean isRubic = savedRubic || rubicWorldInfo;
        if (claimed && mctmods.resourcedatapackloader.content.rubic.RubicWorldControl.wanted()) { isRubic = true; }

        if (savedData == null) {
            int minY = mctmods.resourcedatapackloader.content.rubic.RubicWorldControl.minHeight();
            int maxY = mctmods.resourcedatapackloader.content.rubic.RubicWorldControl.maxHeight();
            if (world.provider.getDimension() != 0) {
                WorldSavedRubicData overworld = (WorldSavedRubicData) DimensionManager
                        .getWorld(0).getPerWorldStorage().getOrLoadData(WorldSavedRubicData.class, "rdplRubicData");
                if (overworld != null) {
                    minY = overworld.minHeight;
                    maxY = overworld.maxHeight;
                }
            }
            savedData = new WorldSavedRubicData("rdplRubicData", isRubic, minY, maxY);
        }
        savedData.markDirty();
        evt.getObject().getPerWorldStorage().setData("rdplRubicData", savedData);
        evt.getObject().getPerWorldStorage().saveAllData();

        if (!isRubic) { return; }

        if (shouldSkipWorld(world)) {
            Rubic.LOGGER.info("Skipping world {} with type {} due to potential compatibility issues", evt.getObject(), evt.getObject().getWorldType());
            return;
        }
        Rubic.LOGGER.info("Initializing world {} with type {}", evt.getObject(), evt.getObject().getWorldType());

        IntRange generationRange = new IntRange(0, ((IRubicWorldProvider) world.provider).rdpl$getOriginalActualHeight());

        int minHeight = savedData.minHeight;
        int maxHeight = savedData.maxHeight;
        ((IRubicWorldInternal.Server) world).rdpl$initRubicWorldServer(new IntRange(minHeight, maxHeight), generationRange);
    }

    @SubscribeEvent public void onWorldServerTick(TickEvent.WorldTickEvent evt) {
        WorldServer world = (WorldServer) evt.world;
        if (evt.phase == TickEvent.Phase.END && ((IRubicWorld) world).rdpl$isRubicWorld() && evt.side == Side.SERVER) { ((IRubicWorldInternal) world).rdpl$tickRubicWorld(); }
    }

    @SubscribeEvent public void onPlayerJoinWorld(EntityJoinWorldEvent evt) {
        if (evt.getEntity() instanceof EntityPlayerMP && ((IRubicWorld) evt.getWorld()).rdpl$isRubicWorld()) {
            RDPLNetwork.sendTo(new MessageRubicWorldData((WorldServer) evt.getWorld()), (EntityPlayerMP) evt.getEntity());
        }
    }

    @SubscribeEvent public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) { checkClientSupport(event.player); }

    @SubscribeEvent public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) { checkClientSupport(event.player); }

    private static void checkClientSupport(EntityPlayer playerIn) {
        if (!(playerIn instanceof EntityPlayerMP)) { return; }
        EntityPlayerMP player = (EntityPlayerMP) playerIn;
        if (!((IRubicWorld) player.world).rdpl$isRubicWorld()) { return; }
        NetHandlerPlayServer connection = player.connection;
        if (connection == null || connection.netManager == null) { return; }
        NetworkDispatcher dispatcher = connection.netManager.channel().attr(NetworkDispatcher.FML_DISPATCHER).get();
        if (dispatcher == null) { return; }
        if (!dispatcher.getModList().containsKey(Rubic.MODID)) {
            connection.disconnect(new TextComponentString("This world requires Resource Data Pack Loader on the client."));
        }
    }

    @SuppressWarnings("unchecked") private final List<Class<?>> allowedServerWorldClasses = ImmutableList.copyOf(new Class[]{
            WorldServer.class,
            WorldServerMulti.class,
            ReflectionUtil.getClassOrDefault("WorldServerOF", Object.class),
            ReflectionUtil.getClassOrDefault("WorldServerMultiOF", Object.class),
            ReflectionUtil.getClassOrDefault("net.optifine.override.WorldServerOF", Object.class),
            ReflectionUtil.getClassOrDefault("net.optifine.override.WorldServerMultiOF", Object.class),
            ReflectionUtil.getClassOrDefault("com.forgeessentials.multiworld.WorldServerMultiworld", Object.class)
    });

    @SuppressWarnings("unchecked") private final List<Class<? extends IChunkProvider>> allowedServerChunkProviderClasses = ImmutableList.copyOf(new Class[]{
            ChunkProviderServer.class
    });

    private boolean shouldSkipWorld(World world) {
        return !allowedServerWorldClasses.contains(world.getClass())
                || !allowedServerChunkProviderClasses.contains(world.getChunkProvider().getClass());
    }

    @SubscribeEvent public void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld().isRemote || !((IRubicWorld) event.getWorld()).rdpl$isRubicWorld()) { return; }
        IRubicWorld world = (IRubicWorld) event.getWorld();
        if (!world.rdpl$isRubicWorld()) { return; }
        ICubeIO io = ((ICubeProviderInternal.Server) world.rdpl$getCubeCache()).getCubeIO();
        try {
            io.close();
        } catch (IOException e) {
            Rubic.LOGGER.catching(e);
        }
    }
}
