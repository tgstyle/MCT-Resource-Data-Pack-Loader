package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.WorldTemplateDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldTemplates;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Says;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public final class ContentThreat {
    private static final int SAMPLE = 100;
    private static final double REACH = 128.0D * 128.0D;
    private static final ResourceLocation KEY = new ResourceLocation(ResourceDataPackLoader.MOD_ID, "threatitems");
    private static final Map<UUID, Integer> BANDS = new HashMap<>();
    private static final Map<World, List<Carrier>> CARRIERS = new HashMap<>();
    private static final Map<World, Integer> OTHERS = new HashMap<>();
    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static final Map<Integer, String> SAYS = new HashMap<>();
    @Nullable private static WorldTemplateDef readFrom;
    private static boolean read;
    private static int[] levels = new int[0];
    private static int most;
    private static float spawnRate;
    private static float notice;

    private ContentThreat() {}

    private static final class Entry {
        final Item item;
        final int meta;
        final int level;
        final int count;
        final boolean batch;

        Entry(Item item, int meta, int level, int count, boolean batch) {
            this.item = item;
            this.meta = meta;
            this.level = level;
            this.count = count;
            this.batch = batch;
        }

        boolean matches(ItemStack stack) { return stack.getItem() == item && (meta == OreDictionary.WILDCARD_VALUE || stack.getMetadata() == meta); }
    }

    private static final class Carrier {
        final Entity entity;
        final int band;

        Carrier(Entity entity, int band) {
            this.entity = entity;
            this.band = band;
        }
    }

    private static boolean live() {
        WorldTemplateDef active = ContentWorldTemplates.active();
        if (read && active == readFrom) { return levels.length > 0 && !ENTRIES.isEmpty(); }
        readFrom = active;
        read = true;
        ENTRIES.clear();
        SAYS.clear();
        levels = ContentControl.numbers(ContentControl.SPAWNING, "threatLevels", Config.worldgen.threatLevels);
        most = ContentControl.number(ContentControl.SPAWNING, "threatMost", Config.worldgen.threatMost);
        spawnRate = Math.max(0.0F, ContentControl.decimal(ContentControl.SPAWNING, "threatSpawnRate", Config.worldgen.threatSpawnRate));
        notice = Math.max(0.0F, ContentControl.decimal(ContentControl.SPAWNING, "threatNotice", Config.worldgen.threatNotice));
        for (int i = 1; i < levels.length; i++) {
            if (levels[i] <= levels[i - 1]) {
                ContentLog.LOGGER.error("threatLevels must rise from one band to the next, {} after {} does not, so the threat level is off", levels[i], levels[i - 1]);
                levels = new int[0];
                break;
            }
        }
        for (String entry : ContentControl.list(ContentControl.SPAWNING, "threatItems", Config.worldgen.threatItems)) { parse(entry); }
        for (String entry : ContentControl.list(ContentControl.SPAWNING, "threatSays", Config.worldgen.threatSays)) {
            String[] parts = entry.split("=", 2);
            if (parts.length < 2) {
                ContentLog.LOGGER.error("threatSays entry '{}' needs the form band=message, skipping it", entry);
                continue;
            }
            try { SAYS.put(Integer.parseInt(parts[0].trim()), parts[1].trim()); }
            catch (NumberFormatException ex) { ContentLog.LOGGER.error("threatSays entry '{}' names band '{}', which is not a number, skipping it", entry, parts[0].trim()); }
        }
        if (levels.length > 0 && !ENTRIES.isEmpty()) { ContentLog.LOGGER.debug("The threat level watches {} item entry/entries over {} band(s), scaling hostile spawns by {} and noticing {} block(s) farther at the top", ENTRIES.size(), levels.length, spawnRate, notice); }
        return levels.length > 0 && !ENTRIES.isEmpty();
    }

    private static void parse(String entry) {
        String[] sides = entry.split("=", 2);
        if (sides.length < 2) {
            ContentLog.LOGGER.error("threatItems entry '{}' needs the form item=level,count[,each|batch], skipping it", entry);
            return;
        }
        String[] parts = sides[1].split(",");
        if (parts.length < 2 || parts.length > 3) {
            ContentLog.LOGGER.error("threatItems entry '{}' needs a level and a count after the =, skipping it", entry);
            return;
        }
        int level;
        int count;
        try {
            level = Integer.parseInt(parts[0].trim());
            count = Integer.parseInt(parts[1].trim());
        }
        catch (NumberFormatException ex) {
            ContentLog.LOGGER.error("threatItems entry '{}' has a level or count that is not a number, skipping it", entry);
            return;
        }
        if (level <= 0 || count <= 0) {
            ContentLog.LOGGER.error("threatItems entry '{}' needs a level and a count above 0, skipping it", entry);
            return;
        }
        boolean batch = false;
        if (parts.length == 3) {
            String mode = parts[2].trim().toLowerCase(java.util.Locale.ROOT);
            if ("batch".equals(mode)) { batch = true; }
            else if (!"each".equals(mode)) {
                ContentLog.LOGGER.error("threatItems entry '{}' ends in '{}', which is neither each nor batch, skipping it", entry, parts[2].trim());
                return;
            }
        }
        ItemStack stack = ContentStacks.parse(KEY, sides[0].trim(), 1);
        if (stack.isEmpty()) { return; }
        ENTRIES.add(new Entry(stack.getItem(), stack.getMetadata(), level, Math.min(count, stack.getMaxStackSize()), batch));
    }

    @SubscribeEvent public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) { return; }
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null || server.getTickCounter() % SAMPLE != 0 || !live()) { return; }
        for (WorldServer world : server.worlds) { sample(world); }
    }

    @SubscribeEvent public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { BANDS.remove(event.player.getUniqueID()); }

    @SubscribeEvent public static void onWorldUnload(WorldEvent.Unload event) {
        CARRIERS.remove(event.getWorld());
        OTHERS.remove(event.getWorld());
    }

    private static void sample(World world) {
        List<Carrier> carriers = new ArrayList<>();
        int others = 0;
        int highest = 0;
        for (Entity entity : world.loadedEntityList) {
            if (entity.isDead) { continue; }
            int score = score(entity);
            int band = 0;
            while (band < levels.length && score >= levels[band]) { band++; }
            if (entity instanceof EntityPlayer) { noteBand((EntityPlayer) entity, score, band); }
            if (band == 0) { continue; }
            carriers.add(new Carrier(entity, band));
            if (entity instanceof EntityPlayer) { continue; }
            others++;
            highest = Math.max(highest, band);
        }
        CARRIERS.put(world, carriers.isEmpty() ? Collections.emptyList() : carriers);
        Integer before = OTHERS.put(world, others);
        if (before == null ? others > 0 : before != others) { ContentLog.LOGGER.debug("Dimension {} holds {} threat carrier(s) beyond the players, the highest in band {} of {}", world.provider.getDimension(), others, highest, levels.length); }
    }

    private static void noteBand(EntityPlayer player, int score, int band) {
        Integer before = BANDS.put(player.getUniqueID(), band);
        if (before != null && before == band) { return; }
        ContentLog.LOGGER.debug("Player {} carries a threat score of {} and stands in band {} of {}", player.getName(), score, band, levels.length);
        if (before == null) { return; }
        String said = SAYS.get(band);
        if (said != null && !said.isEmpty() && player instanceof EntityPlayerMP) { Says.tell((EntityPlayerMP) player, said, TextFormatting.YELLOW); }
    }

    private static int score(Entity entity) {
        int[] held = new int[ENTRIES.size()];
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.isCreative() || player.isSpectator()) { return 0; }
            tally(held, player.inventory.mainInventory);
            tally(held, player.inventory.armorInventory);
            tally(held, player.inventory.offHandInventory);
        }
        else if (entity instanceof EntityItem) { tally(held, ((EntityItem) entity).getItem()); }
        else if (entity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            IItemHandler handler = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if (handler != null) {
                for (int slot = 0; slot < handler.getSlots(); slot++) { tally(held, handler.getStackInSlot(slot)); }
            }
        }
        else if (entity instanceof EntityLivingBase) { tally(held, ((EntityLivingBase) entity).getEquipmentAndArmor()); }
        else { return 0; }
        int score = 0;
        for (int i = 0; i < held.length; i++) {
            if (held[i] <= 0) { continue; }
            Entry entry = ENTRIES.get(i);
            score += entry.batch ? entry.level * (held[i] / entry.count) : entry.level * Math.min(held[i], entry.count);
        }
        return most >= 0 ? Math.min(score, most) : score;
    }

    private static void tally(int[] held, Iterable<ItemStack> stacks) {
        for (ItemStack stack : stacks) { tally(held, stack); }
    }

    private static void tally(int[] held, ItemStack stack) {
        if (stack.isEmpty()) { return; }
        for (int i = 0; i < held.length; i++) {
            if (ENTRIES.get(i).matches(stack)) { held[i] += stack.getCount(); }
        }
    }

    private static int bandNear(World world, double x, double y, double z, double reachSq) {
        List<Carrier> carriers = CARRIERS.get(world);
        if (carriers == null) { return 0; }
        int found = 0;
        for (Carrier carrier : carriers) {
            if (carrier.band <= found || carrier.entity.isDead || carrier.entity.getDistanceSq(x, y, z) > reachSq) { continue; }
            found = carrier.band;
        }
        return found;
    }

    public static float spawnRate(World world, BlockPos pos) {
        if (!live() || spawnRate == 1.0F) { return 1.0F; }
        int band = bandNear(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, REACH);
        if (band == 0) { return 1.0F; }
        return 1.0F + (spawnRate - 1.0F) * band / levels.length;
    }

    public static boolean allowed(Entity entity) {
        int least = ContentEntities.threatLeast(entity);
        if (least <= 0) { return true; }
        if (!live()) { return false; }
        return bandNear(entity.world, entity.posX, entity.posY, entity.posZ, REACH) >= least;
    }

    public static boolean provokes(EntityPlayer player, Entity mob) {
        int least = ContentEntities.threatHostile(mob);
        if (least <= 0) { return true; }
        if (!live()) { return false; }
        Integer band = BANDS.get(player.getUniqueID());
        return band != null && band >= least;
    }

    public static double notice(Entity owner, double base) {
        if (!live() || notice <= 0.0F) { return base; }
        double reach = base + notice;
        int band = bandNear(owner.world, owner.posX, owner.posY, owner.posZ, reach * reach);
        if (band == 0) { return base; }
        return base + notice * band / levels.length;
    }
}
