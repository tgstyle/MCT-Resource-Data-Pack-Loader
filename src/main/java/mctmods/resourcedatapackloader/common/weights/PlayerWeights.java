package mctmods.resourcedatapackloader.common.weights;

import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;
import mctmods.resourcedatapackloader.common.util.entity.Advancements;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

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
        Entry entry = CACHE.computeIfAbsent(player.getUUID(), _ -> new Entry());
        if (entry.dirty || entry.generation != ContentWeights.generation()) { refresh(player, entry); }
        return entry;
    }

    static void mark(Entity entity) {
        if (!(entity instanceof Player) || entity.level().isClientSide() || !ContentWeights.any()) { return; }
        CACHE.computeIfAbsent(entity.getUUID(), _ -> new Entry()).dirty = true;
    }

    static boolean counted(Player player, WeightDef settings) { return !player.isSpectator() && (!player.isCreative() || settings.creative); }

    private static int limitOf(Player player, WeightDef settings, List<ItemStack> worn) {
        if (!settings.limited()) { return 0; }
        long limit = settings.limit;
        for (WeightDef.Bonus bonus : settings.bonuses) {
            if (bonus.advancement() != null && Advancements.has(player, bonus.advancement()) || wearing(player, worn, bonus.worn())) { limit += bonus.add(); }
        }
        return Math.clamp(limit, 1, Integer.MAX_VALUE);
    }

    private static boolean wearing(Player player, List<ItemStack> worn, List<String> names) {
        if (names.isEmpty()) { return false; }
        for (ItemStack stack : worn) {
            if (named(stack, names)) { return true; }
        }
        return named(player.getOffhandItem(), names);
    }

    private static boolean named(ItemStack stack, List<String> names) { return !stack.isEmpty() && names.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().toLowerCase(Locale.ROOT)); }

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

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) { return; }
        if (!ContentWeights.any()) {
            if (!CACHE.isEmpty() && CACHE.remove(player.getUUID()) != null) { WeightTiers.clear(player); }
            return;
        }
        Entry entry = CACHE.computeIfAbsent(player.getUUID(), _ -> new Entry());
        if (entry.dirty || entry.generation != ContentWeights.generation() || (player.tickCount + player.getId()) % SCAN == 0) { refresh(player, entry); }
        if (entry.heavy) { WeightTiers.hold(player, entry); }
    }

    public static void onPickup(ItemEntityPickupEvent.Post event) { mark(event.getPlayer()); }

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
