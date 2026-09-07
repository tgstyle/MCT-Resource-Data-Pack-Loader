package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.DimensionValues;
import mctmods.resourcedatapackloader.util.WorldgenJson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.event.TickEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.Function;
import javax.annotation.Nullable;

public final class ContentSeams {
    private static final int INSET_DOWN = 3;
    private static final int INSET_UP = 1;
    private static final int SETTLE_REACH = 32;
    private static final int LEDGE_REACH = 2;
    private static final int MATCH_REACH = 6;
    private static final long NO_COLUMN = Long.MIN_VALUE;
    private static final int RESCUE_BAND = 16;
    private static final int RESCUE_REACH = 2;
    private static final String BEDROCK_FLOOR = "bedrock_floor";
    private static final String BEDROCK_ROOF = "bedrock_roof";
    private static final Map<Level, Map<UUID, BlockPos>> STOOD = new WeakHashMap<>();
    private static final Set<ResourceLocation> MISSING = new LinkedHashSet<>();
    private static final Target BELOW = new Target("worldBelow");
    private static final Target ABOVE = new Target("worldAbove");

    private ContentSeams() {}

    public static boolean enabled() { return !BELOW.asked().isEmpty() || !ABOVE.asked().isEmpty(); }

    @Nullable public static ResourceLocation below(String dimension) { return BELOW.targetFor(dimension); }

    @Nullable public static ResourceLocation above(String dimension) { return ABOVE.targetFor(dimension); }

    public static boolean opensFloor(String dimension) { return BELOW.targetFor(dimension) != null && opensBedrock(); }

    public static boolean opensCeiling(String dimension) { return ABOVE.targetFor(dimension) != null && opensBedrock(); }

    private static boolean opensBedrock() { return !ContentControl.flag(ContentControl.TERRAIN, "worldSeamBedrock", Config.worldgen.worldSeamBedrock()); }

    public static void openBedrock(JsonObject settings, String dimension) {
        boolean floor = opensFloor(dimension);
        boolean roof = opensCeiling(dimension);
        JsonArray sequence = WorldgenJson.sequenceOf(settings);
        List<JsonElement> kept = new ArrayList<>();
        int removed = 0;
        for (JsonElement element : sequence) {
            String gradient = element.isJsonObject() ? gradientName(element.getAsJsonObject()) : null;
            if (gradient != null && ((floor && gradient.endsWith(BEDROCK_FLOOR)) || (roof && gradient.endsWith(BEDROCK_ROOF)))) {
                removed++;
                continue;
            }
            kept.add(element);
        }
        if (removed == 0) { return; }
        JsonArray rebuilt = new JsonArray();
        for (JsonElement element : kept) { rebuilt.add(element); }
        sequence.asList().clear();
        sequence.asList().addAll(rebuilt.asList());
        ContentLog.LOGGER.debug("Left the bedrock out of {} at its {} so the seam can be dug through", dimension, floor && roof ? "floor and ceiling" : floor ? "floor" : "ceiling");
    }

