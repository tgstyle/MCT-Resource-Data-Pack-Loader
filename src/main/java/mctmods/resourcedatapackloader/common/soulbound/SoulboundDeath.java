package mctmods.resourcedatapackloader.common.soulbound;

import mctmods.resourcedatapackloader.common.loot.PlayerLoot;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class SoulboundDeath {
    private static final String STASH = "rdpl_soulbound";
    private static final String SLOT = "Slot";
    private static final String STACK = "Stack";
    private static final Map<UUID, List<Held>> RECORDS = new HashMap<>();

    private SoulboundDeath() {}

    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        HolderLookup.Provider registries = player.registryAccess();
        player.getPersistentData().remove(STASH);
        RECORDS.remove(player.getUUID());
        if (PlayerLoot.keeping(player)) { return; }
        Inventory inventory = player.getInventory();
        ListTag stash = new ListTag();
        List<Held> recorded = new ArrayList<>();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            List<SoulboundMark> marks = ContentSoulbound.matching(registries, stack);
            if (marks.isEmpty()) { continue; }
            if (graveOnly(marks) && !ContentSoulbound.vanishing(stack)) {
                recorded.add(new Held(slot, stack.copy()));
                continue;
            }
            inventory.setItem(slot, ItemStack.EMPTY);
            stash.add(entry(registries, slot, stack));
        }
        if (!stash.isEmpty()) { player.getPersistentData().put(STASH, stash); }
        if (!recorded.isEmpty()) { RECORDS.put(player.getUUID(), recorded); }
    }

    public static void onDeathCanceled(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        if (event.isCanceled()) {
            RECORDS.remove(player.getUUID());
            restore(player, player);
        }
        else if (PlayerLoot.keeping(player)) { rollKept(player); }
        else { rollStashed(player); }
    }

    private static void rollKept(ServerPlayer player) {
        HolderLookup.Provider registries = player.registryAccess();
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            ContentSoulbound.roll(registries, ContentSoulbound.matching(registries, stack), mark -> mark.rollOnKeepInventory, stack, player.getRandom());
        }
    }

    private static void rollStashed(ServerPlayer player) {
        HolderLookup.Provider registries = player.registryAccess();
        ListTag stash = player.getPersistentData().getList(STASH, Tag.TAG_COMPOUND);
        ListTag kept = new ListTag();
        for (int at = 0; at < stash.size(); at++) {
            CompoundTag saved = stash.getCompound(at);
            ItemStack stack = load(registries, saved);
            if (stack.isEmpty()) {
                kept.add(saved);
                continue;
            }
            ContentSoulbound.roll(registries, ContentSoulbound.matching(registries, stack), mark -> mark.loseFirst, stack, player.getRandom());
            List<SoulboundMark> marks = ContentSoulbound.matching(registries, stack);
            if (marks.isEmpty()) {
                give(player, saved.getInt(SLOT), stack);
                continue;
            }
            ContentSoulbound.roll(registries, marks, mark -> !mark.loseFirst, stack, player.getRandom());
            kept.add(entry(registries, saved.getInt(SLOT), stack));
        }
        if (!kept.isEmpty()) { player.getPersistentData().put(STASH, kept); }
        else { player.getPersistentData().remove(STASH); }
    }

    public static void onDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        List<Held> recorded = RECORDS.remove(player.getUUID());
        if (recorded == null || event.isCanceled()) { return; }
        HolderLookup.Provider registries = player.registryAccess();
        ListTag stash = player.getPersistentData().getList(STASH, Tag.TAG_COMPOUND);
        Iterator<ItemEntity> drops = event.getDrops().iterator();
        while (drops.hasNext()) {
            ItemStack stack = drops.next().getItem();
            Held held = take(recorded, stack);
            if (held == null) { continue; }
            List<SoulboundMark> marks = ContentSoulbound.matching(registries, stack);
            ContentSoulbound.roll(registries, marks, mark -> mark.loseFirst, stack, player.getRandom());
            marks = ContentSoulbound.matching(registries, stack);
            if (marks.isEmpty()) { continue; }
            drops.remove();
            ContentSoulbound.roll(registries, marks, mark -> !mark.loseFirst, stack, player.getRandom());
            stash.add(entry(registries, held.slot(), stack));
        }
        if (!stash.isEmpty()) { player.getPersistentData().put(STASH, stash); }
    }

    public static void onClone(PlayerEvent.Clone event) {
        if (event.isWasDeath() && !event.getEntity().level().isClientSide) { restore(event.getOriginal(), event.getEntity()); }
    }

    private static boolean graveOnly(List<SoulboundMark> marks) {
        for (SoulboundMark mark : marks) {
            if (!mark.grave) { return false; }
        }
        return true;
    }

    @Nullable private static Held take(List<Held> recorded, ItemStack stack) {
        for (Iterator<Held> held = recorded.iterator(); held.hasNext(); ) {
            Held next = held.next();
            if (ItemStack.matches(next.stack(), stack)) {
                held.remove();
                return next;
            }
        }
        return null;
    }

    private static CompoundTag entry(HolderLookup.Provider registries, int slot, ItemStack stack) {
        CompoundTag entry = new CompoundTag();
        entry.putInt(SLOT, slot);
        entry.put(STACK, stack.save(registries));
        return entry;
    }

    private static ItemStack load(HolderLookup.Provider registries, CompoundTag entry) {
        Tag stored = entry.get(STACK);
        return stored == null ? ItemStack.EMPTY : ItemStack.parse(registries, stored).orElse(ItemStack.EMPTY);
    }

    private static void restore(Player from, Player to) {
        HolderLookup.Provider registries = to.registryAccess();
        ListTag stash = from.getPersistentData().getList(STASH, Tag.TAG_COMPOUND);
        for (int at = 0; at < stash.size(); at++) {
            CompoundTag entry = stash.getCompound(at);
            ItemStack stack = load(registries, entry);
            if (stack.isEmpty()) {
                ContentLog.LOGGER.error("A soulbound stack of {} no longer loads, skipping it", to.getName().getString());
                continue;
            }
            give(to, entry.getInt(SLOT), stack);
        }
        from.getPersistentData().remove(STASH);
        to.getPersistentData().remove(STASH);
    }

    private static void give(Player player, int slot, ItemStack stack) {
        Inventory inventory = player.getInventory();
        if (slot >= 0 && slot < inventory.getContainerSize() && inventory.getItem(slot).isEmpty()) { inventory.setItem(slot, stack); }
        else if (!inventory.add(stack)) { player.drop(stack, false); }
    }

    private record Held(int slot, ItemStack stack) {}
}
