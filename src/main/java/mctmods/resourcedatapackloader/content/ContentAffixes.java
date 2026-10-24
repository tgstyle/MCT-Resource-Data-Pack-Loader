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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public final class ContentAffixes {
    public static final String TAG = "rdplAffix";
    public static final String AFFIXES = "Affixes";
    public static final String SOCKETS = "Sockets";
    public static final String NAME = "Name";
    public static final String GEM = "Gem";
    private static final String ID = "Id";
    private static final String ROLLED = "Rolled";
    private static final int COMPOUND = 10;
    private static final int POLL = 20;
    private static final int NO_SOCKET = -1;
    private static final List<AffixDef> DEFS = new ArrayList<>();
    private static final List<GemDef> GEMS = new ArrayList<>();

    private ContentAffixes() {}

    public static boolean load() {
        DEFS.clear();
        GEMS.clear();
        if (!Config.data.affixes) { return false; }
        Json.eachObject(PackManager.AFFIXES, "affix file", ContentAffixes::readAffix);
        Json.eachObject(PackManager.GEMS, "gem file", ContentAffixes::readGem);
        if (!DEFS.isEmpty() || !GEMS.isEmpty()) { Summary.info("affixes", "Loaded " + DEFS.size() + " affix file(s) and " + GEMS.size() + " gem(s)"); }
        return !DEFS.isEmpty();
    }

    public static boolean sockets() { return !GEMS.isEmpty(); }

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
        DEFS.add(new AffixDef(stack, range(json, "rolls", 1), range(json, "sockets", 0), entries));
    }

    @Nullable private static AffixEntry readEntry(ResourceLocation key, JsonObject json) {
        IAttribute attribute = ContentAttributes.find(Json.text(json, "attribute", ""), key);
        List<EntityEquipmentSlot> slots = StackModifiers.slots(key, Json.text(json, "slot", StackModifiers.ANY));
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
        IAttribute attribute = ContentAttributes.find(Json.text(json, "attribute", ""), key);
        List<EntityEquipmentSlot> slots = StackModifiers.slots(key, Json.text(json, "slot", StackModifiers.ANY));
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

    @SubscribeEvent public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (event.phase != TickEvent.Phase.END || player == null || player.world == null || player.world.isRemote || player.ticksExisted % POLL != 0 || DEFS.isEmpty()) { return; }
        roll(player.inventory.mainInventory, player.getRNG());
        roll(player.inventory.armorInventory, player.getRNG());
        roll(player.inventory.offHandInventory, player.getRNG());
    }

    private static void roll(List<ItemStack> stacks, Random random) {
        for (ItemStack stack : stacks) {
            if (stack.isEmpty() || stack.getSubCompound(TAG) != null) { continue; }
            for (AffixDef def : DEFS) {
                if (!ContentStacks.matches(stack, def.item.getItem(), def.item.getMetadata())) { continue; }
                rollOn(stack, def, random);
                break;
            }
        }
    }

    private static void rollOn(ItemStack stack, AffixDef def, Random random) {
        List<AffixEntry> pool = new ArrayList<>(def.affixes);
        int rolls = Math.min(pool.size(), def.minRolls + random.nextInt(def.maxRolls - def.minRolls + 1));
        int sockets = def.minSockets + random.nextInt(def.maxSockets - def.minSockets + 1);
        NBTTagList chosen = new NBTTagList();
        for (int at = 0; at < rolls; at++) {
            AffixEntry entry = pick(pool, random);
            pool.remove(entry);
            String id = "rdpl_affix_" + at;
            double amount = Math.round((entry.min + random.nextDouble() * (entry.max - entry.min)) * 100.0D) / 100.0D;
            StackModifiers.add(stack, id, entry.attribute, amount, entry.operation, entry.slots, "rdpl:affix:" + at);
            NBTTagCompound held = new NBTTagCompound();
            held.setString(NAME, entry.name);
            held.setString(ID, id);
            chosen.appendTag(held);
        }
        NBTTagList open = new NBTTagList();
        for (int at = 0; at < sockets; at++) {
            NBTTagCompound held = new NBTTagCompound();
            held.setString(GEM, "");
            held.setString(NAME, "");
            open.appendTag(held);
        }
        NBTTagCompound mark = new NBTTagCompound();
        mark.setBoolean(ROLLED, true);
        mark.setTag(AFFIXES, chosen);
        mark.setTag(SOCKETS, open);
        stack.setTagInfo(TAG, mark);
        ContentLog.LOGGER.info("Rolled {} affix(es) and {} socket(s) on {}", rolls, sockets, stack);
    }

    private static AffixEntry pick(List<AffixEntry> pool, Random random) {
        int total = 0;
        for (AffixEntry entry : pool) { total += entry.weight; }
        int left = random.nextInt(total);
        for (AffixEntry entry : pool) {
            left -= entry.weight;
            if (left < 0) { return entry; }
        }
        return pool.get(pool.size() - 1);
    }

    @Nullable private static GemDef gemOf(ItemStack right) {
        for (GemDef gem : GEMS) {
            if (ContentStacks.matches(right, gem.item.getItem(), gem.item.getMetadata())) { return gem; }
        }
        return null;
    }

    static boolean socket(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (GEMS.isEmpty() || left.isEmpty() || right.isEmpty()) { return false; }
        NBTTagCompound mark = left.getSubCompound(TAG);
        GemDef gem = mark == null ? null : gemOf(right);
        if (gem == null) { return false; }
        int open = emptySocket(mark.getTagList(SOCKETS, COMPOUND));
        if (open == NO_SOCKET) { return false; }
        ItemStack output = left.copy();
        NBTTagCompound filled = mark.copy();
        NBTTagCompound socket = filled.getTagList(SOCKETS, COMPOUND).getCompoundTagAt(open);
        socket.setString(GEM, String.valueOf(right.getItem().getRegistryName()));
        socket.setString(NAME, gem.name);
        output.setTagInfo(TAG, filled);
        StackModifiers.add(output, "rdpl_socket_" + open, gem.attribute, gem.amount, gem.operation, gem.slots, "rdpl:socket:" + open);
        EntityPlayer worker = AnvilRules.worker();
        int cap = AnvilCosts.cap(worker != null && worker.world.isRemote);
        event.setOutput(output);
        event.setCost(MathHelper.clamp(gem.levels, 1, Math.max(1, cap - 1)));
        event.setMaterialCost(1);
        return true;
    }

    private static int emptySocket(NBTTagList sockets) {
        for (int at = 0; at < sockets.tagCount(); at++) {
            if (sockets.getCompoundTagAt(at).getString(GEM).isEmpty()) { return at; }
        }
        return NO_SOCKET;
    }
}
