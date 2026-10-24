package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.util.ItemMatch;
import mctmods.resourcedatapackloader.content.util.TableMath;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.VanillaClients;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentEnchanting {
    private static final String ENCHANTMENT = "enchantment";
    private static final String MATCH = "match";
    private static final String ENCHANTABLE = "enchantable";
    private static final String ENCHANTABILITY = "enchantability";
    private static final String ENCHANTMENTS = "enchantments";
    private static final String SHELF_CAP = "shelfCap";
    private static final String SHELF_POWER = "shelfPower";
    private static final float MOST_SHELF_POWER = 16.0F;
    private static final Map<Enchantment, TableMath.Shape> SHAPES = new HashMap<>();
    private static final List<Fit> FITS = new ArrayList<>();
    private static int shelfCap = TableMath.VANILLA_SHELF_CAP;
    private static float shelfPower = 1.0F;

    private ContentEnchanting() {}

    private static final class Fit {
        private final ItemMatch match;
        @Nullable private final Boolean enchantable;
        private final int enchantability;
        @Nullable private final Set<Enchantment> allowed;

        private Fit(ItemMatch match, @Nullable Boolean enchantable, int enchantability, @Nullable Set<Enchantment> allowed) {
            this.match = match;
            this.enchantable = enchantable;
            this.enchantability = enchantability;
            this.allowed = allowed;
        }
    }

    public static void load() {
        SHAPES.clear();
        FITS.clear();
        shelfCap = TableMath.VANILLA_SHELF_CAP;
        shelfPower = 1.0F;
        AnvilCosts.resetTable();
        if (!Config.data.enchanting) { return; }
        Json.eachFile(PackManager.ENCHANTING, "enchanting file", (key, contents) -> {
            JsonObject json = Json.object(key, contents, "enchanting file");
            if (json == null || !ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
            if (json.has(ENCHANTMENT)) { shape(key, json); }
            else if (json.has(MATCH)) { fit(key, json); }
            else { table(key, json); }
        });
        boolean opening = false;
        for (Fit fit : FITS) { opening |= Boolean.TRUE.equals(fit.enchantable); }
        AnvilCosts.serveOpening(opening);
        if (!SHAPES.isEmpty()) { Summary.info("enchanting", "Loaded table settings for " + SHAPES.size() + " enchantment(s)"); }
        if (!FITS.isEmpty()) { Summary.info("enchanting", "Loaded " + FITS.size() + " enchanting table item rule(s)"); }
        if (AnvilCosts.levels().length > 0) { Summary.info("enchanting", "The enchanting table asks for levels " + Arrays.toString(AnvilCosts.levels())); }
        if (AnvilCosts.servedPaid().length > 0) { Summary.info("enchanting", "The enchanting table takes levels " + Arrays.toString(AnvilCosts.servedPaid())); }
        if (AnvilCosts.servedLapis().length > 0) { Summary.info("enchanting", "The enchanting table takes lapis " + Arrays.toString(AnvilCosts.servedLapis())); }
        if (shelfCap != TableMath.VANILLA_SHELF_CAP || shelfPower != 1.0F) { Summary.info("enchanting", "Bookshelves count up to " + shelfCap + " power at " + shelfPower + " each"); }
    }

    private static void shape(ResourceLocation key, JsonObject json) {
        String named = Json.text(json, ENCHANTMENT, "");
        Enchantment enchantment = named.isEmpty() ? null : ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(named));
        if (enchantment == null) {
            ContentLog.LOGGER.error("Enchanting file {} names the enchantment '{}', which nothing registers, ignoring it", key, named);
            return;
        }
        int least = whole(key, json, "minLevel", 1);
        int most = whole(key, json, "maxLevel", 1);
        if (most > 0 && least > most) {
            ContentLog.LOGGER.error("Enchanting file {} sets minLevel {} above maxLevel {}, so the vanilla levels are used", key, least, most);
            least = 0;
            most = 0;
        }
        SHAPES.put(enchantment, new TableMath.Shape(Json.getBoolean(json, "off", false), whole(key, json, "weight", 1), least, most));
    }

    private static void fit(ResourceLocation key, JsonObject json) {
        ItemMatch match = ItemMatch.parse(key, Json.text(json, MATCH, ""));
        if (match == null) { return; }
        Boolean enchantable = json.has(ENCHANTABLE) ? Json.getBoolean(json, ENCHANTABLE, true) : null;
        Set<Enchantment> allowed = null;
        if (json.has(ENCHANTMENTS)) {
            allowed = new LinkedHashSet<>();
            for (String named : Json.strings(json, ENCHANTMENTS)) {
                Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(named));
                if (enchantment == null) { ContentLog.LOGGER.error("Enchanting file {} lists the enchantment '{}', which nothing registers, skipping it", key, named); }
                else { allowed.add(enchantment); }
            }
        }
        FITS.add(new Fit(match, enchantable, whole(key, json, ENCHANTABILITY, 1), allowed));
    }

    private static void table(ResourceLocation key, JsonObject json) {
        int[] levels = perSlot(key, json, "enchantLevels", 1);
        if (levels != null) { AnvilCosts.serveLevels(levels); }
        int[] paid = perSlot(key, json, "enchantCosts", 0);
        if (paid != null) {
            AnvilCosts.servePaid(paid);
            VanillaClients.clientDrawn("Enchanting file " + key, "enchantCosts");
        }
        int[] lapis = perSlot(key, json, "lapisCosts", 1);
        if (lapis != null) {
            AnvilCosts.serveLapis(lapis);
            VanillaClients.clientDrawn("Enchanting file " + key, "lapisCosts");
        }
        int cap = whole(key, json, SHELF_CAP, 0);
        if (cap >= 0) { shelfCap = cap; }
        if (json.has(SHELF_POWER)) { shelfPower = Json.ranged(json, SHELF_POWER, 1.0F, 0.0F, MOST_SHELF_POWER, key); }
    }

    private static int whole(ResourceLocation key, JsonObject json, String name, int least) {
        if (!json.has(name)) { return -1; }
        JsonElement held = json.get(name);
        if (held.isJsonPrimitive() && held.getAsJsonPrimitive().isNumber() && held.getAsDouble() == Math.rint(held.getAsDouble()) && held.getAsDouble() >= least && held.getAsDouble() <= AnvilCosts.HIGHEST) { return held.getAsInt(); }
        ContentLog.LOGGER.error("Enchanting file {} gives {} the value {}, which is not a whole number from {} to {}, ignoring it", key, name, held, least, AnvilCosts.HIGHEST);
        return -1;
    }

    @Nullable private static int[] perSlot(ResourceLocation key, JsonObject json, String name, int least) {
        if (!json.has(name)) { return null; }
        JsonElement held = json.get(name);
        if (!held.isJsonArray() || held.getAsJsonArray().size() != AnvilCosts.SLOTS) {
            ContentLog.LOGGER.error("Enchanting file {} gives {} something other than a list of {} numbers, one for each enchanting table slot, ignoring it", key, name, AnvilCosts.SLOTS);
            return null;
        }
        int[] values = new int[AnvilCosts.SLOTS];
        for (int slot = 0; slot < AnvilCosts.SLOTS; slot++) {
            JsonElement each = held.getAsJsonArray().get(slot);
            if (!each.isJsonPrimitive() || !each.getAsJsonPrimitive().isNumber() || each.getAsInt() < least || each.getAsInt() > AnvilCosts.HIGHEST) {
                ContentLog.LOGGER.error("Enchanting file {} gives {} the value {} for slot {}, which is not a whole number from {} to {}, ignoring it", key, name, each, slot + 1, least, AnvilCosts.HIGHEST);
                return null;
            }
            values[slot] = each.getAsInt();
        }
        return values;
    }

    @Nullable private static Fit fitOf(ItemStack stack) { return FITS.isEmpty() ? null : ItemMatch.pick(FITS, each -> each.match, stack); }

    public static boolean enchantable(ItemStack stack, boolean vanilla) {
        Fit fit = fitOf(stack);
        return fit == null || fit.enchantable == null ? vanilla : fit.enchantable;
    }

    private static int enchantability(ItemStack stack, @Nullable Fit fit) { return fit != null && fit.enchantability >= 0 ? fit.enchantability : stack.getItem().getItemEnchantability(stack); }

    @Nullable public static Integer level(Random rand, int slot, int power, ItemStack stack) {
        Fit fit = fitOf(stack);
        boolean shelves = shelfCap != TableMath.VANILLA_SHELF_CAP || shelfPower != 1.0F;
        if (!shelves && (fit == null || fit.enchantability < 0)) { return null; }
        int counted = shelfPower == 1.0F ? power : Math.round(power * shelfPower);
        return TableMath.level(rand, slot, counted, enchantability(stack, fit), shelfCap);
    }

    @Nullable public static List<EnchantmentData> rolled(Random rand, int seed, int slot, ItemStack stack, int level) {
        Fit fit = fitOf(stack);
        if (SHAPES.isEmpty() && (fit == null || fit.enchantability < 0 && fit.allowed == null)) { return null; }
        return TableMath.forSlot(rand, seed, slot, stack, level, enchantability(stack, fit), fit == null ? null : fit.allowed, SHAPES);
    }
}
