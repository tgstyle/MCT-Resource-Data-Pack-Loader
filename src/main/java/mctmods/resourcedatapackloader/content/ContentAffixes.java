package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.AffixDef;
import mctmods.resourcedatapackloader.content.def.AffixEntry;
import mctmods.resourcedatapackloader.content.def.GemDef;
import mctmods.resourcedatapackloader.content.util.ContentAttributes;
import mctmods.resourcedatapackloader.content.util.StackModifiers;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.VanillaClients;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TickEvent;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class ContentAffixes {
    public static final String TAG = "rdplAffix";
    public static final String AFFIXES = "Affixes";
    public static final String SOCKETS = "Sockets";
    public static final String NAME = "Name";
    public static final String GEM = "Gem";
    private static final String ID = "Id";
    private static final String ROLLED = "Rolled";
    private static final int POLL = 20;
    private static final int NO_SOCKET = -1;
    private static final List<AffixDef> DEFS = new ArrayList<>();
    private static final List<GemDef> GEMS = new ArrayList<>();

    private ContentAffixes() {}

    public static void load() {
        DEFS.clear();
        GEMS.clear();
        if (Config.data.affixesOff()) { return; }
        Json.eachObject(PackManager.AFFIXES, "affix file", ContentAffixes::readAffix);
        Json.eachObject(PackManager.GEMS, "gem file", ContentAffixes::readGem);
        if (!DEFS.isEmpty() || !GEMS.isEmpty()) {
            Summary.info("affixes", "Loaded " + DEFS.size() + " affix file(s) and " + GEMS.size() + " gem(s)");
            VanillaClients.clientDrawn("The affix and gem content", "affix and socket names");
        }
    }

    private static void readAffix(ResourceLocation key, JsonObject json) {
        ItemStack stack = ContentStacks.parse(key, Json.text(json, "item", ""), 1);
        if (stack.isEmpty()) { return; }
        if (stack.getMaxStackSize() > 1) {
            ContentLog.LOGGER.error("Affix file {} names {}, which stacks, and a stack cannot carry rolled values, skipping it", key, Json.text(json, "item", ""));
            return;
        }
        List<AffixEntry> entries = new ArrayList<>();
        for (JsonElement element : Json.getJsonArray(json, "affixes", new JsonArray())) {
            AffixEntry entry = element.isJsonObject() ? readEntry(key, element.getAsJsonObject()) : null;
            if (entry != null) { entries.add(entry); }
        }
        if (entries.isEmpty()) {
            ContentLog.LOGGER.error("Affix file {} lists no usable affix, skipping it", key);
            return;
        }
        int[] rolls = range(json, "rolls", 1);
        int[] sockets = range(json, "sockets", 0);
        DEFS.add(new AffixDef(stack, rolls[0], rolls[1], sockets[0], sockets[1], entries));
    }

    @Nullable private static AffixEntry readEntry(ResourceLocation key, JsonObject json) {
        Attribute attribute = ContentAttributes.find(Json.text(json, "attribute", ""), key);
        List<EquipmentSlot> slots = StackModifiers.slots(key, Json.text(json, "slot", StackModifiers.ANY));
        int operation = Json.getInt(json, "operation", 0);
        if (operation < 0 || operation > 2) {
            ContentLog.LOGGER.error("Affix file {} sets operation to {}, which is not 0, 1 or 2, skipping that affix", key, operation);
            return null;
        }
        if (attribute == null || slots == null) { return null; }
        double min = number(json, "min", 0.0D);
        double max = Math.max(min, number(json, "max", min));
        return new AffixEntry(Json.text(json, "name", ""), attribute, min, max, operation, slots, Math.max(1, Json.getInt(json, "weight", 1)));
    }

    private static void readGem(ResourceLocation key, JsonObject json) {
        ItemStack stack = ContentStacks.parse(key, Json.text(json, "item", ""), 1);
        Attribute attribute = ContentAttributes.find(Json.text(json, "attribute", ""), key);
        List<EquipmentSlot> slots = StackModifiers.slots(key, Json.text(json, "slot", StackModifiers.ANY));
        int operation = Json.getInt(json, "operation", 0);
        if (operation < 0 || operation > 2) {
            ContentLog.LOGGER.error("Gem file {} sets operation to {}, which is not 0, 1 or 2, skipping it", key, operation);
            return;
        }
        if (stack.isEmpty() || attribute == null || slots == null) { return; }
        GEMS.add(new GemDef(stack, Json.text(json, "name", Json.text(json, "item", "")), attribute, number(json, "amount", 0.0D), operation, slots, Math.max(1, Json.getInt(json, "levels", 1))));
    }

    private static double number(JsonObject json, String member, double fallback) { return json.has(member) ? Json.asDouble(json.get(member), member, fallback) : fallback; }

    private static int[] range(JsonObject json, String member, int fallback) {
        JsonObject held = Json.object(json, member);
        int min = Math.max(0, Json.getInt(held, "min", fallback));
        return new int[] {min, Math.max(min, Json.getInt(held, "max", min))};
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase != TickEvent.Phase.END || player == null || player.level().isClientSide || player.tickCount % POLL != 0 || DEFS.isEmpty()) { return; }
        roll(player.getInventory().items, player.getRandom());
        roll(player.getInventory().armor, player.getRandom());
        roll(player.getInventory().offhand, player.getRandom());
    }

    private static void roll(List<ItemStack> stacks, RandomSource random) {
        for (ItemStack stack : stacks) {
            if (stack.isEmpty() || stack.getTagElement(TAG) != null) { continue; }
            for (AffixDef def : DEFS) {
                if (!ContentStacks.matches(stack, def.item())) { continue; }
                rollOn(stack, def, random);
                break;
            }
        }
    }

    private static void rollOn(ItemStack stack, AffixDef def, RandomSource random) {
        List<AffixEntry> pool = new ArrayList<>(def.affixes());
        int rolls = Math.min(pool.size(), def.minRolls() + random.nextInt(def.maxRolls() - def.minRolls() + 1));
        int sockets = def.minSockets() + random.nextInt(def.maxSockets() - def.minSockets() + 1);
        ListTag chosen = new ListTag();
        for (int at = 0; at < rolls; at++) {
            AffixEntry entry = pick(pool, random);
            pool.remove(entry);
            String id = "rdpl_affix_" + at;
            double amount = Math.round((entry.min() + random.nextDouble() * (entry.max() - entry.min())) * 100.0D) / 100.0D;
            StackModifiers.add(stack, id, entry.attribute(), amount, entry.operation(), entry.slots(), "rdpl:affix:" + at);
            CompoundTag held = new CompoundTag();
            held.putString(NAME, entry.name());
            held.putString(ID, id);
            chosen.add(held);
        }
        ListTag open = new ListTag();
        for (int at = 0; at < sockets; at++) {
            CompoundTag held = new CompoundTag();
            held.putString(GEM, "");
            held.putString(NAME, "");
            open.add(held);
        }
        CompoundTag mark = new CompoundTag();
        mark.putBoolean(ROLLED, true);
        mark.put(AFFIXES, chosen);
        mark.put(SOCKETS, open);
        stack.addTagElement(TAG, mark);
        ContentLog.LOGGER.info("Rolled {} affix(es) and {} socket(s) on {}", rolls, sockets, stack);
    }

    private static AffixEntry pick(List<AffixEntry> pool, RandomSource random) {
        int total = 0;
        for (AffixEntry entry : pool) { total += entry.weight(); }
        int left = random.nextInt(total);
        for (AffixEntry entry : pool) {
            left -= entry.weight();
            if (left < 0) { return entry; }
        }
        return pool.get(pool.size() - 1);
    }

    @Nullable private static GemDef gemOf(ItemStack right) {
        for (GemDef gem : GEMS) {
            if (ContentStacks.matches(right, gem.item())) { return gem; }
        }
        return null;
    }

    static boolean socket(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (GEMS.isEmpty() || left.isEmpty() || right.isEmpty()) { return false; }
        CompoundTag mark = left.getTagElement(TAG);
        GemDef gem = mark == null ? null : gemOf(right);
        if (gem == null) { return false; }
        int open = emptySocket(mark.getList(SOCKETS, Tag.TAG_COMPOUND));
        if (open == NO_SOCKET) { return false; }
        ItemStack output = left.copy();
        CompoundTag filled = mark.copy();
        CompoundTag socket = filled.getList(SOCKETS, Tag.TAG_COMPOUND).getCompound(open);
        socket.putString(GEM, ContentStacks.id(right.getItem()));
        socket.putString(NAME, gem.name());
        output.addTagElement(TAG, filled);
        StackModifiers.add(output, "rdpl_socket_" + open, gem.attribute(), gem.amount(), gem.operation(), gem.slots(), "rdpl:socket:" + open);
        int cap = AnvilCosts.cap(event.getPlayer() != null && event.getPlayer().level().isClientSide);
        event.setOutput(output);
        event.setCost(Mth.clamp(gem.levels(), 1, Math.max(1, cap - 1)));
        event.setMaterialCost(1);
        return true;
    }

    private static int emptySocket(ListTag sockets) {
        for (int at = 0; at < sockets.size(); at++) {
            if (sockets.getCompound(at).getString(GEM).isEmpty()) { return at; }
        }
        return NO_SOCKET;
    }
}
