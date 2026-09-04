package mctmods.resourcedatapackloader.content.gate;

import mctmods.resourcedatapackloader.util.Stacks;
import mctmods.resourcedatapackloader.util.Says;
import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.GateDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.PackGeneration;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
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
    private static final ResourceLocation GATE = new ResourceLocation("rdpl", "gate");
    private static final PackGeneration GENERATION = new PackGeneration();

    private ContentGates() {}

    public static void load() {
        if (!GENERATION.stale()) { return; }
        DEFS.clear();
        BY_DIMENSION.clear();
        STACKS.clear();
        if (!Config.content.load) { return; }
        Json.eachFile(PackManager.GATES, "gate definition", (key, contents) -> {
            GateDef def = ContentParser.gate(key, contents);
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

    public static boolean unlocked(EntityPlayer player, GateDef def) {
        if (def.open) { return true; }
        if (!def.hold.isEmpty() && carrying(player, def.hold)) { return true; }
        if (!def.advancement.isEmpty() && earned(player, def.advancement)) { return true; }
        if (def.global) { return GateStorage.unlockedGlobally(player.world, def.getKey()); }
        return GateStorage.unlockedFor(player, def.getKey());
    }

    public static void unlock(EntityPlayer player, GateDef def, boolean announce) {
        if (def.global) { GateStorage.unlockGlobally(player.world, def.getKey()); }
        else { GateStorage.unlockFor(player, def.getKey()); }
        if (!announce || def.unlockedMessage.isEmpty()) { return; }
        String message = def.unlockedMessage.replace("%dim%", def.name).replace("%player%", player.getName());
        if (def.global) { broadcast(player, message); }
        else { Says.line(player, TextFormatting.GREEN, message); }
    }

    public static void lock(EntityPlayer player, GateDef def) {
        if (def.global) { GateStorage.lockGlobally(player.world, def.getKey()); }
        else { GateStorage.lockFor(player, def.getKey()); }
    }

    public static void refuse(EntityPlayer player, GateDef def) {
        if (def.blockedMessage.isEmpty()) { return; }
        String needed = def.consume.isEmpty() ? def.craft : def.consume;
        if (needed.isEmpty()) { needed = def.hold; }
        String message = def.blockedMessage.replace("%dim%", def.name).replace("%item%", describe(needed));
        player.sendStatusMessage(new TextComponentString(TextFormatting.RED + message), true);
    }

    public static boolean carrying(EntityPlayer player, String item) {
        ItemStack wanted = stack(item);
        if (wanted.isEmpty()) { return false; }
        for (ItemStack held : player.inventory.mainInventory) {
            if (matches(held, wanted)) { return true; }
        }
        return matches(player.inventory.offHandInventory.get(0), wanted);
    }

    public static boolean matches(ItemStack found, ItemStack wanted) { return Stacks.matches(wanted, found); }

    private static boolean earned(EntityPlayer player, String name) {
        if (!(player instanceof EntityPlayerMP)) { return false; }
        MinecraftServer server = player.getServer();
        if (server == null) { return false; }
        Advancement advancement = server.getAdvancementManager().getAdvancement(new ResourceLocation(name));
        if (advancement == null) { return false; }
        return ((EntityPlayerMP) player).getAdvancements().getProgress(advancement).isDone();
    }

    private static void broadcast(EntityPlayer player, String message) {
        MinecraftServer server = player.getServer();
        if (server == null) { return; }
        for (EntityPlayerMP online : server.getPlayerList().getPlayers()) { Says.line(online, TextFormatting.GREEN, message); }
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