    @Nullable private static String gradientName(JsonObject entry) {
        if (!"minecraft:condition".equals(GsonHelper.getAsString(entry, "type", ""))) { return null; }
        JsonObject test = GsonHelper.getAsJsonObject(entry, "if_true", new JsonObject());
        if ("minecraft:not".equals(GsonHelper.getAsString(test, "type", ""))) { test = GsonHelper.getAsJsonObject(test, "invert", new JsonObject()); }
        if (!"minecraft:vertical_gradient".equals(GsonHelper.getAsString(test, "type", ""))) { return null; }
        return GsonHelper.getAsString(test, "random_name", "");
    }

    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) { return; }
        tick(level);
    }

    public static void tick(ServerLevel level) {
        String dimension = level.dimension().location().toString();
        ResourceLocation below = BELOW.targetFor(dimension);
        ResourceLocation above = ABOVE.targetFor(dimension);
        if (below == null && above == null) { return; }
        int floor = level.getMinBuildHeight();
        int ceiling = level.getMaxBuildHeight();
        boolean carryEntities = ContentControl.flag(ContentControl.TERRAIN, "worldSeamEntities", Config.worldgen.worldSeamEntities());
        List<Entity> falling = null;
        List<Entity> rising = null;
        for (Entity entity : level.getAllEntities()) {
            if (entity.isRemoved() || entity.isPassenger()) { continue; }
            boolean player = entity instanceof ServerPlayer;
            if (!player && !carryEntities) { continue; }
            if (player && entity.onGround() && entity.getY() >= floor && entity.getY() <= ceiling) {
                STOOD.computeIfAbsent(level, key -> new HashMap<>()).put(entity.getUUID(), entity.blockPosition());
            }
            if (below != null && entity.getY() < floor + 1) {
                if (falling == null) { falling = new ArrayList<>(); }
                falling.add(entity);
            }
            else if (above != null && entity.getY() > ceiling - 2) {
                if (rising == null) { rising = new ArrayList<>(); }
                rising.add(entity);
            }
        }
        if (falling != null) {
            for (Entity entity : falling) { carry(level, entity, below, true, floor, ceiling); }
        }
        if (rising != null) {
            for (Entity entity : rising) { carry(level, entity, above, false, floor, ceiling); }
        }
    }

    private static void carry(ServerLevel level, Entity entity, ResourceLocation target, boolean down, int sourceFloor, int sourceCeiling) {
        if (target.equals(level.dimension().location())) { return; }
        MinecraftServer server = level.getServer();
        ServerLevel destination = server.getLevel(ResourceKey.create(Registries.DIMENSION, target));
        if (destination == null) {
            if (MISSING.add(target)) { ContentLog.LOGGER.error("The seam of {} leads to {}, which is not a loaded dimension, so nothing crosses it", level.dimension().location(), target); }
            if (entity instanceof ServerPlayer player) { bounce(player, down, sourceFloor, sourceCeiling); }
            return;
        }
        int floor = destination.getMinBuildHeight();
        int ceiling = destination.getMaxBuildHeight();
        double arriveY = down ? ceiling - INSET_DOWN : floor + INSET_UP;
        boolean walking = entity instanceof ServerPlayer;
        if (walking && down) { SeamMemory.of(level).noteEntry(entity.getBlockX(), entity.getBlockZ()); }
        double anchorX = entity.getX();
        double anchorZ = entity.getZ();
        long remembered = NO_COLUMN;
        if (walking) {
            BlockPos entry = down ? null : SeamMemory.of(destination).entryNear(anchorX, anchorZ, MATCH_REACH);
            if (entry != null) {
                anchorX = entry.getX() + 0.5D;
                anchorZ = entry.getZ() + 0.5D;
            }
            remembered = SeamMemory.column((int) Math.floor(anchorX), (int) Math.floor(anchorZ));
        }
        Vec3 motion = entity.getDeltaMovement();
        Seam seam = new Seam(anchorX, arriveY, anchorZ, motion, down, floor, ceiling, remembered);
        Entity moved = entity.changeDimension(destination, seam);
        if (moved == null && entity instanceof ServerPlayer player) { bounce(player, down, sourceFloor, sourceCeiling); }
        else if (moved != null) { ContentLog.LOGGER.debug("The seam of {} carried {} {} into {} at {}, {}, {}", level.dimension().location(), moved.getType(), down ? "down" : "up", target, moved.getBlockX(), moved.getBlockY(), moved.getBlockZ()); }
    }

    private static void bounce(ServerPlayer player, boolean down, int floor, int ceiling) {
        boolean beyond = down ? player.getY() < floor + 1 : player.getY() > ceiling - 2;
        if (!beyond) { return; }
        if (down) {
            BlockPos feet = stood(player);
            if (feet == null) { feet = footing(player, floor, Math.min(ceiling - 1, floor + RESCUE_BAND)); }
            if (feet == null) { feet = player.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, player.level().getSharedSpawnPos()); }
            player.teleportTo(feet.getX() + 0.5D, feet.getY(), feet.getZ() + 0.5D);
        }
        else { player.teleportTo(player.getX(), ceiling - INSET_DOWN, player.getZ()); }
        player.setDeltaMovement(Vec3.ZERO);
        player.fallDistance = 0.0F;
    }

    @Nullable private static BlockPos footing(ServerPlayer player, int floor, int highest) {
        Level level = player.level();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int x = player.getBlockX();
        int z = player.getBlockZ();
        for (int y = floor + 1; y <= highest; y++) {
            for (int reach = 0; reach <= RESCUE_REACH; reach++) {
                for (int dx = -reach; dx <= reach; dx++) {
                    for (int dz = -reach; dz <= reach; dz++) {
                        if (Math.max(Math.abs(dx), Math.abs(dz)) != reach) { continue; }
                        BlockState below = level.getBlockState(at.set(x + dx, y - 1, z + dz));
                        if (below.isAir() || !below.getFluidState().isEmpty()) { continue; }
                        if (level.isEmptyBlock(at.set(x + dx, y, z + dz)) && level.isEmptyBlock(at.set(x + dx, y + 1, z + dz))) { return new BlockPos(x + dx, y, z + dz); }
                    }
                }
            }
        }
        return null;
    }

    @Nullable private static BlockPos stood(ServerPlayer player) {
        Map<UUID, BlockPos> known = STOOD.get(player.level());
        BlockPos held = known == null ? null : known.get(player.getUUID());
        if (held == null) { return null; }
        Level level = player.level();
        if (level.getBlockState(held.below()).isFaceSturdy(level, held.below(), Direction.UP) && level.isEmptyBlock(held) && level.isEmptyBlock(held.above())) { return held; }
        known.remove(player.getUUID());
        return null;
    }

    private static void clear(ServerLevel level, BlockPos at) {
        if (level.isEmptyBlock(at)) { return; }
        level.destroyBlock(at, true);
    }

    public record Landing(Vec3 pos, Vec3 speed) {}

    public static final class Seam implements ITeleporter {
        private final double x;
        private final double y;
        private final double z;
        private final Vec3 motion;
        private final boolean down;
        private final int floor;
        private final int ceiling;
        private final long remembered;

        Seam(double x, double y, double z, Vec3 motion, boolean down, int floor, int ceiling, long remembered) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.motion = motion;
            this.down = down;
            this.floor = floor;
            this.ceiling = ceiling;
            this.remembered = remembered;
        }

        @Override public PortalInfo getPortalInfo(Entity entity, ServerLevel destination, Function<ServerLevel, PortalInfo> fallback) {
            Landing landing = land(destination, entity);
            return new PortalInfo(landing.pos(), landing.speed(), entity.getYRot(), entity.getXRot());
        }

        @Override public boolean playTeleportSound(ServerPlayer player, ServerLevel source, ServerLevel destination) { return false; }

        public Landing land(ServerLevel level, Entity entity) {
            BlockPos spot = known(level);
            if (spot == null) { spot = settle(level); }
            if (remembered != NO_COLUMN) { SeamMemory.of(level).rememberLanding(remembered, spot); }
            if (entity instanceof ServerPlayer) { open(level, spot); }
            boolean sameColumn = spot.getX() == (int) Math.floor(x) && spot.getZ() == (int) Math.floor(z);
            double landX = sameColumn ? x : spot.getX() + 0.5D;
            double landZ = sameColumn ? z : spot.getZ() + 0.5D;
            return new Landing(new Vec3(landX, spot.getY(), landZ), new Vec3(motion.x, sameColumn ? motion.y : 0.0D, motion.z));
        }

        private void open(ServerLevel level, BlockPos feet) {
            clear(level, feet);
            clear(level, feet.above());
            if (!down || feet.getY() != ceiling - INSET_DOWN) { return; }
            for (int y = feet.getY() + 2; y <= ceiling - 1; y++) { clear(level, new BlockPos(feet.getX(), y, feet.getZ())); }
        }

        private BlockPos settle(ServerLevel level) {
            BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
            int blockX = (int) Math.floor(x);
            int blockZ = (int) Math.floor(z);
            int start = (int) Math.floor(y);
            int step = down ? -1 : 1;
            if (grounded(level, at, blockX, start, blockZ)) { return new BlockPos(blockX, start, blockZ); }
            for (int offset = 0; offset <= SETTLE_REACH; offset++) {
                int feet = start + offset * step;
                if (feet < floor + 1 || feet > ceiling - 1) { break; }
                BlockPos standing = nearest(level, at, blockX, feet, blockZ, offset, true);
                if (standing != null) { return standing; }
                BlockPos rim = nearest(level, at, blockX, feet, blockZ, offset, false);
                if (rim != null) { return rim; }
            }
            if (!down) { return surface(level, blockX, blockZ); }
            return new BlockPos(blockX, start, blockZ);
        }

        @Nullable private BlockPos known(ServerLevel level) {
            if (remembered == NO_COLUMN) { return null; }
            BlockPos held = SeamMemory.of(level).landingFor(remembered);
            if (held == null) { return null; }
            BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
            return standable(level, at, held.getX(), held.getY(), held.getZ()) ? held : null;
        }

        @Nullable private BlockPos nearest(ServerLevel level, BlockPos.MutableBlockPos at, int blockX, int feet, int blockZ, int offset, boolean open) {
            if (offset > 0 && (open ? standable(level, at, blockX, feet, blockZ) : grounded(level, at, blockX, feet, blockZ))) { return new BlockPos(blockX, feet, blockZ); }
            for (int reach = 1; reach <= LEDGE_REACH; reach++) {
                for (int dx = -reach; dx <= reach; dx++) {
                    for (int dz = -reach; dz <= reach; dz++) {
                        if (Math.max(Math.abs(dx), Math.abs(dz)) != reach) { continue; }
                        boolean fits = open ? standable(level, at, blockX + dx, feet, blockZ + dz) : grounded(level, at, blockX + dx, feet, blockZ + dz);
                        if (fits) { return new BlockPos(blockX + dx, feet, blockZ + dz); }
                    }
                }
            }
            return null;
        }

        private BlockPos surface(ServerLevel level, int blockX, int blockZ) {
            BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(blockX, 0, blockZ));
            int feet = Mth.clamp(top.getY(), floor + 1, ceiling - 1);
            return new BlockPos(blockX, feet, blockZ);
        }

        private boolean standable(ServerLevel level, BlockPos.MutableBlockPos at, int blockX, int feet, int blockZ) {
            if (!level.isEmptyBlock(at.set(blockX, feet, blockZ))) { return false; }
            if (!level.isEmptyBlock(at.set(blockX, feet + 1, blockZ))) { return false; }
            return grounded(level, at, blockX, feet, blockZ);
        }

        private boolean grounded(ServerLevel level, BlockPos.MutableBlockPos at, int blockX, int feet, int blockZ) {
            if (feet < floor + 1 || feet > ceiling - 1) { return false; }
            BlockState under = level.getBlockState(at.set(blockX, feet - 1, blockZ));
            return !under.isAir() && under.getFluidState().isEmpty();
        }
    }

    private static final class Target {
        private final String key;
        private final DimensionValues<ResourceLocation> values;

        Target(String key) {
            this.key = key;
            this.values = new DimensionValues<>(key, Target::dimension, "which is not a dimension id");
        }

        List<String> asked() {
            if (ContentControl.off(ContentControl.TERRAIN)) { return List.of(); }
            return ContentControl.list(ContentControl.TERRAIN, key, "worldBelow".equals(key) ? Config.worldgen.worldBelow() : Config.worldgen.worldAbove());
        }

        @Nullable ResourceLocation targetFor(String dimension) { return values.at(dimension, asked()); }

        @Nullable private static ResourceLocation dimension(String value) { return ResourceLocation.tryParse(ContentFormats.dimensionId(value)); }
    }
}
