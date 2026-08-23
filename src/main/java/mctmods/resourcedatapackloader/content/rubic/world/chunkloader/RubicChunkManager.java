package mctmods.resourcedatapackloader.content.rubic.world.chunkloader;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.content.rubic.server.PlayerCubeMap;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicTicketInternal;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.util.Coords;
import mctmods.resourcedatapackloader.util.ReflectionUtil;
import mctmods.resourcedatapackloader.util.interfaces.ITicket;
import static mctmods.resourcedatapackloader.util.ReflectionUtil.cast;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.lang.invoke.MethodHandle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Rubic.MODID) public class RubicChunkManager {
    private static final MethodHandle ticketConstructor = ReflectionUtil.constructHandle(ForgeChunkManager.Ticket.class, String.class, ForgeChunkManager.Type.class, World.class);
    private static final int MAX_FORCED_CUBES_PER_TICKET = 400;

    public static ForgeChunkManager.Ticket makeTicket(String str, ForgeChunkManager.Type type, World world) {
        try {
            return cast(ticketConstructor.invoke(str, type, world));
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public static void onDeserializeTicket(NBTTagCompound ticketNBT, ForgeChunkManager.Ticket ticket) {
        if (!ticketNBT.hasKey("rdplRubic", Constants.NBT.TAG_COMPOUND)) { return; }
        NBTTagCompound rubicNBT = ticketNBT.getCompoundTag("rdplRubic");
        int entityCubeY = rubicNBT.getInteger("entityCubeY");
        Map<ChunkPos, IntSet> coordsMap = new LinkedHashMap<>();
        NBTTagList chunkMap = rubicNBT.getTagList("chunkMap", Constants.NBT.TAG_COMPOUND);
        for (NBTBase entryTagBase : chunkMap) {
            NBTTagCompound entry = (NBTTagCompound) entryTagBase;
            int x = entry.getInteger("x");
            int z = entry.getInteger("z");
            IntSet cubes = new IntArraySet(entry.getIntArray("cubes"));
            coordsMap.put(new ChunkPos(x, z), cubes);
        }
        ((IRubicTicketInternal) ticket).rdpl$setEntityChunkY(entityCubeY);
        ((IRubicTicketInternal) ticket).rdpl$setAllForcedChunkCubes(coordsMap);
    }

    public static void onSerializeTicket(NBTTagCompound ticket, ForgeChunkManager.Ticket tick) {
        if (((IRubicTicketInternal) tick).rdpl$getAllForcedChunkCubes().isEmpty()) { return; }
        NBTTagCompound rubicNBT = new NBTTagCompound();
        rubicNBT.setInteger("entityCubeY", ((IRubicTicketInternal) tick).rdpl$getEntityChunkY());
        NBTTagList chunkMap = new NBTTagList();
        ((IRubicTicketInternal) tick).rdpl$getAllForcedChunkCubes().forEach((pos, cubes) -> {
            NBTTagCompound entry = new NBTTagCompound();
            entry.setInteger("x", pos.x);
            entry.setInteger("z", pos.z);
            entry.setIntArray("cubes", cubes.toIntArray());
            chunkMap.appendTag(entry);
        });
        rubicNBT.setTag("chunkMap", chunkMap);
        ticket.setTag("rdplRubic", rubicNBT);
    }

    public static void onLoadEntityTicketChunk(World world, ForgeChunkManager.Ticket tick) {
        if (((IRubicWorld) world).rdpl$isRubicWorld()) {
            IRubicTicketInternal ticket = (IRubicTicketInternal) tick;
            ((IRubicWorld) world).rdpl$getCubeFromCubeCoords(ticket.getEntityChunkX(), ticket.rdpl$getEntityChunkY(), ticket.getEntityChunkZ());
        }
    }

    @SubscribeEvent public static void onForgeChunkManagerForceChunk(ForgeChunkManager.ForceChunkEvent event) {
        ForgeChunkManager.Ticket ticket = event.getTicket();
        World worldInstance = ticket.world;
        if (!((IRubicWorld) worldInstance).rdpl$isRubicWorld() || !(worldInstance instanceof WorldServer)) { return; }
        addForcedCubesHeuristic(event, ticket, (WorldServer) worldInstance);
    }

    private static void addForcedCubesHeuristic(ForgeChunkManager.ForceChunkEvent event, ForgeChunkManager.Ticket ticket, WorldServer worldInstance) {
        IntSet yCoords = ((IRubicTicketInternal) ticket).rdpl$getAllForcedChunkCubes().get(event.getLocation());
        if (yCoords != null && !yCoords.isEmpty()) {
            yCoords.forEach(cubeY ->
                    ((IRubicWorldInternal) ticket.world)
                            .rdpl$getCubeFromCubeCoords(event.getLocation().x, cubeY, event.getLocation().z)
                            .getTickets().add((ITicket) ticket)
            );
            return;
        }
        PlayerCubeMap cubeMap = (PlayerCubeMap) worldInstance.getPlayerChunkMap();
        PlayerChunkMapEntry columnWatcher = cubeMap.getEntry(event.getLocation().x, event.getLocation().z);
        if (columnWatcher == null) {
            ((IRubicTicketInternal) ticket).rdpl$setForcedChunkCubes(event.getLocation(), new IntArraySet());
            return;
        }
        List<EntityPlayerMP> players = columnWatcher.getWatchingPlayers();
        int verticalViewDistance = ContentControl.number(ContentControl.CHUNKS, "verticalCubeLoadDistance", 8);
        if (yCoords == null) { yCoords = new IntArraySet(players.size() * verticalViewDistance * 3); }
        for (EntityPlayerMP player : players) {
            for (int dy = -verticalViewDistance; dy <= verticalViewDistance; dy++) {
                int cubeY = Coords.getCubeYForEntity(player) + dy;
                Cube cube = (Cube) ((IRubicWorld) worldInstance).rdpl$getCubeFromCubeCoords(event.getLocation().x, cubeY, event.getLocation().z);
                cube.getTickets().add((ITicket) ticket);
                yCoords.add(cubeY);
            }
        }
        ((IRubicTicketInternal) ticket).rdpl$setForcedChunkCubes(event.getLocation(), yCoords);
        ((IRubicTicketInternal) ticket).rdpl$capForcedCubes(MAX_FORCED_CUBES_PER_TICKET);
    }

    @SubscribeEvent public static void onForgeChunkManagerUnforceChunk(ForgeChunkManager.UnforceChunkEvent event) {
        ForgeChunkManager.Ticket ticket = event.getTicket();
        World world = ticket.world;
        if (!((IRubicWorld) world).rdpl$isRubicWorld()) { return; }
        IntSet forcedCubes = ((IRubicTicketInternal) ticket).rdpl$getAllForcedChunkCubes().get(event.getLocation());
        if (forcedCubes == null) {
            Rubic.LOGGER.warn("RubicChunkManager: Unforcing chunk with no information about forced cubes at {}", event.getLocation());
            return;
        }
        for (int cubeY : forcedCubes) {
            Cube cube = (Cube) ((IRubicWorld) world).rdpl$getCubeFromCubeCoords(event.getLocation().x, cubeY, event.getLocation().z);
            cube.getTickets().remove((ITicket) ticket);
        }
        ((IRubicTicketInternal) ticket).rdpl$clearForcedChunkCubes(event.getLocation());
    }
}