package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.Says;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentProspect {
    private static final String KEY = "prospectItems";
    private static final ResourceLocation ITEMS = ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "prospectitems");
    private static final int RADIUS = 8;
    private static final int LEVEL = 6;
    private static final int WEAR = 2;
    private static final String[] POINTS = { "n", "ne", "e", "se", "s", "sw", "w", "nw" };
    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static List<String> loadedFrom = null;

    private ContentProspect() {}

    private record Entry(ItemStack stack, Set<String> names, int radius) {
        boolean matches(ItemStack held) { return ContentStacks.matches(held, stack); }

        boolean reads(ResourceLocation key, boolean blacklist) {
            if (names.isEmpty()) { return !blacklist; }
            boolean listed = names.contains(key.toString()) || names.contains(key.getPath());
            return listed != blacklist;
        }
    }

    public record Reads(boolean blacklist, List<String> labels) {}

    private static synchronized boolean idle() {
        List<String> asked = ContentControl.list(ContentControl.ORES, KEY, Config.worldgen.prospectItems());
        if (!asked.equals(loadedFrom)) {
            ENTRIES.clear();
            for (String entry : asked) { parse(entry); }
            loadedFrom = new ArrayList<>(asked);
            if (!ENTRIES.isEmpty()) { ContentLog.LOGGER.debug("{} item(s) prospect for veins when a sneaking player breaks a block with them", ENTRIES.size()); }
        }
        return ENTRIES.isEmpty();
    }

    private static void parse(String entry) {
        String[] sides = entry.split("=", 2);
        if (sides.length < 2) {
            ContentLog.LOGGER.error("{} entry '{}' needs the form item=entry|entry[,radius] or item=*[,radius], skipping it", KEY, entry);
            return;
        }
        String[] parts = sides[1].split(",");
        int radius = RADIUS;
        if (parts.length > 1) {
            try { radius = Math.clamp(Integer.parseInt(parts[1].trim()), 1, 64); }
            catch (NumberFormatException bad) { ContentLog.LOGGER.error("{} entry '{}' has a radius that is not a number, using {}", KEY, entry, RADIUS); }
        }
        Set<String> names = new HashSet<>();
        for (String name : parts[0].split("\\|")) {
            String trimmed = name.trim();
            if (trimmed.isEmpty() || "*".equals(trimmed)) { continue; }
            names.add(trimmed);
        }
        ItemStack stack = ContentStacks.parse(ITEMS, sides[0].trim(), 1);
        if (stack.isEmpty()) { return; }
        ENTRIES.add(new Entry(stack, Collections.unmodifiableSet(names), radius));
    }

    @Nullable private static Entry holding(ItemStack stack) {
        for (Entry entry : ENTRIES) { if (entry.matches(stack)) { return entry; } }
        return null;
    }

    private static boolean blacklist() { return ContentControl.flag(ContentControl.ORES, "prospectItemsAreBlacklist", Config.worldgen.prospectItemsAreBlacklist()); }

    private static boolean drops() { return ContentControl.flag(ContentControl.ORES, "prospectDrops", Config.worldgen.prospectDrops()); }

    @Nullable public static Reads describe(ItemStack stack) {
        if (stack.isEmpty() || idle()) { return null; }
        Entry entry = holding(stack);
        if (entry == null) { return null; }
        boolean blacklist = blacklist();
        List<String> labels = new ArrayList<>();
        for (String name : entry.names()) {
            ContentWorldgen.Entry found = ContentWorldgen.byName(name);
            labels.add(found != null && !found.def().prospectAs().isEmpty() ? found.def().prospectAs() : name.substring(name.indexOf(':') + 1));
        }
        Collections.sort(labels);
        if (labels.isEmpty() && blacklist) { return null; }
        return new Reads(blacklist, labels);
    }

    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (!player.isShiftKeyDown() || idle() || holding(player.getMainHandItem()) == null) { return; }
        int slow = Math.max(1, ContentControl.number(ContentControl.ORES, "prospectSlow", Config.worldgen.prospectSlow()));
        if (slow > 1) { event.setNewSpeed(event.getOriginalSpeed() / slow); }
    }

    public static void onBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player) || !(event.getLevel() instanceof ServerLevel level) || !player.isShiftKeyDown() || idle()) { return; }
        ItemStack held = player.getMainHandItem();
        Entry entry = holding(held);
        if (entry == null) { return; }
        read(player, level, event.getPos(), entry);
        int wear = Math.max(WEAR, ContentControl.number(ContentControl.ORES, "prospectWear", Config.worldgen.prospectWear()));
        if (held.isDamageableItem()) { held.hurtAndBreak(wear - 1, player, EquipmentSlot.MAINHAND); }
        if (drops()) { return; }
        event.setCanceled(true);
        level.destroyBlock(event.getPos(), false, player);
    }

    private static void read(ServerPlayer player, ServerLevel level, BlockPos at, Entry entry) {
        boolean blacklist = blacklist();
        int chunkX = at.getX() >> 4;
        int chunkZ = at.getZ() >> 4;
        int said = 0;
        for (String name : ContentWorldgen.veinNames()) {
            ContentWorldgen.Entry found = ContentWorldgen.byName(name);
            if (found == null || !(found.shape() instanceof ContentOreVein shape) || !entry.reads(found.def().key(), blacklist) || !ContentWorldgen.dimensionAllows(found, level)) { continue; }
            ContentOreVein.Vein nearest = null;
            double best = Double.MAX_VALUE;
            for (int cx = chunkX - entry.radius(); cx <= chunkX + entry.radius(); cx++) {
                for (int cz = chunkZ - entry.radius(); cz <= chunkZ + entry.radius(); cz++) {
                    for (ContentOreVein.Vein vein : shape.veinsOf(level.getSeed(), level.getMinBuildHeight() + 1, level.getMaxBuildHeight(), cx, cz)) {
                        BlockPos where = vein.pos();
                        double away = at.distSqr(where);
                        if (away >= best || !ContentWorldgen.allows(found, level, where)) { continue; }
                        best = away;
                        nearest = vein;
                    }
                }
            }
            if (nearest == null) { continue; }
            String ore = found.def().prospectAs().isEmpty() ? found.def().key().getPath() : found.def().prospectAs();
            int dy = nearest.y() - at.getY();
            String height = Lang.tr(player, dy > LEVEL ? "rdpl.prospect.above" : dy < -LEVEL ? "rdpl.prospect.below" : "rdpl.prospect.level");
            int dx = nearest.x() - at.getX();
            int dz = nearest.z() - at.getZ();
            String line = dx * dx + dz * dz <= ContentOreVein.REACH * ContentOreVein.REACH
                    ? Lang.tr(player, "rdpl.prospect.here", ore, height)
                    : Lang.tr(player, "rdpl.prospect.hit", ore, Lang.tr(player, "rdpl.dir." + point(dx, dz)), height);
            Says.tell(player, line, ChatFormatting.YELLOW);
            said++;
        }
        if (said == 0) { Says.tell(player, Lang.tr(player, "rdpl.prospect.none"), ChatFormatting.GRAY); }
    }

    private static String point(int dx, int dz) {
        double angle = Math.toDegrees(Math.atan2(dx, -dz));
        int slice = (int) Math.floor(((angle + 360.0 + 22.5) % 360.0) / 45.0);
        return POINTS[slice].toLowerCase(Locale.ROOT);
    }
}
