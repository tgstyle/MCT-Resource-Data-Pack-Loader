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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public final class SoulboundDeath {
    private static final String STASH = "rdpl_soulbound";
    private static final String SLOT = "Slot";
    private static final String STACK = "Stack";
    private static final Map<UUID, List<Held>> RECORDS = new HashMap<>();

    private SoulboundDeath() {}

    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        player.getPersistentData().remove(STASH);
        RECORDS.remove(player.getUUID());
        if (PlayerLoot.keeping(player)) { return; }
        Inventory inventory = player.getInventory();
        ListTag stash = new ListTag();
        List<Held> recorded = new ArrayList<>();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            List<SoulboundMark> marks = ContentSoulbound.matching(stack);
            if (marks.isEmpty()) { continue; }
            if (graveOnly(marks) && !EnchantmentHelper.hasVanishingCurse(stack)) {
                recorded.add(new Held(slot, stack.copy()));
                continue;
            }
            inventory.setItem(slot, ItemStack.EMPTY);
            stash.add(entry(slot, stack));
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
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            ContentSoulbound.roll(ContentSoulbound.matching(stack), mark -> mark.rollOnKeepInventory, stack, player.getRandom());
        }
    }

    private static void rollStashed(ServerPlayer player) {
        ListTag stash = stash(player);
        ListTag kept = new ListTag();
        for (int at = 0; at < stash.size(); at++) {
            CompoundTag saved = stash.getCompound(at);
            ItemStack stack = stackOf(saved);
            if (stack.isEmpty()) {
                kept.add(saved);
                continue;
            }
            ContentSoulbound.roll(ContentSoulbound.matching(stack), mark -> mark.loseFirst, stack, player.getRandom());
            List<SoulboundMark> marks = ContentSoulbound.matching(stack);
            if (marks.isEmpty()) {
                give(player, saved.getInt(SLOT), stack);
                continue;
            }
            ContentSoulbound.roll(marks, mark -> !mark.loseFirst, stack, player.getRandom());
            kept.add(entry(saved.getInt(SLOT), stack));
        }
        if (!kept.isEmpty()) { player.getPersistentData().put(STASH, kept); }
        else { player.getPersistentData().remove(STASH); }
    }

    public static void onDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        List<Held> recorded = RECORDS.remove(player.getUUID());
        if (recorded == null || event.isCanceled()) { return; }
        ListTag stash = stash(player);
        Iterator<ItemEntity> drops = event.getDrops().iterator();
        while (drops.hasNext()) {
            ItemStack stack = drops.next().getItem();
            Held held = take(recorded, stack);
            if (held == null) { continue; }
            List<SoulboundMark> marks = ContentSoulbound.matching(stack);
            ContentSoulbound.roll(marks, mark -> mark.loseFirst, stack, player.getRandom());
            marks = ContentSoulbound.matching(stack);
            if (marks.isEmpty()) { continue; }
            drops.remove();
            ContentSoulbound.roll(marks, mark -> !mark.loseFirst, stack, player.getRandom());
            stash.add(entry(held.slot, stack));
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
            if (ItemStack.matches(next.stack, stack)) {
                held.remove();
                return next;
            }
        }
        return null;
    }

    private static ListTag stash(Player player) { return player.getPersistentData().getList(STASH, Tag.TAG_COMPOUND); }

    private static ItemStack stackOf(CompoundTag entry) { return ItemStack.of(entry.getCompound(STACK)); }

    private static CompoundTag entry(int slot, ItemStack stack) {
        CompoundTag entry = new CompoundTag();
        entry.putInt(SLOT, slot);
        entry.put(STACK, stack.save(new CompoundTag()));
        return entry;
    }

    private static void restore(Player from, Player to) {
        ListTag stash = stash(from);
        for (int at = 0; at < stash.size(); at++) {
            CompoundTag entry = stash.getCompound(at);
            ItemStack stack = loaded(entry, to);
            if (stack.isEmpty()) { continue; }
            give(to, entry.getInt(SLOT), stack);
        }
        from.getPersistentData().remove(STASH);
        to.getPersistentData().remove(STASH);
    }

    private static ItemStack loaded(CompoundTag entry, Player player) {
        ItemStack stack = stackOf(entry);
        if (stack.isEmpty()) { ContentLog.LOGGER.error("A soulbound stack of {} no longer loads, skipping it", player.getName().getString()); }
        return stack;
    }

    private static void give(Player player, int slot, ItemStack stack) {
        Inventory inventory = player.getInventory();
        if (slot >= 0 && slot < inventory.getContainerSize() && inventory.getItem(slot).isEmpty()) { inventory.setItem(slot, stack); }
        else if (!inventory.add(stack)) { player.drop(stack, false); }
    }

    private record Held(int slot, ItemStack stack) {}
}
