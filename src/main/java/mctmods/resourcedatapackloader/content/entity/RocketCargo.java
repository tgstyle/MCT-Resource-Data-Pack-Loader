package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.def.FilterDef;
import mctmods.resourcedatapackloader.content.def.RocketDef;

import com.google.common.collect.MapMaker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class RocketCargo {
    private static final int RESERVED = 2;
    private static final int WARNING_COOLDOWN = 250;
    private static final Map<Entity, Long> WARNED = new MapMaker().weakKeys().makeMap();

    private RocketCargo() {}

    @Nullable public static RocketDef def(Object rocket) {
        EntityVariantDef def = ContentEntities.BY_CLASS.get(rocket.getClass());
        return def == null ? null : def.rocket;
    }

    public static int room(RocketDef def, IInventory rocket, ItemStack stack) { return def.cargo.room(stack, cargo(rocket)); }

    public static boolean refuses(Object rocket, ItemStack stack) {
        RocketDef def = def(rocket);
        return def != null && room(def, (IInventory) rocket, stack) < stack.getCount();
    }

    public static boolean grounded(Entity rocket) {
        RocketDef def = def(rocket);
        if (def == null || rocket.world.isRemote) { return false; }
        List<ItemStack> cargo = cargo((IInventory) rocket);
        for (FilterDef.Entry entry : def.requiredPayload) {
            if (entry.held(cargo) >= entry.amount) { continue; }
            ITextComponent wanted = entry.oreDict.isEmpty() ? new TextComponentTranslation(entry.stack().getTranslationKey() + ".name") : new TextComponentString(entry.oreDict);
            long now = rocket.world.getTotalWorldTime();
            Long warned = WARNED.get(rocket);
            if (warned != null && now - warned < WARNING_COOLDOWN) { return true; }
            WARNED.put(rocket, now);
            for (Entity rider : rocket.getPassengers()) {
                if (rider instanceof EntityPlayerMP) { rider.sendMessage(new TextComponentTranslation("rdpl.rocket.payloadMissing", entry.amount, wanted)); }
            }
            return true;
        }
        return false;
    }

    public static void preload(RocketDef def, NonNullList<ItemStack> cargo) {
        int slot = 0;
        for (FilterDef.Entry entry : def.payload) {
            ItemStack wanted = entry.stack();
            if (wanted.isEmpty()) { continue; }
            for (int left = entry.amount; left > 0 && slot < cargo.size() - RESERVED; slot++) {
                ItemStack loaded = wanted.copy();
                loaded.setCount(Math.min(left, wanted.getMaxStackSize()));
                left -= loaded.getCount();
                cargo.set(slot, loaded);
            }
        }
    }

    private static List<ItemStack> cargo(IInventory rocket) {
        List<ItemStack> held = new ArrayList<>();
        for (int slot = 0; slot < rocket.getSizeInventory() - RESERVED; slot++) { held.add(rocket.getStackInSlot(slot)); }
        return held;
    }
}
