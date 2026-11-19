package mctmods.resourcedatapackloader.common.util.entity;

import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.core.integration.curios.ContentCurios;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class WornCheck {
    public static final WornCheck NONE = new WornCheck("", null, false);
    private static final String GATE = "Gate";

    private final String item;
    @Nullable private final Item wanted;
    private final boolean each;

    private WornCheck(String item, @Nullable Item wanted, boolean each) {
        this.item = item;
        this.wanted = wanted;
        this.each = each;
    }

    public static WornCheck parse(JsonObject json, Identifier key, String kind) {
        String item = Json.getString(json, "worn", "").trim();
        boolean each = GATE.equals(kind) && Json.getBoolean(json, "each", false);
        if (item.isEmpty()) {
            if (each) { ContentLog.LOGGER.error("{} {} sets each without worn, which does nothing", kind, key); }
            return NONE;
        }
        Item found = ContentStacks.find(key, item);
        return new WornCheck(item, found == Items.AIR ? null : found, each);
    }

    public boolean present() { return !item.isEmpty(); }

    public boolean each() { return each; }

    public String describe() { return wanted == null ? item : wanted.getDefaultInstance().getHoverName().getString(); }

    public boolean met(Player player) { return !present() || !found(player).isEmpty(); }

    public ItemStack found(Player player) {
        if (wanted == null) { return ItemStack.EMPTY; }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            if (slot.isArmor() && stack.is(wanted)) { return stack; }
        }
        List<ItemStack> curios = ContentCurios.worn(player, stack -> stack.is(wanted));
        return curios.isEmpty() ? ItemStack.EMPTY : curios.getFirst();
    }
}
