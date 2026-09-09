package mctmods.resourcedatapackloader.content.compat;

import mctmods.resourcedatapackloader.content.item.ContentItemContainer;
import mctmods.resourcedatapackloader.util.ContentLog;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.BaubleItem;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class BaublesPouch {
    private static boolean told = false;

    private BaublesPouch() {}

    @Nullable public static ICapabilityProvider provider(String named) {
        BaubleType type = typeOf(named);
        if (type == null) { return null; }
        BaubleItem bauble = new BaubleItem(type);
        return new ICapabilityProvider() {
            @Override public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                return capability == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE;
            }
            @Override @Nullable public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                return capability == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE ? BaublesCapabilities.CAPABILITY_ITEM_BAUBLE.cast(bauble) : null;
            }
        };
    }

    @Nullable private static BaubleType typeOf(String named) {
        try { return BaubleType.valueOf(named.toUpperCase(Locale.ROOT)); }
        catch (IllegalArgumentException missing) {
            if (!told) {
                told = true;
                ContentLog.LOGGER.error("'{}' is not a Baubles slot, so the item is not worn. The slots are amulet, ring, belt, trinket, head, body and charm", named);
            }
            return null;
        }
    }

    public static int wornSlot(EntityPlayer player) { return nextWorn(player, -1); }

    public static int nextWorn(EntityPlayer player, int after) {
        IBaublesItemHandler worn = BaublesApi.getBaublesHandler(player);
        int slots = worn.getSlots();
        for (int step = 1; step <= slots; step++) {
            int slot = Math.floorMod(after + step, slots);
            ItemStack stack = worn.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof ContentItemContainer) { return slot; }
        }
        return -1;
    }

    @Nonnull public static ItemStack worn(EntityPlayer player, int slot) {
        IBaublesItemHandler held = BaublesApi.getBaublesHandler(player);
        return slot >= 0 && slot < held.getSlots() ? held.getStackInSlot(slot) : ItemStack.EMPTY;
    }
}
