package mctmods.resourcedatapackloader.common.soulbound;

import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public final class ContentSoulbound {
    private static final String ENCHANTMENT = "enchantment";
    private static final String ITEMS = "items";
    private static final String NBT = "nbt";
    private static final String LOSE_CHANCE = "loseChance";
    private static final String GRAVES = "graves";
    private static final String PLAYER = "player";
    private static final String GRAVE = "grave";
    private static final String DEFAULT_TOOLTIP = "rdpl.tooltip.soulbound";
    private static final List<String> KEYS = List.of(ENCHANTMENT, ITEMS, NBT, LOSE_CHANCE, GRAVES, "rollOnKeepInventory", "loseFirst", "keepVanishing", "tooltip");
    private static final List<SoulboundMark> MARKS = new ArrayList<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private ContentSoulbound() {}

    private static void reload() {
        MARKS.clear();
        GENERATION.stale();
        if (Config.data.soulboundOff()) { return; }
        Json.eachObject(PackManager.SOULBOUND, "soulbound file", ContentSoulbound::read);
        if (!MARKS.isEmpty()) { Summary.info("soulbound", "Loaded " + MARKS.size() + " soulbound mark(s)"); }
    }

    private static void read(Identifier key, JsonObject json) {
        Json.unknownKeys(json, "the top level", KEYS);
        SoulboundMark.Kind kind = null;
        String name = "";
        List<ItemStack> items = new ArrayList<>();
        int given = 0;
        if (json.has(ENCHANTMENT)) {
            given++;
            kind = SoulboundMark.Kind.ENCHANTMENT;
            name = Json.getString(json, ENCHANTMENT, "");
        }
        if (json.has(ITEMS)) {
            given++;
            kind = SoulboundMark.Kind.ITEMS;
            for (String item : Json.strings(json, ITEMS)) {
                ItemStack stack = ContentStacks.parse(key, item, 1);
                if (!stack.isEmpty()) { items.add(stack); }
            }
        }
        if (json.has(NBT)) {
            given++;
            kind = SoulboundMark.Kind.NBT;
            name = Json.getString(json, NBT, "");
        }
        if (given != 1) {
            ContentLog.LOGGER.error("Soulbound file {} needs exactly one of '{}', '{}' or '{}', ignoring it", key, ENCHANTMENT, ITEMS, NBT);
            return;
        }
        if (name.isEmpty() && items.isEmpty()) {
            ContentLog.LOGGER.error("Soulbound file {} names nothing to mark, ignoring it", key);
            return;
        }
        if (kind == SoulboundMark.Kind.ITEMS && json.has(LOSE_CHANCE)) { ContentLog.LOGGER.error("Soulbound file {} sets {} on an items mark, which cannot be lost, so the key is ignored", key, LOSE_CHANCE); }
        float chance = kind == SoulboundMark.Kind.ITEMS ? 0.0F : Json.ranged(json, LOSE_CHANCE, 0.0F, 0.0F, 1.0F, key);
        boolean grave = Json.choice(json, GRAVES, PLAYER, key, Arrays.asList(PLAYER, GRAVE)).equals(GRAVE);
        MARKS.add(new SoulboundMark(kind, key, name, items, chance, Json.getBoolean(json, "rollOnKeepInventory", false), grave, Json.getBoolean(json, "loseFirst", false), Json.getBoolean(json, "keepVanishing", false), Json.getString(json, "tooltip", DEFAULT_TOOLTIP)));
    }

    static List<SoulboundMark> matching(HolderLookup.Provider access, ItemStack stack) {
        if (Config.data.soulboundOff() || stack.isEmpty()) { return Collections.emptyList(); }
        if (GENERATION.stale()) { reload(); }
        if (MARKS.isEmpty()) { return Collections.emptyList(); }
        boolean vanishing = vanishing(stack);
        List<SoulboundMark> found = new ArrayList<>();
        for (SoulboundMark mark : MARKS) {
            if ((!vanishing || mark.keepVanishing) && mark.matches(access, stack)) { found.add(mark); }
        }
        return found;
    }

    static boolean vanishing(ItemStack stack) { return EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP); }

    static void roll(HolderLookup.Provider access, List<SoulboundMark> marks, Predicate<SoulboundMark> which, ItemStack stack, RandomSource random) {
        for (SoulboundMark mark : marks) {
            if (which.test(mark)) { mark.lose(access, stack, random); }
        }
    }

    public static boolean marked(HolderLookup.Provider access, ItemStack stack) { return !matching(access, stack).isEmpty(); }

    public static List<String> tooltips(HolderLookup.Provider access, ItemStack stack) {
        List<String> lines = new ArrayList<>();
        for (SoulboundMark mark : matching(access, stack)) {
            if (!mark.tooltip.isEmpty() && !lines.contains(mark.tooltip)) { lines.add(mark.tooltip); }
        }
        return lines;
    }
}
