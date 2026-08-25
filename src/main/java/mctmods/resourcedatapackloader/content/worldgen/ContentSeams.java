package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.rubic.RubicWorldControl;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IMinMaxHeight;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

public final class ContentSeams {
    private ContentSeams() {}

    private static final int INSET_DOWN = 3;
    private static final int INSET_UP = 1;
    private static final int SETTLE_REACH = 32;
    private static final int LEDGE_REACH = 2;
    private static final int MATCH_REACH = 6;
    private static final long NO_COLUMN = Long.MIN_VALUE;
    private static final int RESCUE_BAND = 16;
    private static final int RESCUE_REACH = 2;
    private static final Map<World, Map<UUID, BlockPos>> STOOD = new WeakHashMap<>();
    private static final Target BELOW = new Target("worldBelow");
    private static final Target ABOVE = new Target("worldAbove");

    public static boolean enabled() { return BELOW.asked().length > 0 || ABOVE.asked().length > 0; }

    @Nullable public static Integer below(int dimension) { return BELOW.targetFor(dimension); }

    @Nullable public static Integer above(int dimension) { return ABOVE.targetFor(dimension); }

    public static boolean opensFloor(int dimension) { return BELOW.targetFor(dimension) != null && opensBedrock(); }

    public static boolean opensCeiling(int dimension) { return ABOVE.targetFor(dimension) != null && opensBedrock(); }

    private static boolean opensBedrock() { return !ContentControl.flag(ContentControl.TERRAIN, "worldSeamBedrock", Config.worldgen.worldSeamBedrock); }

