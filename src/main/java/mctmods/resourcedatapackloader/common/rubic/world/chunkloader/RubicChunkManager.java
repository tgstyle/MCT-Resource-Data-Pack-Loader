package mctmods.resourcedatapackloader.common.rubic.world.chunkloader;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.pack.loading.ContentControl;
import mctmods.resourcedatapackloader.common.rubic.server.PlayerCubeMap;
import mctmods.resourcedatapackloader.common.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IRubicTicketInternal;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.common.util.ticks.ITicket;
import mctmods.resourcedatapackloader.common.util.world.Coords;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.ReflectionUtil;
import static mctmods.resourcedatapackloader.core.util.ReflectionUtil.cast;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.lang.invoke.MethodHandle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

@Mod.EventBusSubscriber(modid = ResourceDataPackLoader.MOD_ID) public class RubicChunkManager {
    private static final MethodHandle ticketConstructor = ReflectionUtil.constructHandle(ForgeChunkManager.Ticket.class, String.class, ForgeChunkManager.Type.class, World.class);
    private static final int MAX_FORCED_CUBES_PER_TICKET = 400;
    private static final String RUBIC_KEY = "rdplRubic";
    private static final String ENTITY_CUBE_Y_KEY = "entityCubeY";
    private static final String CHUNK_MAP_KEY = "chunkMap";
    private static final String X_KEY = "x";
    private static final String Z_KEY = "z";
    private static final String CUBES_KEY = "cubes";

    public static ForgeChunkManager.Ticket makeTicket(String str, ForgeChunkManager.Type type, World world) {
        try {
            return cast(ticketConstructor.invoke(str, type, world));
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public static void onDeserializeTicket(NBTTagCompound ticketNBT, ForgeChunkManager.Ticket ticket) {
        if (!ticketNBT.hasKey(RUBIC_KEY, Constants.NBT.TAG_COMPOUND)) { return; }
        NBTTagCompound rubicNBT = ticketNBT.getCompoundTag(RUBIC_KEY);
        int entityCubeY = rubicNBT.getInteger(ENTITY_CUBE_Y_KEY);
        Map<ChunkPos, IntSet> coordsMap = new LinkedHashMap<>();
        NBTTagList chunkMap = rubicNBT.getTagList(CHUNK_MAP_KEY, Constants.NBT.TAG_COMPOUND);
        for (NBTBase entryTagBase : chunkMap) {
            NBTTagCompound entry = (NBTTagCompound) entryTagBase;
            int x = entry.getInteger(X_KEY);
            int z = entry.getInteger(Z_KEY);
            IntSet cubes = new IntArraySet(entry.getIntArray(CUBES_KEY));
            coordsMap.put(new ChunkPos(x, z), cubes);
        }
        ((IRubicTicketInternal) ticket).rdpl$setEntityChunkY(entityCubeY);
        ((IRubicTicketInternal) ticket).rdpl$setAllForcedChunkCubes(coordsMap);
    }

    public static void onSerializeTicket(NBTTagCompound ticket, ForgeChunkManager.Ticket tick) {
        if (((IRubicTicketInternal) tick).rdpl$getAllForcedChunkCubes().isEmpty()) { return; }
        NBTTagCompound rubicNBT = new NBTTagCompound();
        rubicNBT.setInteger(ENTITY_CUBE_Y_KEY, ((IRubicTicketInternal) tick).rdpl$getEntityChunkY());
        NBTTagList chunkMap = new NBTTagList();
        ((IRubicTicketInternal) tick).rdpl$getAllForcedChunkCubes().forEach((pos, cubes) -> {
            NBTTagCompound entry = new NBTTagCompound();
            entry.setInteger(X_KEY, pos.x);
            entry.setInteger(Z_KEY, pos.z);
            entry.setIntArray(CUBES_KEY, cubes.toIntArray());
            chunkMap.appendTag(entry);
        });
        rubicNBT.setTag(CHUNK_MAP_KEY, chunkMap);
        ticket.setTag(RUBIC_KEY, rubicNBT);
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
            IntSet unwatched = new IntArraySet();
            int[] range = ContentControl.numbers(ContentControl.CHUNKS, "forcedColumnCubeRange", new int[0]);
            if (range.length == 2) {
                int lowest = Math.min(range[0], range[1]);
                int highest = Math.min(Math.max(range[0], range[1]), lowest + MAX_FORCED_CUBES_PER_TICKET - 1);
                for (int cubeY = lowest; cubeY <= highest; cubeY++) {
                    ((IRubicWorldInternal) ticket.world).rdpl$getCubeFromCubeCoords(event.getLocation().x, cubeY, event.getLocation().z).getTickets().add((ITicket) ticket);
                    unwatched.add(cubeY);
                }
            }
            ((IRubicTicketInternal) ticket).rdpl$setForcedChunkCubes(event.getLocation(), unwatched);
            ((IRubicTicketInternal) ticket).rdpl$capForcedCubes(MAX_FORCED_CUBES_PER_TICKET);
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
            ContentLog.LOGGER.warn("RubicChunkManager: Unforcing chunk with no information about forced cubes at {}", event.getLocation());
            return;
        }
        for (int cubeY : forcedCubes) {
            Cube cube = (Cube) ((IRubicWorld) world).rdpl$getCubeFromCubeCoords(event.getLocation().x, cubeY, event.getLocation().z);
            cube.getTickets().remove((ITicket) ticket);
        }
        ((IRubicTicketInternal) ticket).rdpl$clearForcedChunkCubes(event.getLocation());
    }
}