package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.WorldgenDef;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.Says;
import mctmods.resourcedatapackloader.util.TemplateMemo;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentProspect {
    private static final String KEY = "prospectItems";
    private static final ResourceLocation ITEMS = new ResourceLocation(ResourceDataPackLoader.MOD_ID, "prospectitems");
    private static final int RADIUS = 8;
    private static final int LEVEL = 6;
    private static final int WEAR = 2;
    private static final String[] POINTS = { "n", "ne", "e", "se", "s", "sw", "w", "nw" };
    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static final TemplateMemo<Boolean> LIVE = new TemplateMemo<>();

    private ContentProspect() {}

    private static final class Entry {
        final Item item;
        final int meta;
        final Set<String> names;
        final int radius;

        Entry(Item item, int meta, Set<String> names, int radius) {
            this.item = item;
            this.meta = meta;
            this.names = names;
            this.radius = radius;
        }

        boolean matches(ItemStack stack) { return ContentStacks.matches(stack, item, meta); }

        boolean reads(WorldgenDef def, boolean blacklist) {
            if (names.isEmpty()) { return !blacklist; }
            String full = def.registryName.toString();
            boolean listed = names.contains(full) || names.contains(def.registryName.getPath());
            return listed != blacklist;
        }
    }

    private static boolean idle() { return !LIVE.get(ContentProspect::load); }

    public static final class Reads {
        public final boolean blacklist;
        public final List<String> labels;
        Reads(boolean blacklist, List<String> labels) {
            this.blacklist = blacklist;
            this.labels = labels;
        }
    }

    @Nullable public static Reads describe(ItemStack stack) {
        if (stack.isEmpty() || idle()) { return null; }
        Entry entry = holding(stack);
        if (entry == null) { return null; }
        boolean blacklist = ContentControl.flag(ContentControl.ORES, "prospectItemsAreBlacklist", Config.worldgen.prospectItemsAreBlacklist);
        List<String> labels = new ArrayList<>();
        for (String name : entry.names) {
            WorldgenDef def = ContentWorldgen.byName(name);
            labels.add(def != null && !def.prospectAs.isEmpty() ? def.prospectAs : name.substring(name.indexOf(':') + 1));
        }
        Collections.sort(labels);
        if (labels.isEmpty() && blacklist) { return null; }
        return new Reads(blacklist, labels);
    }

    @Nullable private static Entry holding(ItemStack stack) {
        for (Entry entry : ENTRIES) { if (entry.matches(stack)) { return entry; } }
        return null;
    }

    private static boolean load() {
        ENTRIES.clear();
        for (String entry : ContentControl.list(ContentControl.ORES, KEY, Config.worldgen.prospectItems)) { parse(entry); }
        if (!ENTRIES.isEmpty()) { ContentLog.LOGGER.debug("{} item(s) prospect for veins when a sneaking player breaks a block with them", ENTRIES.size()); }
        return !ENTRIES.isEmpty();
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
            try { radius = Math.max(1, Math.min(64, Integer.parseInt(parts[1].trim()))); }
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
        ENTRIES.add(new Entry(stack.getItem(), stack.getMetadata(), Collections.unmodifiableSet(names), radius));
    }

    @SubscribeEvent public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (!event.getEntityPlayer().isSneaking() || idle() || holding(event.getEntityPlayer().getHeldItemMainhand()) == null) { return; }
        int slow = Math.max(1, ContentControl.number(ContentControl.ORES, "prospectSlow", Config.worldgen.prospectSlow));
        if (slow > 1) { event.setNewSpeed(event.getOriginalSpeed() / slow); }
    }

    @SubscribeEvent public static void onBreak(BlockEvent.BreakEvent event) {
        if (event.getWorld().isRemote || !(event.getPlayer() instanceof EntityPlayerMP) || !event.getPlayer().isSneaking() || idle()) { return; }
        EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();
        ItemStack held = player.getHeldItemMainhand();
        Entry entry = holding(held);
        if (entry == null) { return; }
        read(player, event.getWorld(), event.getPos(), entry);
        int wear = Math.max(WEAR, ContentControl.number(ContentControl.ORES, "prospectWear", Config.worldgen.prospectWear));
        if (held.isItemStackDamageable()) { held.damageItem(wear - 1, player); }
        if (!drops()) { event.setExpToDrop(0); }
    }

    private static boolean drops() { return ContentControl.flag(ContentControl.ORES, "prospectDrops", Config.worldgen.prospectDrops); }

    @SubscribeEvent public static void onHarvest(BlockEvent.HarvestDropsEvent event) {
        EntityPlayer harvester = event.getHarvester();
        if (harvester == null || event.getWorld().isRemote || !harvester.isSneaking() || idle() || drops() || holding(harvester.getHeldItemMainhand()) == null) { return; }
        event.getDrops().clear();
        event.setDropChance(0.0F);
    }

    private static void read(EntityPlayerMP player, World world, BlockPos at, Entry entry) {
        boolean blacklist = ContentControl.flag(ContentControl.ORES, "prospectItemsAreBlacklist", Config.worldgen.prospectItemsAreBlacklist);
        int chunkX = at.getX() >> 4;
        int chunkZ = at.getZ() >> 4;
        int said = 0;
        for (String name : ContentWorldgen.veinNames()) {
            WorldgenDef def = ContentWorldgen.byName(name);
            if (def == null || !(def.getShape() instanceof ContentOreVein) || !entry.reads(def, blacklist)) { continue; }
            ContentOreVein shape = (ContentOreVein) def.getShape();
            ContentOreVein.Vein nearest = null;
            double best = Double.MAX_VALUE;
            for (int cx = chunkX - entry.radius; cx <= chunkX + entry.radius; cx++) {
                for (int cz = chunkZ - entry.radius; cz <= chunkZ + entry.radius; cz++) {
                    for (ContentOreVein.Vein vein : shape.veinsOf(world, cx, cz)) {
                        double away = at.distanceSq(vein.x, vein.y, vein.z);
                        if (away >= best || !ContentWorldgen.allowsAt(def, world, new BlockPos(vein.x, vein.y, vein.z))) { continue; }
                        best = away;
                        nearest = vein;
                    }
                }
            }
            if (nearest == null) { continue; }
            String ore = def.prospectAs.isEmpty() ? def.registryName.getPath() : def.prospectAs;
            int dy = nearest.y - at.getY();
            String height = Lang.tr(player, dy > LEVEL ? "rdpl.prospect.above" : dy < -LEVEL ? "rdpl.prospect.below" : "rdpl.prospect.level");
            int dx = nearest.x - at.getX();
            int dz = nearest.z - at.getZ();
            String line = dx * dx + dz * dz <= ContentOreVein.REACH * ContentOreVein.REACH
                    ? Lang.tr(player, "rdpl.prospect.here", ore, height)
                    : Lang.tr(player, "rdpl.prospect.hit", ore, Lang.tr(player, "rdpl.dir." + point(dx, dz)), height);
            Says.tell(player, line, TextFormatting.YELLOW);
            said++;
        }
        if (said == 0) { Says.tell(player, Lang.tr(player, "rdpl.prospect.none"), TextFormatting.GRAY); }
    }

    private static String point(int dx, int dz) {
        double angle = Math.toDegrees(Math.atan2(dx, -dz));
        int slice = (int) Math.floor(((angle + 360.0 + 22.5) % 360.0) / 45.0);
        return POINTS[slice].toLowerCase(Locale.ROOT);
    }
}
