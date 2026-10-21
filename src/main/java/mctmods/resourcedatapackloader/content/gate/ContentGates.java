package mctmods.resourcedatapackloader.content.gate;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.card.CardIds;
import mctmods.resourcedatapackloader.util.Stacks;
import mctmods.resourcedatapackloader.util.Says;
import mctmods.resourcedatapackloader.content.ContentParserWorlds;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.GateDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.PackGeneration;
import mctmods.resourcedatapackloader.util.Standing;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentGates {
    private static final Map<ResourceLocation, GateDef> DEFS = new LinkedHashMap<>();
    private static final Map<Integer, List<GateDef>> BY_DIMENSION = new LinkedHashMap<>();
    private static final Map<ResourceLocation, GateDef> ALL = Collections.unmodifiableMap(DEFS);
    private static final Map<String, ItemStack> STACKS = new HashMap<>();
    private static final ResourceLocation GATE = ResourceDataPackLoader.id("gate");
    private static final PackGeneration GENERATION = new PackGeneration();

    private ContentGates() {}

    public static void load() {
        if (!GENERATION.stale()) { return; }
        DEFS.clear();
        BY_DIMENSION.clear();
        STACKS.clear();
        if (!Config.content.load) { return; }
        Json.eachFile(PackManager.GATES, "gate definition", (key, contents) -> {
            GateDef def = ContentParserWorlds.gate(key, contents);
            if (def != null) { DEFS.put(key, def); }
        });
        for (Map.Entry<ResourceLocation, GateDef> entry : DEFS.entrySet()) {
            GateDef def = entry.getValue();
            if (!ContentRegistry.available(def.requires, entry.getKey())) { continue; }
            BY_DIMENSION.computeIfAbsent(def.dimension, k -> new ArrayList<>()).add(def);
        }
        if (!BY_DIMENSION.isEmpty()) { Summary.info("gates", "Guarding " + BY_DIMENSION.size() + " dimension(s) behind " + count() + " gate(s)"); }
    }

    public static boolean enabled() { return !BY_DIMENSION.isEmpty(); }

    public static List<GateDef> forDimension(int dimension) { return BY_DIMENSION.getOrDefault(dimension, Collections.emptyList()); }

    public static Map<ResourceLocation, GateDef> all() { return ALL; }

    @Nullable public static GateDef find(String key) {
        for (GateDef def : DEFS.values()) {
            if (def.getKey().equals(key) || def.registryName.getPath().equals(key)) { return def; }
        }
        return null;
    }

    public static boolean unlocked(EntityPlayer player, GateDef def) { return openFor(player, def) || def.standing.earnedBy(player); }

    public static boolean admits(EntityPlayer player, GateDef def) {
        if (openFor(player, def)) { return true; }
        if (!def.standing.earnedBy(player)) { return false; }
        unlock(player, def, true);
        return true;
    }

    private static boolean openFor(EntityPlayer player, GateDef def) {
        if (def.open) { return true; }
        if (!def.hold.isEmpty() && carrying(player, def.hold)) { return true; }
        if (!def.advancement.isEmpty() && earned(player, def.advancement)) { return true; }
        return GateStorage.unlocked(player, def);
    }

    public static void unlock(EntityPlayer player, GateDef def, boolean announce) {
        GateStorage.unlock(player, def);
        if (!announce || def.unlockedMessage.isEmpty()) { return; }
        String message = def.unlockedMessage.replace("%dim%", def.name).replace("%player%", player.getName());
        String team = GateStorage.teamFor(player, def);
        if (def.global() || !team.isEmpty()) { broadcast(player, message, team); }
        else { Says.line(player, CardIds.GATE_UNLOCKED, TextFormatting.GREEN, message); }
    }

    public static void lock(EntityPlayer player, GateDef def) { GateStorage.lock(player, def); }

    public static void refuse(EntityPlayer player, GateDef def) {
        if (def.blockedMessage.isEmpty()) { return; }
        String needed = def.consume.isEmpty() ? def.craft : def.consume;
        if (needed.isEmpty()) { needed = def.hold; }
        String message = def.blockedMessage.replace("%dim%", def.name).replace("%item%", describe(needed));
        Says.bar(player, CardIds.GATE_BLOCKED, TextFormatting.RED, message);
    }

    public static boolean carrying(EntityPlayer player, String item) {
        ItemStack wanted = stack(item);
        if (wanted.isEmpty()) { return false; }
        for (ItemStack held : player.inventory.mainInventory) {
            if (Stacks.matches(wanted, held)) { return true; }
        }
        return Stacks.matches(wanted, player.inventory.offHandInventory.get(0));
    }

    private static boolean earned(EntityPlayer player, String name) { return mctmods.resourcedatapackloader.util.Advancements.has(player, name); }

    private static void broadcast(EntityPlayer player, String message, String team) {
        MinecraftServer server = player.getServer();
        if (server == null) { return; }
        for (EntityPlayerMP online : server.getPlayerList().getPlayers()) {
            if (team.isEmpty() || team.equals(Standing.teamOf(online))) { Says.line(online, CardIds.GATE_UNLOCKED, TextFormatting.GREEN, message); }
        }
    }

    private static String describe(String item) {
        if (item.isEmpty()) { return "something"; }
        ItemStack stack = stack(item);
        return stack.isEmpty() ? item : stack.getDisplayName();
    }

    public static ItemStack stack(String item) { return STACKS.computeIfAbsent(item, held -> ContentStacks.parse(GATE, held, 1)); }

    private static int count() {
        int total = 0;
        for (List<GateDef> defs : BY_DIMENSION.values()) { total += defs.size(); }
        return total;
    }
}
