package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;

public record ItemMatch(@Nullable Item item, @Nullable TagKey<Item> tag, int rank) {
    public static final String ANY = "*";
    private static final int ANY_RANK = 0;
    private static final int TAG_RANK = 1;
    private static final int ITEM_RANK = 2;

    @Nullable public static ItemMatch parse(ResourceLocation key, String text) {
        String held = text.trim();
        if (held.isEmpty()) {
            ContentLog.LOGGER.error("{} names no item, item tag or {} to match, ignoring it", key, ANY);
            return null;
        }
        if (ANY.equals(held)) { return new ItemMatch(null, null, ANY_RANK); }
        if (held.startsWith("#")) {
            ResourceLocation name = ResourceLocation.tryParse(held.substring(1).trim());
            if (name == null) {
                ContentLog.LOGGER.error("{} names the tag '{}', which is not a valid tag id, ignoring it", key, held);
                return null;
            }
            return new ItemMatch(null, TagKey.create(Registries.ITEM, name), TAG_RANK);
        }
        Item item = ContentStacks.find(key, held);
        return item == null ? null : new ItemMatch(item, null, ITEM_RANK);
    }

    public boolean matches(ItemStack stack) {
        if (stack.isEmpty()) { return false; }
        if (tag != null) { return stack.is(tag); }
        return item == null || stack.is(item);
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
