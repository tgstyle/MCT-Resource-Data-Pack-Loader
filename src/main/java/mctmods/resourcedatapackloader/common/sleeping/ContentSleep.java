package mctmods.resourcedatapackloader.common.sleeping;

import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.util.entity.SpawnPoints;
import mctmods.resourcedatapackloader.core.network.BedStateMessage;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.VanillaClients;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

public final class ContentSleep {
    private static final String KIND = "sleeping";
    private static final String BED_SETS_SPAWN = "bedSetsSpawn";
    private static final String BED_SPAWN_BY_DAY = "bedSpawnByDay";
    private static final String SLEEP_SHARE = "sleepShare";
    private static final String LEAVES_OUT_BELOW = "sleepLeavesOutBelow";
    private static final String SLEEP_SAYS = "sleepSays";
    private static final String WAKE_SAYS = "wakeSays";
    private static final String MORNING_SAYS = "morningSays";
    private static final String SAYS_IN = "sleepSaysIn";
    private static final String CHAT = "chat";
    private static final String BAR = "bar";
    private static final List<String> PLACES = Arrays.asList(CHAT, BAR);
    private static final List<String> KEYS = Collections.unmodifiableList(Arrays.asList(BED_SETS_SPAWN, BED_SPAWN_BY_DAY, SLEEP_SHARE, LEAVES_OUT_BELOW, SLEEP_SAYS, WAKE_SAYS, MORNING_SAYS, SAYS_IN));
    private static final int WHOLE_SHARE = 100;
    private static final int NO_HEIGHT = Integer.MIN_VALUE;
    private static final Map<Integer, Map<UUID, String>> SLEEPERS = new HashMap<>();
    private static final Set<Integer> SKIPPED = new HashSet<>();
    private static boolean bedSetsSpawn = true;
    private static boolean bedSpawnByDay;
    private static int sleepShare = WHOLE_SHARE;
    private static int leavesOutBelow = NO_HEIGHT;
    private static String sleepSays = "";
    private static String wakeSays = "";
    private static String morningSays = "";
    private static boolean onBar;
    private static boolean pressing;

    private ContentSleep() {}

    public static void load() {
        bedSetsSpawn = true;
        bedSpawnByDay = false;
        sleepShare = WHOLE_SHARE;
        leavesOutBelow = NO_HEIGHT;
        sleepSays = "";
        wakeSays = "";
        morningSays = "";
        onBar = false;
        SLEEPERS.clear();
        SKIPPED.clear();
        Json.eachFile(PackManager.SLEEPING, "sleeping file", (key, contents) -> {
            JsonObject json = Json.object(key, contents, "sleeping file");
            if (json != null) { read("Sleeping file " + key, json); }
        });
        if (!bedSetsSpawn) {
            Summary.info(KIND, "Sleeping in a bed keeps a spawn point the player already has; the in-bed screen offers a Set spawn button");
            VanillaClients.clientDrawn("A sleeping file", BED_SETS_SPAWN);
        }
        if (bedSpawnByDay) { Summary.info(KIND, "Using a bed by day sets the spawn there unless the player sneaks" + (bedSetsSpawn ? ", which only applies with bedSetsSpawn false, so it is left out" : "")); }
        if (sleepShare != WHOLE_SHARE) { Summary.info(KIND, sleepShare + "% of the counted players asleep skip the night"); }
        if (leavesOutBelow != NO_HEIGHT) { Summary.info(KIND, "Awake players below Y " + leavesOutBelow + " are left out of the sleep count"); }
        if (!sleepSays.isEmpty()) { Summary.info(KIND, "Falling asleep says '" + sleepSays + "'"); }
        if (!wakeSays.isEmpty()) { Summary.info(KIND, "Leaving bed at night says '" + wakeSays + "'"); }
        if (!morningSays.isEmpty()) { Summary.info(KIND, "Sleeping through the night says '" + morningSays + "'"); }
        if (onBar) { Summary.info(KIND, "Sleep lines go to the action bar"); }
    }