    @SubscribeEvent public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.world instanceof WorldServer)) { return; }
        WorldServer world = (WorldServer) event.world;
        int dimension = world.provider.getDimension();
        Integer below = BELOW.targetFor(dimension);
        Integer above = ABOVE.targetFor(dimension);
        if (below == null && above == null) { return; }
        boolean rubic = ((IRubicWorld) world).rdpl$isRubicWorld();
        int floor = rubic ? ((IMinMaxHeight) world).rdpl$getMinHeight() : 0;
        int ceiling = RubicWorldControl.generatedCeiling(world);
        boolean carryEntities = ContentControl.flag(ContentControl.TERRAIN, "worldSeamEntities", Config.worldgen.worldSeamEntities);
        List<Entity> falling = null;
        List<Entity> rising = null;
        for (Entity entity : world.loadedEntityList) {
            if (entity.isDead || entity.isRiding()) { continue; }
            boolean player = entity instanceof EntityPlayerMP;
            if (player && ((EntityPlayerMP) entity).connection == null) { continue; }
            if (!player && !carryEntities) { continue; }
            if (player && entity.onGround && entity.posY >= floor && entity.posY <= ceiling) {
                STOOD.computeIfAbsent(world, key -> new HashMap<>()).put(entity.getUniqueID(), entity.getPosition());
            }
            if (below != null && entity.posY < floor + 1) {
                if (falling == null) { falling = new ArrayList<>(); }
                falling.add(entity);
            }
            else if (above != null && entity.posY > ceiling - 2) {
                if (rising == null) { rising = new ArrayList<>(); }
                rising.add(entity);
            }
        }
        if (falling != null) {
            for (Entity entity : falling) { carry(world, entity, below, true, floor, ceiling); }
        }
        if (rising != null) {
            for (Entity entity : rising) { carry(world, entity, above, false, floor, ceiling); }
        }
    }

    private static void carry(WorldServer world, Entity entity, int target, boolean down, int sourceFloor, int sourceCeiling) {
        if (target == world.provider.getDimension()) { return; }
        MinecraftServer server = world.getMinecraftServer();
        if (server == null) { return; }
        WorldServer destination = server.getWorld(target);
        boolean rubic = ((IRubicWorld) destination).rdpl$isRubicWorld();
        int floor = rubic ? ((IMinMaxHeight) destination).rdpl$getMinHeight() : 0;
        int ceiling = RubicWorldControl.generatedCeiling(destination);
        double arriveY = down ? ceiling - INSET_DOWN : floor + INSET_UP;
        boolean walking = entity instanceof EntityPlayerMP;
        if (walking && down) { SeamMemory.of(world).noteEntry((int) Math.floor(entity.posX), (int) Math.floor(entity.posZ)); }
        double anchorX = entity.posX;
        double anchorZ = entity.posZ;
        long remembered = NO_COLUMN;
        if (walking) {
            BlockPos entry = down ? null : SeamMemory.of(destination).entryNear(anchorX, anchorZ, MATCH_REACH);
            if (entry != null) {
                anchorX = entry.getX() + 0.5;
                anchorZ = entry.getZ() + 0.5;
            }
            remembered = SeamMemory.column((int) Math.floor(anchorX), (int) Math.floor(anchorZ));
        }
        Seam seam = new Seam(anchorX, arriveY, anchorZ, entity.motionX, entity.motionY, entity.motionZ, down, floor, ceiling, remembered);
        if (entity instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            if (!ForgeHooks.onTravelToDimension(player, target)) {
                bounce(player, down, sourceFloor, sourceCeiling);
                return;
            }
            server.getPlayerList().transferPlayerToDimension(player, target, seam);
        }
        else { entity.changeDimension(target, seam); }
    }

    private static void bounce(EntityPlayerMP player, boolean down, int floor, int ceiling) {
        boolean beyond = down ? player.posY < floor + 1 : player.posY > ceiling - 2;
        if (!beyond) { return; }
        if (down) {
            BlockPos feet = stood(player);
            if (feet == null) { feet = footing(player, floor, Math.min(ceiling - 1, floor + RESCUE_BAND)); }
            if (feet == null) { feet = player.world.getTopSolidOrLiquidBlock(player.world.getSpawnPoint()); }
            player.setPositionAndUpdate(feet.getX() + 0.5, feet.getY(), feet.getZ() + 0.5);
        }
        else { player.setPositionAndUpdate(player.posX, ceiling - INSET_DOWN, player.posZ); }
        player.motionX = 0.0;
        player.motionY = 0.0;
        player.motionZ = 0.0;
        player.fallDistance = 0.0F;
    }

    @Nullable private static BlockPos footing(EntityPlayerMP player, int floor, int highest) {
        World world = player.world;
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int x = (int) Math.floor(player.posX);
        int z = (int) Math.floor(player.posZ);
        for (int y = floor + 1; y <= highest; y++) {
            for (int reach = 0; reach <= RESCUE_REACH; reach++) {
                for (int dx = -reach; dx <= reach; dx++) {
                    for (int dz = -reach; dz <= reach; dz++) {
                        if (Math.max(Math.abs(dx), Math.abs(dz)) != reach) { continue; }
                        IBlockState below = world.getBlockState(at.setPos(x + dx, y - 1, z + dz));
                        if (below.getMaterial() == Material.AIR || below.getMaterial().isLiquid()) { continue; }
                        if (world.isAirBlock(at.setPos(x + dx, y, z + dz)) && world.isAirBlock(at.setPos(x + dx, y + 1, z + dz))) { return new BlockPos(x + dx, y, z + dz); }
                    }
                }
            }
        }
        return null;
    }

    @Nullable private static BlockPos stood(EntityPlayerMP player) {
        Map<UUID, BlockPos> known = STOOD.get(player.world);
        BlockPos held = known == null ? null : known.get(player.getUniqueID());
        if (held == null) { return null; }
        World world = player.world;
        if (world.getBlockState(held.down()).getMaterial().isSolid() && world.isAirBlock(held) && world.isAirBlock(held.up())) { return held; }
        known.remove(player.getUniqueID());
        return null;
    }

    private static void clear(World world, BlockPos at) {
        if (world.isAirBlock(at)) { return; }
        world.destroyBlock(at, true);
    }

    private static final class Seam implements ITeleporter {
        private final double x;
        private final double y;
        private final double z;
        private final double motionX;
        private final double motionY;
        private final double motionZ;
        private final boolean down;
        private final int floor;
        private final int ceiling;
        private final long remembered;

        Seam(double x, double y, double z, double motionX, double motionY, double motionZ, boolean down, int floor, int ceiling, long remembered) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.motionX = motionX;
            this.motionY = motionY;
            this.motionZ = motionZ;
            this.down = down;
            this.floor = floor;
            this.ceiling = ceiling;
            this.remembered = remembered;
        }

        @Override public void placeEntity(World world, Entity entity, float yaw) {
            BlockPos spot = known(world);
            if (spot == null) { spot = settle(world); }
            if (remembered != NO_COLUMN && world instanceof WorldServer) { SeamMemory.of((WorldServer) world).rememberLanding(remembered, spot); }
            if (entity instanceof EntityPlayerMP) { open(world, spot); }
            boolean sameColumn = spot.getX() == (int) Math.floor(x) && spot.getZ() == (int) Math.floor(z);
            double landX = sameColumn ? x : spot.getX() + 0.5;
            double landZ = sameColumn ? z : spot.getZ() + 0.5;
            entity.setLocationAndAngles(landX, spot.getY(), landZ, yaw, entity.rotationPitch);
            entity.motionX = motionX;
            entity.motionY = sameColumn ? motionY : 0.0;
            entity.motionZ = motionZ;
        }

        private void open(World world, BlockPos feet) {
            clear(world, feet);
            clear(world, feet.up());
            if (!down || feet.getY() != ceiling - INSET_DOWN) { return; }
            for (int y = feet.getY() + 2; y <= ceiling - 1; y++) { clear(world, new BlockPos(feet.getX(), y, feet.getZ())); }
        }

        private BlockPos settle(World world) {
            BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
            int blockX = (int) Math.floor(x);
            int blockZ = (int) Math.floor(z);
            int start = (int) Math.floor(y);
            int step = down ? -1 : 1;
            if (grounded(world, at, blockX, start, blockZ)) { return new BlockPos(blockX, start, blockZ); }
            for (int offset = 0; offset <= SETTLE_REACH; offset++) {
                int feet = start + offset * step;
                if (feet < floor + 1 || feet > ceiling - 1) { break; }
                BlockPos standing = nearest(world, at, blockX, feet, blockZ, offset, true);
                if (standing != null) { return standing; }
                BlockPos rim = nearest(world, at, blockX, feet, blockZ, offset, false);
                if (rim != null) { return rim; }
            }
            if (!down) { return surface(world, blockX, blockZ); }
            return new BlockPos(blockX, start, blockZ);
        }

        @Nullable private BlockPos known(World world) {
            if (remembered == NO_COLUMN || !(world instanceof WorldServer)) { return null; }
            BlockPos held = SeamMemory.of((WorldServer) world).landingFor(remembered);
            if (held == null) { return null; }
            BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
            return standable(world, at, held.getX(), held.getY(), held.getZ()) ? held : null;
        }

        @Nullable private BlockPos nearest(World world, BlockPos.MutableBlockPos at, int blockX, int feet, int blockZ, int offset, boolean open) {
            if (offset > 0 && (open ? standable(world, at, blockX, feet, blockZ) : grounded(world, at, blockX, feet, blockZ))) { return new BlockPos(blockX, feet, blockZ); }
            for (int reach = 1; reach <= LEDGE_REACH; reach++) {
                for (int dx = -reach; dx <= reach; dx++) {
                    for (int dz = -reach; dz <= reach; dz++) {
                        if (Math.max(Math.abs(dx), Math.abs(dz)) != reach) { continue; }
                        boolean fits = open ? standable(world, at, blockX + dx, feet, blockZ + dz) : grounded(world, at, blockX + dx, feet, blockZ + dz);
                        if (fits) { return new BlockPos(blockX + dx, feet, blockZ + dz); }
                    }
                }
            }
            return null;
        }

        private BlockPos surface(World world, int blockX, int blockZ) {
            BlockPos top = world.getTopSolidOrLiquidBlock(new BlockPos(blockX, 0, blockZ));
            int feet = Math.max(floor + 1, Math.min(ceiling - 1, top.getY()));
            return new BlockPos(blockX, feet, blockZ);
        }

        private boolean standable(World world, BlockPos.MutableBlockPos at, int blockX, int feet, int blockZ) {
            if (!world.isAirBlock(at.setPos(blockX, feet, blockZ))) { return false; }
            if (!world.isAirBlock(at.setPos(blockX, feet + 1, blockZ))) { return false; }
            return grounded(world, at, blockX, feet, blockZ);
        }

        private boolean grounded(World world, BlockPos.MutableBlockPos at, int blockX, int feet, int blockZ) {
            if (feet < floor + 1 || feet > ceiling - 1) { return false; }
            Material under = world.getBlockState(at.setPos(blockX, feet - 1, blockZ)).getMaterial();
            return under != Material.AIR && !under.isLiquid();
        }
    }

    private static final class Target {
        private final String key;
        private String[] raw;
        @Nullable private Integer everywhere;
        private Map<Integer, Integer> byDimension = new HashMap<>();

        Target(String key) { this.key = key; }

        String[] asked() { return ContentControl.list(ContentControl.TERRAIN, key, "worldBelow".equals(key) ? Config.worldgen.worldBelow : Config.worldgen.worldAbove); }

        @Nullable Integer targetFor(int dimension) {
            if (ContentControl.off(ContentControl.TERRAIN)) { return null; }
            String[] asked = asked();
            if (asked.length == 0) { return null; }
            if (!Arrays.equals(asked, raw)) {
                Integer bare = null;
                Map<Integer, Integer> scoped = new HashMap<>();
                for (String entry : asked) {
                    String line = entry.trim();
                    int split = line.indexOf('=');
                    String value = split < 0 ? line : line.substring(split + 1).trim();
                    int found;
                    try { found = Integer.parseInt(value); }
                    catch (NumberFormatException wrong) {
                        ContentLog.LOGGER.error("{} names '{}', which is not a dimension id, ignoring it", key, line);
                        continue;
                    }
                    if (split < 0) { bare = found; }
                    else {
                        try { scoped.put(Integer.parseInt(line.substring(0, split).trim()), found); }
                        catch (NumberFormatException wrong) { ContentLog.LOGGER.error("{} names '{}', whose dimension is not a number, ignoring it", key, line); }
                    }
                }
                everywhere = bare;
                byDimension = scoped;
                raw = asked;
            }
            Integer scoped = byDimension.get(dimension);
            return scoped != null ? scoped : everywhere;
        }
    }
}
