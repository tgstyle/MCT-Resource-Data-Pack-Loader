package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.content.rubic.server.CubeProviderServer;
import mctmods.resourcedatapackloader.content.rubic.server.PlayerCubeMap;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.interfaces.ITicket;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ThutRubicLifts {
    private static final ResourceLocation LIFT = new ResourceLocation("thuttech", "lift");
    private static final int MAINTAIN_EVERY = 20;
    private static final int DISCOVER_EVERY = 200;
    private static final int GIVE_UP_AFTER = 5;
    private static final Map<World, Map<UUID, Pin>> PINS = new WeakHashMap<>();

    private ThutRubicLifts() {}

    public static void register() {
        if (!Loader.isModLoaded("thuttech")) { return; }
        MinecraftForge.EVENT_BUS.register(new Handler());
        ContentLog.LOGGER.info("Thut's Elevators finds its lifts in the loaded entity list, which vanilla fills a whole column at a time, so on rubic worlds a lift's cube is kept loaded while a player watches its column");
    }

    public static final class Handler {
        @SubscribeEvent public void onJoin(EntityJoinWorldEvent event) {
            World world = event.getWorld();
            if (world.isRemote || !(world instanceof WorldServer)) { return; }
            if (!((IRubicWorld) world).rdpl$isRubicWorld()) { return; }
            Entity entity = event.getEntity();
            if (!LIFT.equals(EntityList.getKey(entity))) { return; }
            LiftPositions saved = LiftPositions.of((WorldServer) world);
            saved.positions.put(entity.getUniqueID(), entity.getPosition());
            saved.markDirty();
        }

        @SubscribeEvent public void onWorldTick(TickEvent.WorldTickEvent event) {
            if (event.phase != TickEvent.Phase.END || !(event.world instanceof WorldServer)) { return; }
            if (!((IRubicWorld) event.world).rdpl$isRubicWorld()) { return; }
            WorldServer world = (WorldServer) event.world;
            long time = world.getTotalWorldTime();
            if (time % DISCOVER_EVERY == 0) { discover(world); }
            if (time % MAINTAIN_EVERY == 0) { maintain(world); }
        }

        @SubscribeEvent public void onWorldUnload(WorldEvent.Unload event) { PINS.remove(event.getWorld()); }
    }

    private static void discover(WorldServer world) {
        LiftPositions saved = LiftPositions.of(world);
        for (Entity entity : world.loadedEntityList) {
            if (entity.isDead || !LIFT.equals(EntityList.getKey(entity))) { continue; }
            if (saved.positions.containsKey(entity.getUniqueID())) { continue; }
            saved.positions.put(entity.getUniqueID(), entity.getPosition());
            saved.markDirty();
        }
    }

    private static void maintain(WorldServer world) {
        LiftPositions saved = LiftPositions.of(world);
        if (saved.positions.isEmpty()) { return; }
        PlayerCubeMap watchers = (PlayerCubeMap) world.getPlayerChunkMap();
        Map<UUID, Pin> pins = PINS.computeIfAbsent(world, key -> new HashMap<>());
        Iterator<Map.Entry<UUID, BlockPos>> entries = saved.positions.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<UUID, BlockPos> entry = entries.next();
            UUID id = entry.getKey();
            Entity lift = world.getEntityFromUuid(id);
            if (lift != null && lift.isDead) {
                unpin(world, pins.remove(id));
                entries.remove();
                saved.markDirty();
                continue;
            }
            BlockPos at = lift != null ? lift.getPosition() : entry.getValue();
            if (lift != null && !at.equals(entry.getValue())) {
                entry.setValue(at);
                saved.markDirty();
            }
            if (watchers.getEntry(at.getX() >> 4, at.getZ() >> 4) == null) {
                unpin(world, pins.remove(id));
                continue;
            }
            if (lift == null) {
                ((IRubicWorldInternal) world).rdpl$getCubeFromCubeCoords(at.getX() >> 4, at.getY() >> 4, at.getZ() >> 4);
                lift = world.getEntityFromUuid(id);
                if (lift == null) {
                    Pin pin = pins.computeIfAbsent(id, key -> new Pin());
                    if (++pin.misses >= GIVE_UP_AFTER) {
                        unpin(world, pins.remove(id));
                        entries.remove();
                        saved.markDirty();
                    }
                    continue;
                }
                at = lift.getPosition();
                entry.setValue(at);
                saved.markDirty();
            }
            pinAt(world, pins.computeIfAbsent(id, key -> new Pin()), at);
        }
    }

    private static void pinAt(WorldServer world, Pin pin, BlockPos at) {
        int cubeX = at.getX() >> 4;
        int cubeY = at.getY() >> 4;
        int cubeZ = at.getZ() >> 4;
        pin.misses = 0;
        if (pin.pinned && pin.cubeX == cubeX && pin.cubeY == cubeY && pin.cubeZ == cubeZ) { return; }
        release(world, pin);
        Cube cube = ((IRubicWorldInternal) world).rdpl$getCubeFromCubeCoords(cubeX, cubeY, cubeZ);
        cube.getTickets().add(pin.ticket);
        pin.pinned = true;
        pin.cubeX = cubeX;
        pin.cubeY = cubeY;
        pin.cubeZ = cubeZ;
    }

    private static void unpin(WorldServer world, @Nullable Pin pin) {
        if (pin != null) { release(world, pin); }
    }

    private static void release(WorldServer world, Pin pin) {
        if (!pin.pinned) { return; }
        Cube cube = ((CubeProviderServer) world.getChunkProvider()).getLoadedCube(pin.cubeX, pin.cubeY, pin.cubeZ);
        if (cube != null) { cube.getTickets().remove(pin.ticket); }
        pin.pinned = false;
    }

    private static final class Pin {
        final LiftTicket ticket = new LiftTicket();
        boolean pinned;
        int cubeX;
        int cubeY;
        int cubeZ;
        int misses;
    }

    private static final class LiftTicket implements ITicket {
        @Override public boolean shouldTick() { return true; }
    }

    public static final class LiftPositions extends WorldSavedData {
        private static final String ID = "rdpl_thuttech_lifts";
        final Map<UUID, BlockPos> positions = new HashMap<>();

        public LiftPositions(String name) { super(name); }

        static LiftPositions of(WorldServer world) {
            LiftPositions data = (LiftPositions) world.getPerWorldStorage().getOrLoadData(LiftPositions.class, ID);
            if (data == null) {
                data = new LiftPositions(ID);
                world.getPerWorldStorage().setData(ID, data);
            }
            return data;
        }

        @Override public void readFromNBT(NBTTagCompound nbt) {
            positions.clear();
            NBTTagList list = nbt.getTagList("lifts", 10);
            for (int index = 0; index < list.tagCount(); index++) {
                NBTTagCompound entry = list.getCompoundTagAt(index);
                positions.put(new UUID(entry.getLong("most"), entry.getLong("least")), BlockPos.fromLong(entry.getLong("pos")));
            }
        }

        @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt) {
            NBTTagList list = new NBTTagList();
            for (Map.Entry<UUID, BlockPos> entry : positions.entrySet()) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setLong("most", entry.getKey().getMostSignificantBits());
                tag.setLong("least", entry.getKey().getLeastSignificantBits());
                tag.setLong("pos", entry.getValue().toLong());
                list.appendTag(tag);
            }
            nbt.setTag("lifts", list);
            return nbt;
        }
    }
}