    private static void read(String owner, JsonObject json) {
        Json.unknownKeys(json, "the top level", KEYS);
        if (json.has(BED_SETS_SPAWN)) { bedSetsSpawn = Json.getBoolean(json, BED_SETS_SPAWN, true); }
        if (json.has(BED_SPAWN_BY_DAY)) { bedSpawnByDay = Json.getBoolean(json, BED_SPAWN_BY_DAY, false); }
        if (json.has(SLEEP_SHARE)) { sleepShare = Math.round(Json.ranged(json, SLEEP_SHARE, WHOLE_SHARE, 0.0F, WHOLE_SHARE, owner)); }
        if (json.has(LEAVES_OUT_BELOW)) { leavesOutBelow = Json.getInt(json, LEAVES_OUT_BELOW, NO_HEIGHT); }
        if (json.has(SLEEP_SAYS)) { sleepSays = Json.text(json, SLEEP_SAYS, ""); }
        if (json.has(WAKE_SAYS)) { wakeSays = Json.text(json, WAKE_SAYS, ""); }
        if (json.has(MORNING_SAYS)) { morningSays = Json.text(json, MORNING_SAYS, ""); }
        if (json.has(SAYS_IN)) { onBar = BAR.equals(Json.choice(json, SAYS_IN, CHAT, owner, PLACES)); }
    }

    public static boolean vanillaShare() { return sleepShare == WHOLE_SHARE && leavesOutBelow == NO_HEIGHT; }

    public static boolean enoughAsleep(WorldServer world) {
        int counted = 0;
        int asleep = 0;
        for (EntityPlayer player : world.playerEntities) {
            if (player.isSpectator() || leftOut(player)) { continue; }
            counted++;
            if (player.isPlayerFullyAsleep()) { asleep++; }
        }
        return asleep >= Math.max(1, (int) Math.ceil(counted * sleepShare / (double) WHOLE_SHARE));
    }

    private static boolean leftOut(EntityPlayer player) { return !player.isPlayerSleeping() && player.posY < leavesOutBelow; }

    private static boolean says() { return !sleepSays.isEmpty() || !wakeSays.isEmpty() || !morningSays.isEmpty(); }

    private static boolean bed(World world, BlockPos pos, EntityPlayer player) {
        if (!world.isBlockLoaded(pos)) { return false; }
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().isBed(state, world, pos, player);
    }

    private static boolean noKeptSpawn(EntityPlayer player) { return SpawnPoints.working(player, player.world, player.getBedLocation(player.dimension), player.dimension) == null; }

    private static boolean spawnIs(EntityPlayer player, BlockPos pos) { return pos.equals(player.getBedLocation(player.dimension)); }

    private static void spawnAt(EntityPlayer player, BlockPos pos) {
        pressing = true;
        try { player.setSpawnPoint(pos, false); }
        finally { pressing = false; }
    }

    public static void setSpawnHere(EntityPlayerMP player) {
        BlockPos at = player.bedLocation;
        if (bedSetsSpawn || !player.isPlayerSleeping() || at == null || !bed(player.world, at, player)) { return; }
        spawnAt(player, at);
        if (spawnIs(player, at)) { RDPLNetwork.sendBedState(player, BedStateMessage.UPDATED); }
    }

