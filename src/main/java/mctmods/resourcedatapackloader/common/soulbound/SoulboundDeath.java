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
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class SoulboundDeath {
    private static final String STASH = "rdpl_soulbound";
    private static final String SLOT = "Slot";
    private static final String STACK = "Stack";
    private static final int COMPOUND = 10;
    private static final Map<UUID, List<Held>> RECORDS = new HashMap<>();

    private SoulboundDeath() {}

    @SubscribeEvent(priority = EventPriority.HIGHEST) public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayerMP)) { return; }
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
        player.getEntityData().removeTag(STASH);
        RECORDS.remove(player.getUniqueID());
        if (PlayerLoot.keeping(player)) { return; }
        InventoryPlayer inventory = player.inventory;
        NBTTagList stash = new NBTTagList();
        List<Held> recorded = new ArrayList<>();
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            List<SoulboundMark> marks = ContentSoulbound.matching(stack);
            if (marks.isEmpty()) { continue; }
            if (graveOnly(marks) && !EnchantmentHelper.hasVanishingCurse(stack)) {
                recorded.add(new Held(slot, stack.copy()));
                continue;
            }
            inventory.setInventorySlotContents(slot, ItemStack.EMPTY);
            stash.appendTag(entry(slot, stack));
        }
        if (stash.tagCount() > 0) { player.getEntityData().setTag(STASH, stash); }
        if (!recorded.isEmpty()) { RECORDS.put(player.getUniqueID(), recorded); }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true) public static void onDeathCanceled(LivingDeathEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayerMP)) { return; }
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
        if (event.isCanceled()) {
            RECORDS.remove(player.getUniqueID());
            restore(player, player);
        }
        else if (PlayerLoot.keeping(player)) { rollKept(player); }
        else { rollStashed(player); }
    }

    private static void rollKept(EntityPlayerMP player) {
        InventoryPlayer inventory = player.inventory;
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            ContentSoulbound.roll(ContentSoulbound.matching(stack), mark -> mark.rollOnKeepInventory, stack, player.getRNG());
        }
    }

    private static void rollStashed(EntityPlayerMP player) {
        NBTTagList stash = player.getEntityData().getTagList(STASH, COMPOUND);
        NBTTagList kept = new NBTTagList();
        for (int at = 0; at < stash.tagCount(); at++) {
            NBTTagCompound saved = stash.getCompoundTagAt(at);
            ItemStack stack = new ItemStack(saved.getCompoundTag(STACK));
            if (stack.isEmpty()) {
                kept.appendTag(saved);
                continue;
            }
            ContentSoulbound.roll(ContentSoulbound.matching(stack), mark -> mark.loseFirst, stack, player.getRNG());
            List<SoulboundMark> marks = ContentSoulbound.matching(stack);
            if (marks.isEmpty()) {
                give(player, saved.getInteger(SLOT), stack);
                continue;
            }
            ContentSoulbound.roll(marks, mark -> !mark.loseFirst, stack, player.getRNG());
            kept.appendTag(entry(saved.getInteger(SLOT), stack));
        }
        if (kept.tagCount() > 0) { player.getEntityData().setTag(STASH, kept); }
        else { player.getEntityData().removeTag(STASH); }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true) public static void onDrops(PlayerDropsEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        List<Held> recorded = RECORDS.remove(player.getUniqueID());
        if (recorded == null || event.isCanceled()) { return; }
        NBTTagList stash = player.getEntityData().getTagList(STASH, COMPOUND);
        Iterator<EntityItem> drops = event.getDrops().iterator();
        while (drops.hasNext()) {
            ItemStack stack = drops.next().getItem();
            Held held = take(recorded, stack);
            if (held == null) { continue; }
            List<SoulboundMark> marks = ContentSoulbound.matching(stack);
            ContentSoulbound.roll(marks, mark -> mark.loseFirst, stack, player.getRNG());
            marks = ContentSoulbound.matching(stack);
            if (marks.isEmpty()) { continue; }
            drops.remove();
            ContentSoulbound.roll(marks, mark -> !mark.loseFirst, stack, player.getRNG());
            stash.appendTag(entry(held.slot, stack));
        }
        if (stash.tagCount() > 0) { player.getEntityData().setTag(STASH, stash); }
    }

    @SubscribeEvent public static void onClone(PlayerEvent.Clone event) {
        if (event.isWasDeath() && !event.getEntityPlayer().world.isRemote) { restore(event.getOriginal(), event.getEntityPlayer()); }
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
            if (ItemStack.areItemStacksEqual(next.stack, stack)) {
                held.remove();
                return next;
            }
        }
        return null;
    }

    private static NBTTagCompound entry(int slot, ItemStack stack) {
        NBTTagCompound entry = new NBTTagCompound();
        entry.setInteger(SLOT, slot);
        entry.setTag(STACK, stack.writeToNBT(new NBTTagCompound()));
        return entry;
    }

    private static void restore(EntityPlayer from, EntityPlayer to) {
        NBTTagList stash = from.getEntityData().getTagList(STASH, COMPOUND);
        for (int at = 0; at < stash.tagCount(); at++) {
            NBTTagCompound entry = stash.getCompoundTagAt(at);
            ItemStack stack = new ItemStack(entry.getCompoundTag(STACK));
            if (stack.isEmpty()) {
                ContentLog.LOGGER.error("A soulbound stack of {} no longer loads, skipping it", to.getName());
                continue;
            }
            give(to, entry.getInteger(SLOT), stack);
        }
        from.getEntityData().removeTag(STASH);
        to.getEntityData().removeTag(STASH);
    }

    private static void give(EntityPlayer player, int slot, ItemStack stack) {
        InventoryPlayer inventory = player.inventory;
        if (slot >= 0 && slot < inventory.getSizeInventory() && inventory.getStackInSlot(slot).isEmpty()) { inventory.setInventorySlotContents(slot, stack); }
        else if (!inventory.addItemStackToInventory(stack)) { player.dropItem(stack, false); }
    }

    private static final class Held {
        final int slot;
        final ItemStack stack;

        Held(int slot, ItemStack stack) {
            this.slot = slot;
            this.stack = stack;
        }
    }
}
