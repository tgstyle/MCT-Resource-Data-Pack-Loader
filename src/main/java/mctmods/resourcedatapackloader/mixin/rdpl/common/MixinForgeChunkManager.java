package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicTicketInternal;
import mctmods.resourcedatapackloader.content.rubic.world.chunkloader.RubicChunkManager;

import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mixin(ForgeChunkManager.class) public abstract class MixinForgeChunkManager {
    @Shadow(remap = false) private static Map<World, Multimap<String, ForgeChunkManager.Ticket>> tickets;

    @Shadow(remap = false) private static Map<World, ImmutableSetMultimap<ChunkPos, ForgeChunkManager.Ticket>> forcedChunks;

    @Shadow(remap = false) private static Map<String, ForgeChunkManager.LoadingCallback> callbacks;

    @Shadow(remap = false) private static BiMap<UUID, ForgeChunkManager.Ticket> pendingEntities;

    @Shadow(remap = false) public static int getMaxTicketLengthFor(String modId) { throw new Error("WTF!?"); }

    @Shadow(remap = false) private static int dormantChunkCacheSize;

    @Shadow(remap = false) private static Map<Object, Object> dormantChunkCache;

    @Shadow(remap = false) private static SetMultimap<String, ForgeChunkManager.Ticket> playerTickets;

    /**
     * @author tgstyle
     * @reason Rebuild ticket loading around RubicChunkManager so cube tickets deserialize with their rubic data.
     */
    @Overwrite(remap = false) static void loadWorld(World world) {
        ArrayListMultimap<String, ForgeChunkManager.Ticket> newTickets = ArrayListMultimap.create();
        tickets.put(world, newTickets);
        forcedChunks.put(world, ImmutableSetMultimap.of());
        if (!(world instanceof WorldServer)) { return; }
        if (dormantChunkCacheSize != 0) { dormantChunkCache.put(world, CacheBuilder.newBuilder().maximumSize(dormantChunkCacheSize).build()); }
        WorldServer worldServer = (WorldServer) world;
        File chunkDir = worldServer.getChunkSaveLocation();
        File chunkLoaderData = new File(chunkDir, "forcedchunks.dat");
        if (chunkLoaderData.exists() && chunkLoaderData.isFile()) {
            ArrayListMultimap<String, ForgeChunkManager.Ticket> loadedTickets = ArrayListMultimap.create();
            Map<String, ListMultimap<String, ForgeChunkManager.Ticket>> playerLoadedTickets = Maps.newHashMap();
            NBTTagCompound forcedChunkData;
            try {
                forcedChunkData = CompressedStreamTools.read(chunkLoaderData);
            } catch (IOException e) {
                FMLLog.log.warn("Unable to read forced chunk data at {} - it will be ignored", chunkLoaderData.getAbsolutePath(), e);
                return;
            }
            if (forcedChunkData == null) {
                FMLLog.log.warn("Unable to read forced chunk data at {} - it will be ignored", chunkLoaderData.getAbsolutePath());
                return;
            }
            NBTTagList ticketList = forcedChunkData.getTagList("TicketList", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < ticketList.tagCount(); i++) {
                NBTTagCompound ticketHolder = ticketList.getCompoundTagAt(i);
                String modId = ticketHolder.getString("Owner");
                boolean isPlayer = ForgeVersion.MOD_ID.equals(modId);
                if (!isPlayer && !Loader.isModLoaded(modId)) {
                    FMLLog.log.warn("Found chunkloading data for mod {} which is currently not available or active - it will be removed from the "
                            + "world save", modId);
                    continue;
                }
                if (!isPlayer && !callbacks.containsKey(modId)) {
                    FMLLog.log.warn("The mod {} has registered persistent chunkloading data but doesn't seem to want to be called back with it - it "
                            + "will be removed from the world save", modId);
                    continue;
                }
                NBTTagList tickets = ticketHolder.getTagList("Tickets", Constants.NBT.TAG_COMPOUND);
                for (int j = 0; j < tickets.tagCount(); j++) {
                    NBTTagCompound ticket = tickets.getCompoundTagAt(j);
                    modId = ticket.hasKey("ModId") ? ticket.getString("ModId") : modId;
                    ForgeChunkManager.Type type = ForgeChunkManager.Type.values()[ticket.getByte("Type")];
                    ForgeChunkManager.Ticket tick = RubicChunkManager.makeTicket(modId, type, world);
                    RubicChunkManager.onDeserializeTicket(ticket, tick);
                    if (ticket.hasKey("ModData")) { ((IRubicTicketInternal) tick).setModData(ticket.getCompoundTag("ModData")); }
                    if (ticket.hasKey("Player")) {
                        ((IRubicTicketInternal) tick).setPlayer(ticket.getString("Player"));
                        if (!playerLoadedTickets.containsKey(tick.getModId())) { playerLoadedTickets.put(modId, ArrayListMultimap.create()); }
                        playerLoadedTickets.get(tick.getModId()).put(tick.getPlayerName(), tick);
                    }
                    else { loadedTickets.put(modId, tick); }
                    if (type == ForgeChunkManager.Type.ENTITY) {
                        ((IRubicTicketInternal) tick).setEntityChunkX(ticket.getInteger("chunkX"));
                        ((IRubicTicketInternal) tick).setEntityChunkZ(ticket.getInteger("chunkZ"));
                        UUID uuid = new UUID(ticket.getLong("PersistentIDMSB"), ticket.getLong("PersistentIDLSB"));
                        pendingEntities.put(uuid, tick);
                    }
                }
            }
            for (ForgeChunkManager.Ticket tick : ImmutableSet.copyOf(pendingEntities.values())) {
                if (tick.getType() == ForgeChunkManager.Type.ENTITY && tick.getEntity() == null) {
                    world.getChunk(((IRubicTicketInternal) tick).getEntityChunkX(), ((IRubicTicketInternal) tick).getEntityChunkZ());
                    RubicChunkManager.onLoadEntityTicketChunk(world, tick);
                }
            }
            for (ForgeChunkManager.Ticket tick : ImmutableSet.copyOf(pendingEntities.values())) {
                if (tick.getType() == ForgeChunkManager.Type.ENTITY && tick.getEntity() == null) {
                    FMLLog.log.warn("Failed to load persistent chunkloading entity {} from store.", pendingEntities.inverse().get(tick));
                    loadedTickets.remove(tick.getModId(), tick);
                }
            }
            pendingEntities.clear();
            for (String modId : loadedTickets.keySet()) {
                ForgeChunkManager.LoadingCallback loadingCallback = callbacks.get(modId);
                if (loadingCallback == null) { continue; }
                int maxTicketLength = getMaxTicketLengthFor(modId);
                List<ForgeChunkManager.Ticket> tickets = loadedTickets.get(modId);
                if (loadingCallback instanceof ForgeChunkManager.OrderedLoadingCallback) {
                    ForgeChunkManager.OrderedLoadingCallback orderedLoadingCallback = (ForgeChunkManager.OrderedLoadingCallback) loadingCallback;
                    tickets = orderedLoadingCallback.ticketsLoaded(ImmutableList.copyOf(tickets), world, maxTicketLength);
                }
                if (tickets.size() > maxTicketLength) {
                    FMLLog.log.warn("The mod {} has too many open chunkloading tickets {}. Excess will be dropped", modId, tickets.size());
                    tickets.subList(maxTicketLength, tickets.size()).clear();
                }
                MixinForgeChunkManager.tickets.get(world).putAll(modId, tickets);
                loadingCallback.ticketsLoaded(ImmutableList.copyOf(tickets), world);
            }
            for (String modId : playerLoadedTickets.keySet()) {
                ForgeChunkManager.LoadingCallback loadingCallback = callbacks.get(modId);
                if (loadingCallback == null) { continue; }
                ListMultimap<String, ForgeChunkManager.Ticket> tickets = playerLoadedTickets.get(modId);
                if (loadingCallback instanceof ForgeChunkManager.PlayerOrderedLoadingCallback) {
                    ForgeChunkManager.PlayerOrderedLoadingCallback orderedLoadingCallback =
                            (ForgeChunkManager.PlayerOrderedLoadingCallback) loadingCallback;
                    tickets = orderedLoadingCallback.playerTicketsLoaded(ImmutableListMultimap.copyOf(tickets), world);
                    playerTickets.putAll(tickets);
                }
                MixinForgeChunkManager.tickets.get(world).putAll(ForgeVersion.MOD_ID, tickets.values());
                loadingCallback.ticketsLoaded(ImmutableList.copyOf(tickets.values()), world);
            }
        }
    }

    @Inject(
            method = "saveWorld",
            at = @At(value = "CONSTANT", args = "stringValue=ChunkListDepth"),
            remap = false
    )
    private static void onSaveTicket(World world, CallbackInfo ci,
                                     @Local(name = "tick") ForgeChunkManager.Ticket tick,
                                     @Local(name = "ticket") NBTTagCompound ticket) { RubicChunkManager.onSerializeTicket(ticket, tick); }
}
