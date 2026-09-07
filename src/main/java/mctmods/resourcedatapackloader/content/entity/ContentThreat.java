package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Says;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.items.IItemHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public final class ContentThreat {
    private static final int SAMPLE = 100;
    private static final double REACH = 128.0D * 128.0D;
    private static final ResourceLocation KEY = ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "threatitems");
    private static final Map<UUID, Integer> BANDS = new HashMap<>();
    private static final Map<Level, List<Carrier>> CARRIERS = new HashMap<>();
    private static final Map<Level, Integer> OTHERS = new HashMap<>();
    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static final Map<Integer, String> SAYS = new HashMap<>();
    private static List<String> rawItems = null;
    private static int[] scratch = new int[0];
    private static int[] levels = new int[0];
    private static int most;
    private static float spawnRate;
    private static float notice;
    private static boolean live;

    private ContentThreat() {}

    private record Entry(Item item, int level, int count, boolean batch) {
        boolean matches(ItemStack stack) { return stack.getItem() == item; }
    }

    private record Carrier(Entity entity, int band) {}

    private static boolean disabled() {
        if (ContentControl.off(ContentControl.SPAWNING)) { return true; }
        List<String> items = ContentControl.list(ContentControl.SPAWNING, "threatItems", Config.worldgen.threatItems());
        if (!items.equals(rawItems)) {
            rawItems = List.copyOf(items);
            live = read(items);
        }
        return !live;
    }

    private static boolean read(List<String> items) {
        ENTRIES.clear();
        SAYS.clear();
        List<Integer> bands = new ArrayList<>();
        for (String entry : ContentControl.list(ContentControl.SPAWNING, "threatLevels", Config.worldgen.threatLevels())) {
            try { bands.add(Integer.parseInt(entry.trim())); }
            catch (NumberFormatException wrong) { ContentLog.LOGGER.error("threatLevels names '{}', which is not a number, skipping it", entry); }
        }
        levels = bands.stream().mapToInt(Integer::intValue).toArray();
        most = ContentControl.number(ContentControl.SPAWNING, "threatMost", Config.worldgen.threatMost());
        spawnRate = Math.max(0.0F, ContentControl.decimal(ContentControl.SPAWNING, "threatSpawnRate", Config.worldgen.threatSpawnRate()));
        notice = Math.max(0.0F, ContentControl.decimal(ContentControl.SPAWNING, "threatNotice", Config.worldgen.threatNotice()));
        for (int i = 1; i < levels.length; i++) {
            if (levels[i] <= levels[i - 1]) {
                ContentLog.LOGGER.error("threatLevels must rise from one band to the next, {} after {} does not, so the threat level is off", levels[i], levels[i - 1]);
                levels = new int[0];
                break;
            }
        }
        for (String entry : items) { parse(entry); }
        for (String entry : ContentControl.list(ContentControl.SPAWNING, "threatSays", Config.worldgen.threatSays())) {
            String[] parts = entry.split("=", 2);
            if (parts.length < 2) {
                ContentLog.LOGGER.error("threatSays entry '{}' needs the form band=message, skipping it", entry);
                continue;
            }
            try { SAYS.put(Integer.parseInt(parts[0].trim()), parts[1].trim()); }
            catch (NumberFormatException wrong) { ContentLog.LOGGER.error("threatSays entry '{}' names band '{}', which is not a number, skipping it", entry, parts[0].trim()); }
        }
        scratch = new int[ENTRIES.size()];
        boolean on = levels.length > 0 && !ENTRIES.isEmpty();
        if (on) { ContentLog.LOGGER.debug("The threat level watches {} item entry/entries over {} band(s), scaling hostile spawns by {} and noticing {} block(s) farther at the top", ENTRIES.size(), levels.length, spawnRate, notice); }
        return on;
    }

    private static void parse(String entry) {
        String[] sides = entry.split("=", 2);
        if (sides.length < 2) {
            ContentLog.LOGGER.error("threatItems entry '{}' needs the form item=level,count[,each|batch], skipping it", entry);
            return;
        }
        String[] parts = sides[1].split(",");
        if (parts.length < 2 || parts.length > 3) {
            ContentLog.LOGGER.error("threatItems entry '{}' needs a level and a count after the =, skipping it", entry);
            return;
        }
        int level;
        int count;
        try {
            level = Integer.parseInt(parts[0].trim());
            count = Integer.parseInt(parts[1].trim());
        }
        catch (NumberFormatException wrong) {
            ContentLog.LOGGER.error("threatItems entry '{}' has a level or count that is not a number, skipping it", entry);
            return;
        }
        if (level <= 0 || count <= 0) {
            ContentLog.LOGGER.error("threatItems entry '{}' needs a level and a count above 0, skipping it", entry);
            return;
        }
        boolean batch = false;
        if (parts.length == 3) {
            String mode = parts[2].trim().toLowerCase(Locale.ROOT);
            if ("batch".equals(mode)) { batch = true; }
            else if (!"each".equals(mode)) {
                ContentLog.LOGGER.error("threatItems entry '{}' ends in '{}', which is neither each nor batch, skipping it", entry, parts[2].trim());
                return;
            }
        }
        ItemStack stack = ContentStacks.parse(KEY, sides[0].trim(), 1);
        if (stack.isEmpty()) { return; }
        ENTRIES.add(new Entry(stack.getItem(), level, Math.min(count, stack.getMaxStackSize()), batch));
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        if (server.getTickCount() % SAMPLE != 0 || disabled()) { return; }
        for (ServerLevel level : server.getAllLevels()) { sample(level); }
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { BANDS.remove(event.getEntity().getUUID()); }

    public static void onLevelUnload(LevelEvent.Unload event) {
        if (!(event.getLevel() instanceof Level level)) { return; }
        CARRIERS.remove(level);
        OTHERS.remove(level);
    }

    private static void sample(ServerLevel level) {
        List<Carrier> carriers = new ArrayList<>();
        int others = 0;
        int highest = 0;
        for (Entity entity : level.getAllEntities()) {
            if (entity.isRemoved()) { continue; }
            int score = score(entity);
            int band = 0;
            while (band < levels.length && score >= levels[band]) { band++; }
            if (entity instanceof ServerPlayer player) { noteBand(player, score, band); }
            if (band == 0) { continue; }
            carriers.add(new Carrier(entity, band));
            if (entity instanceof Player) { continue; }
            others++;
            highest = Math.max(highest, band);
        }
        CARRIERS.put(level, carriers.isEmpty() ? Collections.emptyList() : carriers);
        Integer before = OTHERS.put(level, others);
        if (before == null ? others > 0 : before != others) { ContentLog.LOGGER.debug("Dimension {} holds {} threat carrier(s) beyond the players, the highest in band {} of {}", level.dimension().location(), others, highest, levels.length); }
    }

    private static void noteBand(ServerPlayer player, int score, int band) {
        Integer before = BANDS.put(player.getUUID(), band);
        if (before != null && before == band) { return; }
        ContentLog.LOGGER.debug("Player {} carries a threat score of {} and stands in band {} of {}", player.getName().getString(), score, band, levels.length);
        if (before == null) { return; }
        String said = SAYS.get(band);
        if (said != null && !said.isEmpty()) { Says.tell(player, said, ChatFormatting.YELLOW); }
    }

    private static int score(Entity entity) {
        int[] held = scratch;
        Arrays.fill(held, 0);
        if (entity instanceof Player player) {
            if (player.isCreative() || player.isSpectator()) { return 0; }
            tally(held, player.getInventory().items);
            tally(held, player.getInventory().armor);
            tally(held, player.getInventory().offhand);
        }
        else if (entity instanceof ItemEntity item) { tally(held, item.getItem()); }
        else {
            IItemHandler handler = entity.getCapability(Capabilities.ItemHandler.ENTITY);
            if (handler != null) {
                for (int slot = 0; slot < handler.getSlots(); slot++) { tally(held, handler.getStackInSlot(slot)); }
            }
            else if (entity instanceof LivingEntity living) { tally(held, living.getAllSlots()); }
            else { return 0; }
        }
        int score = 0;
        for (int i = 0; i < held.length; i++) {
            if (held[i] <= 0) { continue; }
            Entry entry = ENTRIES.get(i);
            score += entry.batch() ? entry.level() * (held[i] / entry.count()) : entry.level() * Math.min(held[i], entry.count());
        }
        return most >= 0 ? Math.min(score, most) : score;
    }

    private static void tally(int[] held, Iterable<ItemStack> stacks) {
        for (ItemStack stack : stacks) { tally(held, stack); }
    }

    private static void tally(int[] held, ItemStack stack) {
        if (stack.isEmpty()) { return; }
        for (int i = 0; i < held.length; i++) {
            if (ENTRIES.get(i).matches(stack)) { held[i] += stack.getCount(); }
        }
    }

    private static int bandNear(Level level, double x, double y, double z, double reachSq) {
        List<Carrier> carriers = CARRIERS.get(level);
        if (carriers == null) { return 0; }
        int found = 0;
        for (Carrier carrier : carriers) {
            if (carrier.band() <= found || carrier.entity().isRemoved() || carrier.entity().distanceToSqr(x, y, z) > reachSq) { continue; }
            found = carrier.band();
        }
        return found;
    }

    public static float spawnRate(Level level, BlockPos pos) {
        if (disabled() || spawnRate == 1.0F) { return 1.0F; }
        int band = bandNear(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, REACH);
        if (band == 0) { return 1.0F; }
        return 1.0F + (spawnRate - 1.0F) * band / levels.length;
    }

    public static boolean allowed(Entity entity) {
        EntityVariantDef def = ContentEntities.def(entity);
        int least = def == null ? 0 : def.threatLeast();
        if (least <= 0) { return true; }
        if (disabled()) { return false; }
        return bandNear(entity.level(), entity.getX(), entity.getY(), entity.getZ(), REACH) >= least;
    }

    public static boolean docile(@Nullable LivingEntity target, Entity mob) { return target instanceof Player player && !provokes(player, mob); }

    public static boolean provokes(Player player, Entity mob) {
        EntityVariantDef def = ContentEntities.def(mob);
        int least = def == null ? 0 : def.threatHostile();
        if (least <= 0) { return true; }
        if (disabled()) { return false; }
        Integer band = BANDS.get(player.getUUID());
        return band != null && band >= least;
    }

    public static double notice(Entity owner, double base) {
        if (disabled() || notice <= 0.0F) { return base; }
        double reach = base + notice;
        int band = bandNear(owner.level(), owner.getX(), owner.getY(), owner.getZ(), reach * reach);
        if (band == 0) { return base; }
        return base + notice * band / levels.length;
    }
}
