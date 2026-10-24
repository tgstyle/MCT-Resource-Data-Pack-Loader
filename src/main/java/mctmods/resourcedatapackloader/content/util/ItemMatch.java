package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;

public final class ItemMatch {
    public static final String ANY = "*";
    private static final int ANY_RANK = 0;
    private static final int TAG_RANK = 1;
    private static final int ITEM_RANK = 2;
    private final ItemStack item;
    private final String ore;
    private final int rank;

    private ItemMatch(ItemStack item, String ore, int rank) {
        this.item = item;
        this.ore = ore;
        this.rank = rank;
    }

    @Nullable public static ItemMatch parse(ResourceLocation key, String text) {
        String held = text.trim();
        if (held.isEmpty()) {
            ContentLog.LOGGER.error("{} names no item, ore name or {} to match, ignoring it", key, ANY);
            return null;
        }
        if (ANY.equals(held)) { return new ItemMatch(ItemStack.EMPTY, "", ANY_RANK); }
        if (held.startsWith("#")) {
            String ore = held.substring(1).trim();
            if (ore.isEmpty()) {
                ContentLog.LOGGER.error("{} names an empty ore name, ignoring it", key);
                return null;
            }
            return new ItemMatch(ItemStack.EMPTY, ore, TAG_RANK);
        }
        ItemStack stack = ContentStacks.parse(key, held, 1);
        return stack.isEmpty() ? null : new ItemMatch(stack, "", ITEM_RANK);
    }

    public boolean matches(ItemStack stack) {
        if (stack.isEmpty()) { return false; }
        if (rank == ANY_RANK) { return true; }
        if (rank == TAG_RANK) { return OreDictionary.containsMatch(false, OreDictionary.getOres(ore, false), stack); }
        return ContentStacks.matches(stack, item.getItem(), item.getMetadata());
    }

    @Nullable public static <T> T pick(List<T> rules, Function<T, ItemMatch> match, ItemStack stack) {
        T best = null;
        int bestRank = -1;
        for (T rule : rules) {
            ItemMatch each = match.apply(rule);
            if (each.rank >= bestRank && each.matches(stack)) {
                best = rule;
                bestRank = each.rank;
            }
        }
        return best;
    }
}
