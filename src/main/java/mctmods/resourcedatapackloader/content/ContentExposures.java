package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.ExposureDef;
import mctmods.resourcedatapackloader.content.def.ExposureLevelDef;
import mctmods.resourcedatapackloader.content.def.PotionEffectDef;
import mctmods.resourcedatapackloader.util.world.GenHeights;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.ExposureSpread;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentExposures {
    private static final Map<ExposureDef, Map<Block, Integer>> BLOCK_LEVELS = new IdentityHashMap<>();
    private static final Map<ExposureDef, Map<Item, Integer>> ITEM_LEVELS = new IdentityHashMap<>();
    private static final Map<ExposureDef, DamageSource> SOURCES = new IdentityHashMap<>();
    private static final Map<ExposureDef, Potion[]> MARKERS = new IdentityHashMap<>();
    private static final Map<PotionEffectDef, Potion> EXTRAS = new IdentityHashMap<>();
    private static final Map<ExposureDef, Potion> IMMUNITIES = new IdentityHashMap<>();

    private ContentExposures() {}

    public static boolean enabled() { return !ContentRegistry.exposures().isEmpty(); }

    public static boolean spreads() {
        for (ExposureDef def : ContentRegistry.exposures()) {
            if (def.spreads()) { return true; }
        }
        return false;
    }

    @SubscribeEvent public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (event.phase != TickEvent.Phase.END || player == null || player.world == null || player.world.isRemote) { return; }
        for (ExposureDef def : ContentRegistry.exposures()) { tick(def, player); }
    }

    private static void tick(ExposureDef def, EntityPlayer player) {
        if (def.skipsCreative && (player.isCreative() || player.isSpectator())) {
            applyLevel(def, player, 0);
            return;
        }
        if (player.ticksExisted % def.scanInterval == 0) {
            int level = scan(def, player);
            applyLevel(def, player, level);
            spreadFrom(def, player, level);
        }
        damageTick(def, player);
    }

    private static void tickMob(ExposureDef def, EntityLivingBase mob) {
        boolean catches = ExposureSpread.catches(def, mob);
        if (!catches && ExposureSpread.carrierLevel(def, mob) <= 0) { return; }
        if (mob.ticksExisted % def.scanInterval == 0) {
            int level = catches && !immune(def, mob) ? Math.min(def.levels.size(), ExposureSpread.caughtLevel(def, mob)) : 0;
            if (catches) { applyLevel(def, mob, level); }
            spreadFrom(def, mob, level);
        }
        if (catches) { damageTick(def, mob); }
    }

    private static void spreadFrom(ExposureDef def, EntityLivingBase entity, int level) {
        if (!def.spreads()) { return; }
        int spreading = Math.max(ExposureSpread.carrierLevel(def, entity), def.contagious ? level : 0);
        ExposureSpread.spread(def, entity, Math.min(def.levels.size(), spreading));
    }

    private static int scan(ExposureDef def, EntityPlayer player) {
        if (immune(def, player)) { return 0; }
        int most = def.levels.size();
        int level = Math.max(def.dimensions.getOrDefault(player.dimension, 0), scanItems(def, player, most));
        level = Math.max(level, Math.max(ExposureSpread.weatherLevel(def, player), ExposureSpread.caughtLevel(def, player)));
        if (level >= most) { return most; }
        return Math.max(level, scanWorld(def, player, most));
    }

    private static boolean immune(ExposureDef def, EntityLivingBase entity) {
        if (def.immunity.isEmpty()) { return false; }
        Potion immune = immunity(def);
        return immune != null && entity.isPotionActive(immune);
    }

    @Nullable private static Potion immunity(ExposureDef def) {
        if (IMMUNITIES.containsKey(def)) { return IMMUNITIES.get(def); }
        Potion potion = Potion.getPotionFromResourceLocation(def.immunity);
        IMMUNITIES.put(def, potion);
        return potion;
    }

    private static int scanItems(ExposureDef def, EntityPlayer player, int most) {
        Map<Item, Integer> levels = itemLevels(def);
        if (levels.isEmpty()) { return 0; }
        int level = scanList(levels, player.inventory.mainInventory, 0, most);
        if (level >= most) { return level; }
        level = scanList(levels, player.inventory.offHandInventory, level, most);
        if (level >= most) { return level; }
        return scanList(levels, player.inventory.armorInventory, level, most);
    }

    private static int scanList(Map<Item, Integer> levels, List<ItemStack> list, int level, int most) {
        for (ItemStack stack : list) {
            if (stack.isEmpty()) { continue; }
            Integer found = levels.get(stack.getItem());
            if (found != null && found > level) { level = found; }
            if (level >= most) { return level; }
        }
        return level;
    }

    private static int scanWorld(ExposureDef def, EntityPlayer player, int most) {
        Map<Block, Integer> levels = blockLevels(def);
        int radius = def.range;
        if (levels.isEmpty() || radius <= 0) { return 0; }
        World world = player.world;
        int radiusSq = radius * radius;
        int centerX = MathHelper.floor(player.posX);
        int centerY = MathHelper.floor(player.posY);
        int centerZ = MathHelper.floor(player.posZ);
        int lowest = Math.max(GenHeights.floor(world, 0), centerY - radius);
        int highest = Math.min(GenHeights.ceiling(world, 256) - 1, centerY + radius);
        int level = 0;
        int sources = 0;
        for (int chunkX = centerX - radius >> 4; chunkX <= centerX + radius >> 4; chunkX++) {
            for (int chunkZ = centerZ - radius >> 4; chunkZ <= centerZ + radius >> 4; chunkZ++) {
                Chunk chunk = world.getChunkProvider().getLoadedChunk(chunkX, chunkZ);
                if (chunk == null) { continue; }
                int fromX = Math.max(centerX - radius, chunkX << 4);
                int toX = Math.min(centerX + radius, (chunkX << 4) + 15);
                int fromZ = Math.max(centerZ - radius, chunkZ << 4);
                int toZ = Math.min(centerZ + radius, (chunkZ << 4) + 15);
                for (int y = lowest; y <= highest; y++) {
                    int offY = (y - centerY) * (y - centerY);
                    for (int x = fromX; x <= toX; x++) {
                        int offX = (x - centerX) * (x - centerX);
                        for (int z = fromZ; z <= toZ; z++) {
                            int offZ = (z - centerZ) * (z - centerZ);
                            if (offX + offY + offZ > radiusSq) { continue; }
                            Integer found = levels.get(chunk.getBlockState(x & 15, y, z & 15).getBlock());
                            if (found == null || found <= 0) { continue; }
                            if (found > level) { level = found; }
                            if (level >= most) { return most; }
                            sources++;
                        }
                    }
                }
            }
        }
        if (level > 0 && level < most && def.sourcesForNextLevel > 0 && sources >= def.sourcesForNextLevel) { return level + 1; }
        return level;
    }

    private static void applyLevel(ExposureDef def, EntityLivingBase living, int level) {
        int duration = def.scanInterval * 2 + 20;
        Potion[] markers = markers(def);
        for (int index = 0; index < markers.length; index++) {
            if (index + 1 == level) { continue; }
            if (markers[index] != null) { living.removePotionEffect(markers[index]); }
        }
        if (level <= 0) { return; }
        ExposureLevelDef entry = def.levels.get(level - 1);
        Potion marker = markers[level - 1];
        if (marker == null) { return; }
        living.addPotionEffect(new PotionEffect(marker, duration, 0, false, true));
        for (PotionEffectDef extra : entry.extras) {
            Potion potion = extraPotion(extra);
            if (potion == null) { continue; }
            living.addPotionEffect(new PotionEffect(potion, extra.duration > 0 ? extra.duration : duration, extra.amplifier, extra.ambient, extra.showParticles));
        }
    }

    private static void damageTick(ExposureDef def, EntityLivingBase living) {
        ExposureLevelDef active = null;
        Potion[] markers = markers(def);
        for (int index = 0; index < markers.length; index++) {
            if (markers[index] != null && living.isPotionActive(markers[index])) { active = def.levels.get(index); }
        }
        NBTTagCompound data = living.getEntityData();
        String tag = "RDPLExposure" + def.name;
        if (active != null && active.damage > 0.0F && active.damageInterval > 0) {
            int timer = data.getInteger(tag) + 1;
            if (timer >= active.damageInterval) {
                timer = 0;
                living.attackEntityFrom(source(def), active.damage);
            }
            data.setInteger(tag, timer);
        }
        else if (data.hasKey(tag)) { data.setInteger(tag, 0); }
    }

    private static Potion[] markers(ExposureDef def) {
        return MARKERS.computeIfAbsent(def, held -> {
            Potion[] markers = new Potion[held.levels.size()];
            for (int index = 0; index < markers.length; index++) { markers[index] = Potion.getPotionFromResourceLocation(held.levels.get(index).effect); }
            return markers;
        });
    }

    @Nullable private static Potion extraPotion(PotionEffectDef extra) {
        if (EXTRAS.containsKey(extra)) { return EXTRAS.get(extra); }
        Potion potion = Potion.getPotionFromResourceLocation(extra.potion);
        EXTRAS.put(extra, potion);
        return potion;
    }

    private static DamageSource source(ExposureDef def) {
        return SOURCES.computeIfAbsent(def, held -> new DamageSource("rdpl." + held.name).setDamageBypassesArmor().setDamageIsAbsolute());
    }

    private static Map<Block, Integer> blockLevels(ExposureDef def) {
        return BLOCK_LEVELS.computeIfAbsent(def, held -> {
            Map<Block, Integer> levels = new IdentityHashMap<>();
            for (Map.Entry<ResourceLocation, Integer> entry : held.blocks.entrySet()) {
                Block block = resolveBlock(held, entry.getKey());
                if (block != null) { levels.put(block, entry.getValue()); }
            }
            return levels;
        });
    }

    private static Map<Item, Integer> itemLevels(ExposureDef def) {
        return ITEM_LEVELS.computeIfAbsent(def, held -> {
            Map<Item, Integer> levels = new IdentityHashMap<>();
            for (Map.Entry<ResourceLocation, Integer> entry : held.items.entrySet()) {
                Item item = ForgeRegistries.ITEMS.getValue(entry.getKey());
                if (item == null) { ContentLog.LOGGER.error("Exposure {} names item {}, which is not registered, so it is ignored", held.registryName, entry.getKey()); }
                else { levels.put(item, entry.getValue()); }
            }
            return levels;
        });
    }

    @Nullable private static Block resolveBlock(ExposureDef def, ResourceLocation name) {
        if (!ForgeRegistries.BLOCKS.containsKey(name)) {
            ContentLog.LOGGER.error("Exposure {} names block {}, which is not registered, so it is ignored", def.registryName, name);
            return null;
        }
        return ForgeRegistries.BLOCKS.getValue(name);
    }

    public static final class Mobs {
        private Mobs() {}

        @SubscribeEvent public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
            EntityLivingBase living = event.getEntityLiving();
            if (living instanceof EntityPlayer || living.world.isRemote) { return; }
            for (ExposureDef def : ContentRegistry.exposures()) {
                if (def.spreads()) { tickMob(def, living); }
            }
        }
    }
}
