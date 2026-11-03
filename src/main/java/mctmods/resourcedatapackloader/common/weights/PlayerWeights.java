package mctmods.resourcedatapackloader.common.weights;

import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;
import mctmods.resourcedatapackloader.common.util.entity.Advancements;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.registries.ForgeRegistries;

public final class PlayerWeights {
    private static final int SCAN = 10;
    private static final long REPORT = 60_000_000_000L;
    private static final Map<UUID, Entry> CACHE = new HashMap<>();
    private static long scans;
    private static long nanos;
    private static long reported = System.nanoTime();

    private PlayerWeights() {}

    public static final class Entry {
        public int total;
        public int limit;
        public int tier = -1;
        boolean dirty = true;
        int generation = -1;
        boolean stop;
        boolean heavy;
        long refused;
        double exhaustion;
    }

    public static Entry current(Player player) {
        Entry entry = CACHE.computeIfAbsent(player.getUUID(), id -> new Entry());
        if (entry.dirty || entry.generation != ContentWeights.generation()) { refresh(player, entry); }
        return entry;
    }

    static void mark(Entity entity) {
        if (!(entity instanceof Player) || entity.level().isClientSide() || !ContentWeights.any()) { return; }
        CACHE.computeIfAbsent(entity.getUUID(), id -> new Entry()).dirty = true;
    }

    static boolean counted(Player player, WeightDef settings) { return !player.isSpectator() && (!player.isCreative() || settings.creative); }

    private static int limitOf(Player player, WeightDef settings, List<ItemStack> worn) {
        if (!settings.limited()) { return 0; }
        long limit = settings.limit;
        for (WeightDef.Bonus bonus : settings.bonuses) {
            if (bonus.advancement() != null && Advancements.has(player, bonus.advancement()) || wearing(player, worn, bonus.worn())) { limit += bonus.add(); }
        }
        return (int) Math.max(1L, Math.min(Integer.MAX_VALUE, limit));
    }

    private static boolean wearing(Player player, List<ItemStack> worn, List<String> names) {
        if (names.isEmpty()) { return false; }
        for (ItemStack stack : worn) {
            if (named(stack, names)) { return true; }
        }
        return named(player.getOffhandItem(), names);
    }

    private static boolean named(ItemStack stack, List<String> names) { return !stack.isEmpty() && names.contains(String.valueOf(ForgeRegistries.ITEMS.getKey(stack.getItem())).toLowerCase(Locale.ROOT)); }

    private static void refresh(Player player, Entry entry) {
        long started = System.nanoTime();
        WeightDef settings = ContentWeights.settings();
        entry.dirty = false;
        entry.generation = ContentWeights.generation();
        if (counted(player, settings)) {
            List<ItemStack> worn = CarriedWeight.worn(player);
            entry.total = (int) Math.min(Integer.MAX_VALUE, CarriedWeight.total(player, settings, worn));
            entry.limit = limitOf(player, settings, worn);
        }
        else {
            entry.total = 0;
            entry.limit = settings.limit;
        }
        WeightTiers.sync(player, entry, settings, WeightTiers.pick(settings, entry.total, entry.limit));
        record(System.nanoTime() - started);
    }

    private static void record(long spent) {
        scans++;
        nanos += spent;
        long now = System.nanoTime();
        if (now - reported < REPORT) { return; }
        ContentLog.LOGGER.debug("Weights: {} scans, {} ns in the last {} s", scans, nanos, (now - reported) / 1_000_000_000L);
        scans = 0L;
        nanos = 0L;
        reported = now;
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase != TickEvent.Phase.END || player.level().isClientSide()) { return; }
        if (!ContentWeights.any()) {
            if (!CACHE.isEmpty() && CACHE.remove(player.getUUID()) != null) { WeightTiers.clear(player); }
            return;
        }
        Entry entry = CACHE.computeIfAbsent(player.getUUID(), id -> new Entry());
        if (entry.dirty || entry.generation != ContentWeights.generation() || (player.tickCount + player.getId()) % SCAN == 0) { refresh(player, entry); }
        if (entry.heavy) { WeightTiers.hold(player, entry); }
    }

    public static void onJump(LivingEvent.LivingJumpEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player) || entity.level().isClientSide() || !ContentWeights.any()) { return; }
        Entry entry = CACHE.get(entity.getUUID());
        if (entry == null || WeightTiers.jumps(ContentWeights.settings(), entry.tier)) { return; }
        Vec3 motion = entity.getDeltaMovement();
        entity.setDeltaMovement(motion.x, 0.0D, motion.z);
    }

    public static void onPickup(EntityItemPickupEvent event) { mark(event.getEntity()); }

    public static void onToss(ItemTossEvent event) { mark(event.getPlayer()); }

    public static void onOpen(PlayerContainerEvent.Open event) { mark(event.getEntity()); }

    public static void onClose(PlayerContainerEvent.Close event) { mark(event.getEntity()); }

    public static void onCrafted(PlayerEvent.ItemCraftedEvent event) { mark(event.getEntity()); }

    public static void onSmelted(PlayerEvent.ItemSmeltedEvent event) { mark(event.getEntity()); }

    public static void onUsed(LivingEntityUseItemEvent.Finish event) { mark(event.getEntity()); }

    public static void onPlace(BlockEvent.EntityPlaceEvent event) { mark(event.getEntity()); }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) { mark(event.getEntity()); }

    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) { mark(event.getEntity()); }

    public static void onDimension(PlayerEvent.PlayerChangedDimensionEvent event) { mark(event.getEntity()); }

    public static void onClone(PlayerEvent.Clone event) { mark(event.getEntity()); }

    public static void onAdvancement(AdvancementEvent.AdvancementEarnEvent event) { mark(event.getEntity()); }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { CACHE.remove(event.getEntity().getUUID()); }
}
