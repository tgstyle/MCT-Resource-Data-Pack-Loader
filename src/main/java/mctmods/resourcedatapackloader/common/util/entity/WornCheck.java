package mctmods.resourcedatapackloader.common.util.entity;

import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.core.integration.curios.ContentCurios;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class WornCheck {
    public static final WornCheck NONE = new WornCheck("", ItemStack.EMPTY, false);
    private static final String GATE = "Gate";

    private final String item;
    private final ItemStack wanted;
    private final boolean each;

    private WornCheck(String item, ItemStack wanted, boolean each) {
        this.item = item;
        this.wanted = wanted;
        this.each = each;
    }

    public static WornCheck parse(JsonObject json, ResourceLocation key, String kind) {
        String item = Json.getString(json, "worn", "").trim();
        boolean each = GATE.equals(kind) && Json.getBoolean(json, "each", false);
        if (item.isEmpty()) {
            if (each) { ContentLog.LOGGER.error("{} {} sets each without worn, which does nothing", kind, key); }
            return NONE;
        }
        return new WornCheck(item, ContentStacks.parse(key, item, 1), each);
    }

    public boolean present() { return !item.isEmpty(); }

    public boolean each() { return each; }

    public Component describe() { return wanted.isEmpty() ? Component.literal(item) : wanted.getHoverName(); }

    public boolean met(Player player) { return !present() || !found(player).isEmpty(); }

    public ItemStack found(Player player) {
        if (wanted.isEmpty()) { return ItemStack.EMPTY; }
        for (ItemStack stack : player.getInventory().armor) {
            if (ContentStacks.matches(stack, wanted)) { return stack; }
        }
        List<ItemStack> curios = ContentCurios.worn(player, stack -> ContentStacks.matches(stack, wanted));
        return curios.isEmpty() ? ItemStack.EMPTY : curios.getFirst();
    }
}
