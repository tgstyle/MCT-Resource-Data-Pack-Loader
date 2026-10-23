package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.def.OrderDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Enums;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.TileScan;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public final class ContentOrders {
    public static final String PLOT_MARK = "rdplPlotOrder";
    private static final String BOSS = "rdplBoss";
    private static final String TEAM = "team:";
    private static final String TAG = "tag:";
    private static final int SIGN_WAIT = 2400;
    private static final int CHECK = 20;
    private static final Map<String, OrderDef> DEFS = new LinkedHashMap<>();
    private static final Map<Integer, Map<BlockPos, Pending>> PENDING = new HashMap<>();
    private static String stockName = "Stock";
    private static String hire = "";
    private static int spawnCap = 1;
    private static boolean anyGrouped;
    private static ItemStack hireStack = ItemStack.EMPTY;

    private ContentOrders() {}

    public static boolean load() {
        DEFS.clear();
        OrderItems.forget();
        anyGrouped = false;
        stockName = "Stock";
        hire = "";
        spawnCap = 1;
        hireStack = ItemStack.EMPTY;
        Json.eachFile(PackManager.ORDERS, "order file", ContentOrders::read);
        if (!hire.isEmpty()) { hireStack = ContentStacks.parse(new ResourceLocation("rdpl", PackManager.ORDERS), hire, 1); }
        if (!DEFS.isEmpty()) { Summary.info("orders", "Loaded " + DEFS.size() + " work order(s)"); }
        return !DEFS.isEmpty();
    }

    private static void read(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "order file");
        if (json == null) { return; }
        if (json.has("stockName")) { stockName = Json.getString(json, "stockName").trim(); }
        if (json.has("hire")) { hire = Json.getString(json, "hire").trim(); }
        if (json.has("spawnCap")) { spawnCap = Math.max(0, Json.getInt(json, "spawnCap")); }
        if (!json.has("orders")) { return; }
        JsonArray list = Json.getJsonArray(json, "orders");
        for (int i = 0; i < list.size(); i++) {
            String name = key + "#" + i;
            OrderDef def = list.get(i).isJsonObject() ? order(name, list.get(i).getAsJsonObject()) : null;
            if (def == null) { continue; }
            DEFS.put(name, def);
            for (String taker : def.takers) { anyGrouped |= grouped(taker); }
        }
    }

    @Nullable private static OrderDef order(String name, JsonObject json) {
        String named = Json.getString(json, "job", "");
        OrderDef.Job job = Enums.byName(OrderDef.Job.class, named);
        if (job == null) {
            ContentLog.LOGGER.error("Order {} asks for the job '{}', which is not gather, mine, farm or haul, ignoring it", name, named);
            return null;
        }
        String blocks = Json.getString(json, "blocks", "").trim();
        if (blocks.isEmpty() && job != OrderDef.Job.FARM) {
            ContentLog.LOGGER.error("Order {} names no ore dictionary name under blocks, and only a farm order works without one, ignoring it", name);
            return null;
        }
        List<String> takers = Json.strings(json, "takers");
        if (takers.isEmpty()) {
            ContentLog.LOGGER.error("Order {} names no takers, so no worker could ever take it, ignoring it", name);
            return null;
        }
        String deliver = Json.getString(json, "deliver", "chest");
        if (!"chest".equalsIgnoreCase(deliver) && !"self".equalsIgnoreCase(deliver)) { ContentLog.LOGGER.error("Order {} delivers to '{}', which is not chest or self, so it delivers to the chest", name, deliver); }
        int[] byTier = new int[0];
        if (json.has("areaByTier")) {
            JsonArray tiers = Json.getJsonArray(json, "areaByTier");
            byTier = new int[tiers.size()];
            for (int t = 0; t < tiers.size(); t++) { byTier[t] = Math.max(1, Json.asInt(tiers.get(t), "tiers", 1)); }
        }
        return new OrderDef(name, job, blocks, Math.max(1, Json.getInt(json, "area", 16)), byTier, "self".equalsIgnoreCase(deliver), Math.max(1, Json.getInt(json, "limit", 64)),
                Math.max(0, Json.getInt(json, "standing", 0)), Math.max(1, Json.getInt(json, "workers", 1)), Json.getInt(json, "priority", 0), takers,
                Json.getString(json, "sign", "").trim(), Math.max(0.01F, Json.getFloat(json, "speed", 1.0F)), Json.getString(json, "tool", "").trim(),
                Json.getString(json, "wins", "").trim(), Json.getInt(json, "points", 0), Json.getString(json, "loot", "").trim());
    }

    @Nullable static OrderDef def(String name) { return DEFS.get(name); }

    static int spawnCap() { return spawnCap; }

    static boolean grouped(String taker) {
        String lower = taker.toLowerCase(Locale.ROOT);
        return lower.startsWith(TEAM) || lower.startsWith(TAG);
    }

    public static boolean wants(EntityVariantDef variant) {
        if (DEFS.isEmpty()) { return false; }
        if (anyGrouped) { return true; }
        for (OrderDef def : DEFS.values()) {
            for (String taker : def.takers) {
                if (taker.equalsIgnoreCase(variant.registryName.toString())) { return true; }
            }
        }
        return false;
    }

    private static boolean takes(OrderDef def, EntityLiving mob) {
        EntityVariantDef variant = ContentEntities.BY_CLASS.get(mob.getClass());
        Team side = mob.getTeam();
        for (String taker : def.takers) {
            String lower = taker.toLowerCase(Locale.ROOT);
            if (lower.startsWith(TEAM)) {
                if (side != null && side.getName().equalsIgnoreCase(taker.substring(TEAM.length()))) { return true; }
                continue;
            }
            if (lower.startsWith(TAG)) {
                for (String tag : mob.getTags()) {
                    if (tag.equalsIgnoreCase(taker.substring(TAG.length()))) { return true; }
                }
                continue;
            }
            if (variant != null && taker.equalsIgnoreCase(variant.registryName.toString())) { return true; }
        }
        return false;
    }

    static boolean serves(EntityLiving mob, WorkOrder order) {
        if (!takes(order.def, mob)) { return false; }
        Team side = mob.getTeam();
        if (!order.team.isEmpty()) { return side != null && side.getName().equals(order.team); }
        UUID boss = boss(mob);
        if (boss != null) { return boss.equals(order.owner); }
        return side == null;
    }

    @Nullable static UUID boss(EntityLiving mob) {
        String kept = mob.getEntityData().getString(BOSS);
        if (kept.isEmpty()) { return null; }
        try { return UUID.fromString(kept); }
        catch (IllegalArgumentException ex) { return null; }
    }

    static void bind(EntityLiving mob, UUID player) {
        mob.getEntityData().setString(BOSS, player.toString());
        mob.enablePersistence();
    }

    private static boolean free(EntityLiving mob) { return boss(mob) == null && mob.getTeam() == null; }

    static boolean isOrderChest(World world, BlockPos pos) { return OrderBoard.of(world).hasChest(pos); }

    static boolean isChest(@Nullable TileEntity tile) { return tile instanceof TileEntityLockableLoot && OrderItems.handler(tile.getWorld(), tile.getPos()) != null; }

    @SubscribeEvent public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote) { return; }
        OrderBoard.of(event.world).tick(event.world);
        if (event.world.getTotalWorldTime() % CHECK == 0) { signs(event.world); }
    }

    @SubscribeEvent public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        World world = event.getWorld();
        if (world.isRemote || !(event.getEntity() instanceof EntityPlayer)) { return; }
        EntityPlayer player = (EntityPlayer) event.getEntity();
        BlockPos pos = event.getPos();
        if (event.getPlacedBlock().getBlock() == Blocks.STANDING_SIGN || event.getPlacedBlock().getBlock() == Blocks.WALL_SIGN) {
            PENDING.computeIfAbsent(world.provider.getDimension(), dimension -> new HashMap<>()).put(pos.toImmutable(), new Pending(player.getUniqueID(), world.getTotalWorldTime() + SIGN_WAIT));
            return;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (stockName.isEmpty() || !isChest(tile) || !((TileEntityLockableLoot) tile).hasCustomName() || !stockName.equalsIgnoreCase(((TileEntityLockableLoot) tile).getName().trim())) { return; }
        OrderDef haul = null;
        for (OrderDef def : DEFS.values()) {
            if (def.job == OrderDef.Job.HAUL) {
                haul = def;
                break;
            }
        }
        if (haul == null) {
            ContentLog.LOGGER.error("{} placed a chest named {} at {}, {}, {}, but no pack has a haul order, so nothing keeps it stocked", player.getName(), stockName, pos.getX(), pos.getY(), pos.getZ());
            return;
        }
        OrderBoard.of(world).open(world, haul, pos, null, player, true);
    }

    @SubscribeEvent public static void onBreak(BlockEvent.BreakEvent event) { if (!event.getWorld().isRemote) { OrderBoard.of(event.getWorld()).cancelAt(event.getPos()); } }

    public static void plotSign(World world, BlockPos pos) {
        if (DEFS.isEmpty()) { return; }
        PENDING.computeIfAbsent(world.provider.getDimension(), dimension -> new HashMap<>()).put(pos.toImmutable(), new Pending(null, world.getTotalWorldTime() + SIGN_WAIT));
    }

    @SubscribeEvent public static void onChunkLoad(ChunkEvent.Load event) { plotSigns(event.getWorld(), event.getChunk().getTileEntityMap().values()); }

    public static void plotSigns(World world, Collection<TileEntity> tiles) {
        if (world.isRemote) { return; }
        for (TileEntity tile : new ArrayList<>(tiles)) {
            if (tile instanceof TileEntitySign && tile.getTileData().getBoolean(PLOT_MARK)) { plotSign(world, tile.getPos()); }
        }
    }

    private static void clearMark(TileEntity tile) {
        if (!tile.getTileData().hasKey(PLOT_MARK)) { return; }
        tile.getTileData().removeTag(PLOT_MARK);
        tile.markDirty();
    }

    private static void signs(World world) {
        Map<BlockPos, Pending> waiting = PENDING.get(world.provider.getDimension());
        if (waiting == null || waiting.isEmpty()) { return; }
        long now = world.getTotalWorldTime();
        for (BlockPos pos : new ArrayList<>(waiting.keySet())) {
            Pending pending = waiting.get(pos);
            if (pending == null) { continue; }
            if (now > pending.until || !world.isBlockLoaded(pos)) {
                waiting.remove(pos);
                continue;
            }
            TileEntity tile = world.getTileEntity(pos);
            if (!(tile instanceof TileEntitySign)) {
                waiting.remove(pos);
                continue;
            }
            String word = ((TileEntitySign) tile).signText[0].getUnformattedText().trim();
            if (word.isEmpty()) { continue; }
            waiting.remove(pos);
            OrderDef def = bySign(word);
            if (pending.player == null) {
                plotOrder(world, tile, def, word);
                continue;
            }
            EntityPlayer by = world.getPlayerEntityByUUID(pending.player);
            BlockPos chest = def == null || by == null ? null : beside(world, pos);
            if (chest != null) { OrderBoard.of(world).open(world, def, chest, pos, by, false); }
        }
    }

    private static void plotOrder(World world, TileEntity sign, @Nullable OrderDef def, String word) {
        BlockPos pos = sign.getPos();
        if (def == null) { ContentLog.LOGGER.error("Village plot sign at {}, {}, {} names the order word '{}', which no order has", pos.getX(), pos.getY(), pos.getZ(), word); }
        else {
            BlockPos chest = beside(world, pos);
            if (chest == null) { ContentLog.LOGGER.error("Village plot sign at {}, {}, {} names the order word '{}', but no chest is below it or beside it, so the order stays closed", pos.getX(), pos.getY(), pos.getZ(), word); }
            else { OrderBoard.of(world).open(world, def, chest, pos, null, false); }
        }
        clearMark(sign);
    }

    @Nullable private static OrderDef bySign(String word) {
        for (OrderDef def : DEFS.values()) {
            if (!def.sign.isEmpty() && def.sign.equalsIgnoreCase(word)) { return def; }
        }
        return null;
    }

    @Nullable private static BlockPos beside(World world, BlockPos sign) {
        List<BlockPos> near = new ArrayList<>();
        IBlockState state = world.getBlockState(sign);
        if (state.getBlock() == Blocks.WALL_SIGN) { near.add(sign.offset(state.getValue(BlockWallSign.FACING).getOpposite())); }
        near.add(sign.down());
        for (EnumFacing side : EnumFacing.Plane.HORIZONTAL) { near.add(sign.offset(side)); }
        for (BlockPos pos : near) {
            if (isChest(world.getTileEntity(pos))) { return pos; }
        }
        return null;
    }

    @SubscribeEvent public static void onInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getHand() != EnumHand.MAIN_HAND || event.getWorld().isRemote || !(event.getTarget() instanceof EntityCreature)) { return; }
        EntityPlayer player = event.getEntityPlayer();
        EntityCreature mob = (EntityCreature) event.getTarget();
        ItemStack held = player.getHeldItemMainhand();
        EntityVariantDef variant = ContentEntities.BY_CLASS.get(mob.getClass());
        if (player.isSneaking() || held.isEmpty() || variant == null || !wants(variant)) { return; }
        boolean done = hired(player, mob, held) || handedTool(player, mob, held);
        if (!done) { return; }
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
    }

    private static boolean hired(EntityPlayer player, EntityLiving mob, ItemStack held) {
        if (hireStack.isEmpty() || !ContentStacks.matches(held, hireStack.getItem(), hireStack.getMetadata()) || !free(mob)) { return false; }
        enlist(player, mob);
        if (!player.capabilities.isCreativeMode) { held.shrink(1); }
        ContentLog.LOGGER.info("{} hired {} at {}, {}, {}", player.getName(), mob.getName(), (int) mob.posX, (int) mob.posY, (int) mob.posZ);
        return true;
    }

    private static void enlist(EntityPlayer player, EntityLiving mob) {
        ScorePlayerTeam side = teamOf(player);
        if (side != null) { mob.world.getScoreboard().addPlayerToTeam(mob.getCachedUniqueIdString(), side.getName()); }
        else { bind(mob, player.getUniqueID()); }
    }

    @Nullable static ScorePlayerTeam teamOf(EntityPlayer player) { return player.world.getScoreboard().getPlayersTeam(player.getName()); }

    private static boolean handedTool(EntityPlayer player, EntityLiving mob, ItemStack held) {
        OrderDef def = null;
        for (OrderDef one : DEFS.values()) {
            if (OrderItems.isKind(held, one.tool) && takes(one, mob)) {
                def = one;
                break;
            }
        }
        if (def == null) { return false; }
        Team side = mob.getTeam();
        ScorePlayerTeam theirs = teamOf(player);
        if (side != null && (theirs == null || !side.getName().equals(theirs.getName()))) { return false; }
        UUID boss = boss(mob);
        if (side == null && boss != null && !boss.equals(player.getUniqueID())) { return false; }
        BlockPos chest = nearestChest(mob.world, new BlockPos(mob), def.widest());
        if (chest == null) { return false; }
        if (free(mob)) { enlist(player, mob); }
        ItemStack old = mob.getHeldItemMainhand();
        mob.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, held.copy());
        mob.setDropChance(EntityEquipmentSlot.MAINHAND, 2.0F);
        if (!player.capabilities.isCreativeMode) { player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY); }
        if (!old.isEmpty() && !player.addItemStackToInventory(old)) { player.dropItem(old, false); }
        OrderBoard board = OrderBoard.of(mob.world);
        WorkOrder order = board.open(mob.world, def, chest, null, player, false);
        OrderBoard.release(mob, board.byId(mob.getEntityData().getInteger(OrderBoard.ORDER)));
        board.lease(mob, order, mob.world.getTotalWorldTime());
        ContentLog.LOGGER.info("{} handed {} a {} and the job {}, anchored to the chest at {}, {}, {}", player.getName(), mob.getName(), def.tool, def.name, chest.getX(), chest.getY(), chest.getZ());
        return true;
    }

    @Nullable private static BlockPos nearestChest(World world, BlockPos from, int radius) {
        BlockPos[] best = {null};
        double[] bestDistance = {Double.MAX_VALUE};
        TileScan.within(world, from, radius, tile -> {
            double distance = tile.getPos().distanceSq(from);
            if (distance > (double) radius * radius || distance >= bestDistance[0] || !isChest(tile)) { return true; }
            best[0] = tile.getPos();
            bestDistance[0] = distance;
            return true;
        });
        return best[0];
    }

    private static final class Pending {
        @Nullable private final UUID player;
        private final long until;

        private Pending(@Nullable UUID player, long until) {
            this.player = player;
            this.until = until;
        }
    }
}
