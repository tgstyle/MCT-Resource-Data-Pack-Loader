package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.content.card.CardFire;
import mctmods.resourcedatapackloader.content.def.CostPart;
import mctmods.resourcedatapackloader.content.def.PortwayCost;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.PlayerPersisted;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import javax.annotation.Nullable;

public final class PortwayCosts {
    private static final String READY = "ready";
    private static final long TICKS = 20L;

    private PortwayCosts() {}

    public static int costOf(CostPart part, double distance, boolean crossDimension) {
        int total = part.amount + (part.perBlocks > 0 ? (int) Math.ceil(distance / part.perBlocks) : 0) + (crossDimension ? part.dimension : 0);
        return part.max > 0 ? Math.min(total, part.max) : total;
    }

    private static int barCap(int level) { return level >= 30 ? 112 + (level - 30) * 9 : level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2; }

    private static int points(EntityPlayer player) {
        long total = 0L;
        for (int level = 0; level < player.experienceLevel; level++) { total += barCap(level); }
        total += Math.round(player.experience * barCap(player.experienceLevel));
        return (int) Math.min(total, Integer.MAX_VALUE);
    }

    private static void takePoints(EntityPlayer player, int taken) {
        int left = Math.max(0, points(player) - taken);
        player.experienceTotal = left;
        int level = 0;
        int rest = left;
        while (rest >= barCap(level)) {
            rest -= barCap(level);
            level++;
        }
        player.experienceLevel = level;
        player.experience = (float) rest / barCap(level);
        player.addExperienceLevel(0);
    }

    public static boolean exempt(EntityPlayer player) { return player.isCreative() || player.isSpectator(); }

    @Nullable private static Item item(CostPart part) {
        Item found = part.id.isEmpty() ? null : Item.REGISTRY.getObject(new ResourceLocation(part.id));
        return found == null || found == Items.AIR ? null : found;
    }

    private static int carried(EntityPlayer player, Item item) {
        int total = 0;
        for (ItemStack held : player.inventory.mainInventory) {
            if (held.getItem() == item) { total += held.getCount(); }
        }
        return total;
    }

    public static long ready(EntityPlayer player) { return PlayerPersisted.read(player, Portways.SECTION).getLong(READY); }

    public static void setReady(EntityPlayer player, long at) {
        if (at > 0L) { PlayerPersisted.section(player, Portways.SECTION).setLong(READY, at); }
        else { PlayerPersisted.section(player, Portways.SECTION).removeTag(READY); }
    }

    public static void cooldown(EntityPlayer player, int seconds) { setReady(player, seconds > 0 ? CardFire.clock(player.world) + seconds * TICKS : 0L); }

    public static int waiting(EntityPlayer player) {
        if (exempt(player)) { return 0; }
        long left = ready(player) - CardFire.clock(player.world);
        return left > 0L ? (int) ((left + TICKS - 1L) / TICKS) : 0;
    }

    public static void stamp(EntityPlayer player, PortwayCost cost) {
        if (cost.cooldown > 0 && !exempt(player)) { setReady(player, CardFire.clock(player.world) + cost.cooldown * TICKS); }
    }

    @Nullable public static Refusal refusal(EntityPlayer player, PortwayCost cost, double distance, boolean cross) {
        if (exempt(player)) { return null; }
        int seconds = waiting(player);
        if (seconds > 0) { return new Refusal("cooldown", Lang.tr(player, Portways.counted("rdpl.portway.cooldown", seconds), seconds)); }
        int xp = costOf(cost.xp, distance, cross);
        if (xp > 0 && (cost.xp.points ? points(player) : player.experienceLevel) < xp) { return new Refusal("cost", Lang.tr(player, Portways.counted(cost.xp.points ? "rdpl.portway.cost.xp.points" : "rdpl.portway.cost.xp", xp), xp)); }
        Item item = item(cost.item);
        int count = costOf(cost.item, distance, cross);
        if (item != null && count > 0 && carried(player, item) < count) { return new Refusal("cost", Lang.tr(player, "rdpl.portway.cost.item", count, new ItemStack(item).getDisplayName())); }
        return null;
    }

    public static boolean charge(EntityPlayer player, PortwayCost cost, double distance, boolean cross) {
        if (exempt(player)) { return false; }
        int xp = costOf(cost.xp, distance, cross);
        if (xp > 0 && cost.xp.points) { takePoints(player, xp); }
        else if (xp > 0) { player.addExperienceLevel(-xp); }
        Item item = item(cost.item);
        int count = costOf(cost.item, distance, cross);
        int owed = count;
        for (ItemStack held : player.inventory.mainInventory) {
            if (count <= 0) { break; }
            if (held.getItem() != item) { continue; }
            int taken = Math.min(count, held.getCount());
            held.shrink(taken);
            count -= taken;
        }
        float hurt = Math.min(costOf(cost.health, distance, cross), player.getHealth() - 1.0F);
        if (hurt > 0.0F) { player.setHealth(player.getHealth() - hurt); }
        int hungry = Math.min(costOf(cost.hunger, distance, cross), player.getFoodStats().getFoodLevel());
        if (hungry > 0) { player.getFoodStats().setFoodLevel(player.getFoodStats().getFoodLevel() - hungry); }
        return xp > 0 || owed > 0 && item != null || hurt > 0.0F || hungry > 0;
    }

    public static final class Refusal {
        public final String reason;
        public final String text;

        private Refusal(String reason, String text) {
            this.reason = reason;
            this.text = text;
        }
    }
}