    @SubscribeEvent public static void onSetSpawn(PlayerSetSpawnEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        BlockPos at = event.getNewSpawn();
        if (bedSetsSpawn || pressing || event.isForced() || at == null || player.world.isRemote) { return; }
        if (!bed(player.world, at, player) || spawnIs(player, at) || noKeptSpawn(player)) { return; }
        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onSleep(PlayerSleepInBedEvent event) {
        if (bedSetsSpawn || event.getResultStatus() != null || !(event.getEntityPlayer() instanceof EntityPlayerMP)) { return; }
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
        RDPLNetwork.sendBedState(player, bedState(player, event.getPos()));
    }

    private static int bedState(EntityPlayerMP player, BlockPos pos) {
        if (!bed(player.world, pos, player)) { return BedStateMessage.HIDDEN; }
        if (noKeptSpawn(player)) { return BedStateMessage.NONE; }
        return spawnIs(player, pos) ? BedStateMessage.CURRENT : BedStateMessage.OTHER;
    }

    @SubscribeEvent public static void onWakeUp(PlayerWakeUpEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player.world.isRemote) { return; }
        if (says() && !event.wakeImmediately() && !event.updateWorld() && event.shouldSetSpawn()) { SKIPPED.add(player.world.provider.getDimension()); }
        if (!bedSetsSpawn && player instanceof EntityPlayerMP) { RDPLNetwork.sendBedState((EntityPlayerMP) player, BedStateMessage.HIDDEN); }
    }

    @SubscribeEvent public static void onBedByDay(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        EntityPlayer player = event.getEntityPlayer();
        if (bedSetsSpawn || !bedSpawnByDay || world.isRemote || event.getHand() != EnumHand.MAIN_HAND || player.isSneaking() || !(player instanceof EntityPlayerMP)) { return; }
        if (!world.isDaytime() || !world.provider.canRespawnHere()) { return; }
        BlockPos at = head(world, event.getPos(), player);
        if (at == null || spawnIs(player, at)) { return; }
        spawnAt(player, at);
        if (spawnIs(player, at)) { player.sendStatusMessage(new TextComponentTranslation("rdpl.sleep.spawnSetByDay"), false); }
    }

    @Nullable private static BlockPos head(World world, BlockPos pos, EntityPlayer player) {
        if (!bed(world, pos, player)) { return null; }
        IBlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof BlockBed) || state.getValue(BlockBed.PART) == BlockBed.EnumPartType.HEAD) { return pos; }
        BlockPos head = pos.offset(state.getValue(BlockHorizontal.FACING));
        return bed(world, head, player) ? head : null;
    }

    @SubscribeEvent public static void onTick(TickEvent.WorldTickEvent event) {
        if (!says() || event.side != Side.SERVER || event.phase != TickEvent.Phase.END) { return; }
        World world = event.world;
        int dimension = world.provider.getDimension();
        Map<UUID, String> before = SLEEPERS.getOrDefault(dimension, Collections.emptyMap());
        boolean skipped = SKIPPED.remove(dimension);
        Map<UUID, String> now = sleepersIn(world);
        if (now.isEmpty()) { SLEEPERS.remove(dimension); }
        else { SLEEPERS.put(dimension, now); }
        for (Map.Entry<UUID, String> one : now.entrySet()) {
            if (!before.containsKey(one.getKey())) { say(world, sleepSays, one.getValue(), now.size()); }
        }
        if (skipped) {
            say(world, morningSays, before.isEmpty() ? "" : before.values().iterator().next(), before.size());
            return;
        }
        if (world.isDaytime()) { return; }
        for (Map.Entry<UUID, String> one : before.entrySet()) {
            if (!now.containsKey(one.getKey())) { say(world, wakeSays, one.getValue(), now.size()); }
        }
    }

    private static Map<UUID, String> sleepersIn(World world) {
        Map<UUID, String> found = Collections.emptyMap();
        for (EntityPlayer player : world.playerEntities) {
            if (!player.isPlayerSleeping()) { continue; }
            if (found.isEmpty()) { found = new HashMap<>(); }
            found.put(player.getUniqueID(), player.getName());
        }
        return found;
    }

    private static void say(World world, String line, String name, int asleep) {
        if (line.isEmpty()) { return; }
        int players = 0;
        int left = 0;
        for (EntityPlayer player : world.playerEntities) {
            if (player.isSpectator()) { continue; }
            players++;
            if (leftOut(player)) { left++; }
        }
        TextComponentTranslation said = new TextComponentTranslation(line, name, asleep, players - left, left);
        for (EntityPlayer player : world.playerEntities) { player.sendStatusMessage(said, onBar); }
    }
}
