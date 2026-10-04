package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.def.OrderDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public final class ContentOrders {
    private static final Gson GSON = new GsonBuilder().create();
    private static final String BOSS = "rdplBoss";
    private static final String TEAM = "team:";
    private static final String TAG = "tag:";
    private static final int SIGN_WAIT = 2400;
    private static final int CHECK = 20;
    private static final Map<String, OrderDef> DEFS = new LinkedHashMap<>();
    private static final Map<ResourceKey<Level>, Map<BlockPos, Pending>> PENDING = new HashMap<>();
    private static String stockName = "Stock";
    private static String hire = "";
    private static int spawnCap = 1;
    private static boolean anyGrouped;
    @Nullable private static ItemStack hireStack;

    private ContentOrders() {}

    public static boolean load() {
        DEFS.clear();
        OrderItems.forget();
        anyGrouped = false;
        stockName = "Stock";
        hire = "";
        spawnCap = 1;
        hireStack = null;
        Json.eachFile(PackManager.ORDERS, "order file", ContentOrders::read);
        if (!DEFS.isEmpty()) { Summary.info("orders", "Loaded " + DEFS.size() + " work order(s)"); }
        return !DEFS.isEmpty();
    }

    public static void listen() {
        NeoForge.EVENT_BUS.addListener(ContentOrders::onLevelTick);
        NeoForge.EVENT_BUS.addListener(ContentOrders::onPlace);
        NeoForge.EVENT_BUS.addListener(ContentOrders::onBreak);
        NeoForge.EVENT_BUS.addListener(ContentOrders::onInteract);
    }

    private static void read(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Order file {} is empty, ignoring it", key);
            return;
        }
        if (json.has("stockName")) { stockName = GsonHelper.getAsString(json, "stockName").trim(); }
        if (json.has("hire")) { hire = GsonHelper.getAsString(json, "hire").trim(); }
        if (json.has("spawnCap")) { spawnCap = Math.max(0, GsonHelper.getAsInt(json, "spawnCap")); }
        if (!json.has("orders")) { return; }
        JsonArray list = GsonHelper.getAsJsonArray(json, "orders");
        for (int i = 0; i < list.size(); i++) {
            String name = key + "#" + i;
            OrderDef def = list.get(i).isJsonObject() ? order(name, list.get(i).getAsJsonObject()) : null;
            if (def == null) { continue; }
            DEFS.put(name, def);
            for (String taker : def.takers()) { anyGrouped |= grouped(taker); }
        }
    }

    @Nullable private static OrderDef order(String name, JsonObject json) {
        OrderDef.Job job = null;
        String named = GsonHelper.getAsString(json, "job", "");
        for (OrderDef.Job one : OrderDef.Job.values()) {
            if (one.name().equalsIgnoreCase(named)) { job = one; }
        }
        if (job == null) {
            ContentLog.LOGGER.error("Order {} asks for the job '{}', which is not gather, mine, farm or haul, ignoring it", name, named);
            return null;
        }
        String blocks = GsonHelper.getAsString(json, "blocks", "").trim();
        if (blocks.isEmpty() && job != OrderDef.Job.FARM) {
            ContentLog.LOGGER.error("Order {} names no item tag under blocks, and only a farm order works without one, ignoring it", name);
            return null;
        }
        ResourceLocation tag = blocks.isEmpty() ? null : ResourceLocation.tryParse(blocks.startsWith("#") ? blocks.substring(1) : blocks);
        if (!blocks.isEmpty() && tag == null) {
            ContentLog.LOGGER.error("Order {} names '{}' under blocks, which is not an item tag id, ignoring it", name, blocks);
            return null;
        }
        List<String> takers = Json.strings(json, "takers");
        if (takers.isEmpty()) {
            ContentLog.LOGGER.error("Order {} names no takers, so no worker could ever take it, ignoring it", name);
            return null;
        }
        String deliver = GsonHelper.getAsString(json, "deliver", "chest");
        if (!"chest".equalsIgnoreCase(deliver) && !"self".equalsIgnoreCase(deliver)) { ContentLog.LOGGER.error("Order {} delivers to '{}', which is not chest or self, so it delivers to the chest", name, deliver); }
        List<Integer> byTier = new ArrayList<>();
        if (json.has("areaByTier")) {
            for (JsonElement radius : GsonHelper.getAsJsonArray(json, "areaByTier")) { byTier.add(Math.max(1, radius.getAsInt())); }
        }
        TagKey<Item> items = tag == null ? null : TagKey.create(Registries.ITEM, tag);
        return new OrderDef(name, job, items, Math.max(1, GsonHelper.getAsInt(json, "area", 16)), List.copyOf(byTier), "self".equalsIgnoreCase(deliver), Math.max(1, GsonHelper.getAsInt(json, "limit", 64)),
                Math.max(0, GsonHelper.getAsInt(json, "standing", 0)), Math.max(1, GsonHelper.getAsInt(json, "workers", 1)), GsonHelper.getAsInt(json, "priority", 0), takers,
                GsonHelper.getAsString(json, "sign", "").trim(), Math.max(0.01F, GsonHelper.getAsFloat(json, "speed", 1.0F)), GsonHelper.getAsString(json, "tool", "").trim());
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
            for (String taker : def.takers()) {
                if (taker.equalsIgnoreCase(variant.key().toString())) { return true; }
            }
        }
        return false;
    }

    private static boolean takes(OrderDef def, Mob mob) {
        EntityVariantDef variant = ContentEntities.def(mob);
        Team side = mob.getTeam();
        for (String taker : def.takers()) {
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
            if (variant != null && taker.equalsIgnoreCase(variant.key().toString())) { return true; }
        }
        return false;
    }

    static boolean serves(Mob mob, WorkOrder order) {
        if (!takes(order.def, mob)) { return false; }
        Team side = mob.getTeam();
        if (!order.team.isEmpty()) { return side != null && side.getName().equals(order.team); }
        UUID boss = boss(mob);
        if (boss != null) { return boss.equals(order.owner); }
        return side == null;
    }

    @Nullable static UUID boss(Mob mob) {
        String kept = mob.getPersistentData().getString(BOSS);
        if (kept.isEmpty()) { return null; }
        try { return UUID.fromString(kept); }
        catch (IllegalArgumentException ex) { return null; }
    }

    static void bind(Mob mob, UUID player) {
        mob.getPersistentData().putString(BOSS, player.toString());
        mob.setPersistenceRequired();
    }

    private static boolean free(Mob mob) { return boss(mob) == null && mob.getTeam() == null; }

    static boolean isOrderChest(ServerLevel level, BlockPos pos) { return OrderBoard.of(level).hasChest(pos); }

    private static boolean isChest(Level level, @Nullable BlockEntity tile) { return tile instanceof RandomizableContainerBlockEntity && OrderItems.handler(level, tile.getBlockPos()) != null; }

    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        OrderBoard.of(level).tick(level);
        if (level.getGameTime() % CHECK == 0) { signs(level); }
    }

    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) || !(event.getEntity() instanceof Player player)) { return; }
        BlockPos pos = event.getPos();
        Block placed = event.getPlacedBlock().getBlock();
        if (placed instanceof StandingSignBlock || placed instanceof WallSignBlock) {
            PENDING.computeIfAbsent(level.dimension(), dimension -> new HashMap<>()).put(pos.immutable(), new Pending(player.getUUID(), level.getGameTime() + SIGN_WAIT));
            return;
        }
        BlockEntity tile = level.getBlockEntity(pos);
        if (stockName.isEmpty() || !isChest(level, tile) || !(tile instanceof RandomizableContainerBlockEntity chest)) { return; }
        Component custom = chest.getCustomName();
        if (custom == null || !stockName.equalsIgnoreCase(custom.getString().trim())) { return; }
        OrderDef haul = null;
        for (OrderDef def : DEFS.values()) {
            if (def.job() == OrderDef.Job.HAUL) {
                haul = def;
                break;
            }
        }
        if (haul == null) {
            ContentLog.LOGGER.error("{} placed a chest named {} at {}, {}, {}, but no pack has a haul order, so nothing keeps it stocked", player.getName().getString(), stockName, pos.getX(), pos.getY(), pos.getZ());
            return;
        }
        OrderBoard.of(level).open(level, haul, pos, null, player, true);
    }

    public static void onBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof ServerLevel level) { OrderBoard.of(level).cancelAt(event.getPos()); }
    }

    private static void signs(ServerLevel level) {
        Map<BlockPos, Pending> waiting = PENDING.get(level.dimension());
        if (waiting == null || waiting.isEmpty()) { return; }
        long now = level.getGameTime();
        Iterator<Map.Entry<BlockPos, Pending>> walk = waiting.entrySet().iterator();
        while (walk.hasNext()) {
            Map.Entry<BlockPos, Pending> entry = walk.next();
            BlockPos pos = entry.getKey();
            if (now > entry.getValue().until() || !level.isLoaded(pos)) {
                walk.remove();
                continue;
            }
            if (!(level.getBlockEntity(pos) instanceof SignBlockEntity sign)) {
                walk.remove();
                continue;
            }
            String word = sign.getFrontText().getMessage(0, false).getString().trim();
            if (word.isEmpty()) { continue; }
            walk.remove();
            OrderDef def = bySign(word);
            Player by = level.getPlayerByUUID(entry.getValue().player());
            BlockPos chest = def == null || by == null ? null : beside(level, pos);
            if (chest != null) { OrderBoard.of(level).open(level, def, chest, pos, by, false); }
        }
    }

    @Nullable private static OrderDef bySign(String word) {
        for (OrderDef def : DEFS.values()) {
            if (!def.sign().isEmpty() && def.sign().equalsIgnoreCase(word)) { return def; }
        }
        return null;
    }

    @Nullable private static BlockPos beside(ServerLevel level, BlockPos sign) {
        List<BlockPos> near = new ArrayList<>();
        BlockState state = level.getBlockState(sign);
        if (state.getBlock() instanceof WallSignBlock) { near.add(sign.relative(state.getValue(WallSignBlock.FACING).getOpposite())); }
        near.add(sign.below());
        for (Direction side : Direction.Plane.HORIZONTAL) { near.add(sign.relative(side)); }
        for (BlockPos pos : near) {
            if (isChest(level, level.getBlockEntity(pos))) { return pos; }
        }
        return null;
    }

    public static void onInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getHand() != InteractionHand.MAIN_HAND || !(event.getLevel() instanceof ServerLevel level) || !(event.getTarget() instanceof PathfinderMob mob)) { return; }
        Player player = event.getEntity();
        ItemStack held = player.getMainHandItem();
        EntityVariantDef variant = ContentEntities.def(mob);
        if (player.isShiftKeyDown() || held.isEmpty() || variant == null || !wants(variant)) { return; }
        boolean done = hired(player, mob, held) || handedTool(level, player, mob, held);
        if (!done) { return; }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    private static boolean hired(Player player, Mob mob, ItemStack held) {
        ItemStack wanted = hireStack();
        if (wanted.isEmpty() || !ContentStacks.matches(held, wanted) || !free(mob)) { return false; }
        enlist(player, mob);
        if (!player.getAbilities().instabuild) { held.shrink(1); }
        ContentLog.LOGGER.info("{} hired {} at {}, {}, {}", player.getName().getString(), mob.getName().getString(), mob.getBlockX(), mob.getBlockY(), mob.getBlockZ());
        return true;
    }

    private static ItemStack hireStack() {
        if (hireStack == null) { hireStack = hire.isEmpty() ? ItemStack.EMPTY : ContentStacks.parse(ResourceLocation.fromNamespaceAndPath("rdpl", PackManager.ORDERS), hire, 1); }
        return hireStack;
    }

    private static void enlist(Player player, Mob mob) {
        PlayerTeam side = teamOf(player);
        if (side != null) { mob.level().getScoreboard().addPlayerToTeam(mob.getScoreboardName(), side); }
        else { bind(mob, player.getUUID()); }
    }

    @Nullable private static PlayerTeam teamOf(Player player) { return player.level().getScoreboard().getPlayersTeam(player.getScoreboardName()); }

    private static boolean handedTool(ServerLevel level, Player player, Mob mob, ItemStack held) {
        OrderDef def = null;
        for (OrderDef one : DEFS.values()) {
            if (OrderItems.isKind(held, one.tool()) && takes(one, mob)) {
                def = one;
                break;
            }
        }
        if (def == null) { return false; }
        Team side = mob.getTeam();
        PlayerTeam theirs = teamOf(player);
        if (side != null && (theirs == null || !side.getName().equals(theirs.getName()))) { return false; }
        UUID boss = boss(mob);
        if (side == null && boss != null && !boss.equals(player.getUUID())) { return false; }
        BlockPos chest = nearestChest(level, mob.blockPosition(), def.widest());
        if (chest == null) { return false; }
        if (free(mob)) { enlist(player, mob); }
        ItemStack old = mob.getMainHandItem();
        mob.setItemSlot(EquipmentSlot.MAINHAND, held.copy());
        mob.setDropChance(EquipmentSlot.MAINHAND, 2.0F);
        if (!player.getAbilities().instabuild) { player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY); }
        if (!old.isEmpty() && !player.getInventory().add(old)) { player.drop(old, false); }
        OrderBoard board = OrderBoard.of(level);
        WorkOrder order = board.open(level, def, chest, null, player, false);
        OrderBoard.release(mob, board.byId(mob.getPersistentData().getInt(OrderBoard.ORDER)));
        board.lease(mob, order, level.getGameTime());
        ContentLog.LOGGER.info("{} handed {} a {} and the job {}, anchored to the chest at {}, {}, {}", player.getName().getString(), mob.getName().getString(), def.tool(), def.name(), chest.getX(), chest.getY(), chest.getZ());
        return true;
    }

    @Nullable private static BlockPos nearestChest(ServerLevel level, BlockPos from, int radius) {
        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;
        for (int cx = from.getX() - radius >> 4; cx <= from.getX() + radius >> 4; cx++) {
            for (int cz = from.getZ() - radius >> 4; cz <= from.getZ() + radius >> 4; cz++) {
                if (!level.hasChunk(cx, cz)) { continue; }
                for (BlockEntity tile : level.getChunk(cx, cz).getBlockEntities().values()) {
                    double distance = tile.getBlockPos().distSqr(from);
                    if (distance > (double) radius * radius || distance >= bestDistance || !isChest(level, tile)) { continue; }
                    best = tile.getBlockPos();
                    bestDistance = distance;
                }
            }
        }
        return best;
    }

    private record Pending(UUID player, long until) {}
}
