package mctmods.resourcedatapackloader.common.exposures;

import mctmods.resourcedatapackloader.common.cards.CardEvents;
import mctmods.resourcedatapackloader.common.pack.definitions.effects.PotionEffectDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.ExposureDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.ExposureLevelDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.scoring.ContentTeams;
import mctmods.resourcedatapackloader.common.util.dimension.ExposurePlaces;
import mctmods.resourcedatapackloader.common.util.dimension.ExposureSpread;
import mctmods.resourcedatapackloader.common.util.entity.Players;
import mctmods.resourcedatapackloader.common.util.entity.PotionEffects;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;
import mctmods.resourcedatapackloader.core.integration.CompatHandler;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Registries;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public final class ContentExposures {
    private static final Map<ExposureDef, Map<Block, Integer>> BLOCK_LEVELS = new IdentityHashMap<>();
    private static final Map<ExposureDef, Map<Item, Integer>> ITEM_LEVELS = new IdentityHashMap<>();
    private static final Map<ExposureDef, Set<Item>> IMMUNE_ITEMS = new IdentityHashMap<>();
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
        EntityPlayerMP server = Players.serverPlayer(player);
        if (def.skipsCreative && (player.isCreative() || player.isSpectator())) {
            applyLevel(def, player, 0);
            if (server != null) { CardEvents.exposureLevel(server, def, 0); }
            return;
        }
        if (player.ticksExisted % def.scanInterval == 0) {
            int level = scan(def, player);
            applyLevel(def, player, level);
            if (server != null) { CardEvents.exposureLevel(server, def, level); }
            spreadFrom(def, player, level);
        }
        damageTick(def, player);
    }

    private static void tickMob(ExposureDef def, EntityLivingBase mob) {
        boolean catches = ExposureSpread.catches(def, mob);
        if (!catches && ExposureSpread.carrierLevel(def, mob) <= 0) { return; }
        if (mob.ticksExisted % def.scanInterval == 0) {
            int level = catches && !immune(def, mob) ? capped(def, ExposureSpread.caughtLevel(def, mob)) : 0;
            if (catches) { applyLevel(def, mob, level); }
            spreadFrom(def, mob, level);
        }
        if (catches) { damageTick(def, mob); }
    }

    private static void spreadFrom(ExposureDef def, EntityLivingBase entity, int level) {
        if (!def.spreads()) { return; }
        int spreading = Math.max(ExposureSpread.carrierLevel(def, entity), def.contagious ? level : 0);
        ExposureSpread.spread(def, entity, capped(def, spreading));
    }

    private static int capped(ExposureDef def, int level) { return Math.min(def.levels.size(), level); }

    private static int scan(ExposureDef def, EntityPlayer player) {
        if (immune(def, player)) { return 0; }
        int most = def.levels.size();
        int level = Math.max(def.dimensions.getOrDefault(player.dimension, 0), scanItems(def, player, most));
        level = Math.max(level, Math.max(ExposureSpread.weatherLevel(def, player), ExposureSpread.caughtLevel(def, player)));
        level = Math.max(level, ExposurePlaces.level(def, player));
        if (level >= most) { return most; }
        return Math.max(level, scanWorld(def, player, most));
    }

    private static boolean immune(ExposureDef def, EntityLivingBase entity) {
        if (!def.immunity.isEmpty()) {
            Potion immune = immunity(def);
            if (immune != null && entity.isPotionActive(immune)) { return true; }
        }
        return wearsImmune(def, entity) || onImmuneTeam(def, entity);
    }

    private static boolean wearsImmune(ExposureDef def, EntityLivingBase entity) {
        if (def.immuneItems.isEmpty()) { return false; }
        Set<Item> immune = immuneItems(def);
        for (ItemStack stack : entity.getEquipmentAndArmor()) {
            if (!stack.isEmpty() && immune.contains(stack.getItem())) { return true; }
        }
        return false;
    }

    private static boolean onImmuneTeam(ExposureDef def, EntityLivingBase entity) {
        if (def.immuneTeams.isEmpty()) { return false; }
        ScorePlayerTeam team = ContentTeams.teamOf(entity.world, ContentTeams.memberName(entity));
        return team != null && def.immuneTeams.contains(team.getName());
    }

    @Nullable private static Potion immunity(ExposureDef def) { return IMMUNITIES.computeIfAbsent(def, held -> Potion.getPotionFromResourceLocation(held.immunity)); }

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
        int highest = Math.min(GenHeights.ceiling(world, CompatHandler.VANILLA_HEIGHT) - 1, centerY + radius);
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
            living.addPotionEffect(PotionEffects.build(potion, extra, extra.duration > 0 ? extra.duration : duration, extra.showParticles));
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

    @Nullable private static Potion extraPotion(PotionEffectDef extra) { return EXTRAS.computeIfAbsent(extra, held -> Potion.getPotionFromResourceLocation(held.potion)); }

    private static DamageSource source(ExposureDef def) { return SOURCES.computeIfAbsent(def, held -> new DamageSource("rdpl." + held.name).setDamageBypassesArmor().setDamageIsAbsolute()); }

    private static Map<Block, Integer> blockLevels(ExposureDef def) {
        return BLOCK_LEVELS.computeIfAbsent(def, held -> {
            Map<Block, Integer> levels = new IdentityHashMap<>();
            for (Map.Entry<ResourceLocation, Integer> entry : held.blocks.entrySet()) {
                Block block = resolve(ForgeRegistries.BLOCKS, held, "block", entry.getKey());
                if (block != null) { levels.put(block, entry.getValue()); }
            }
            return levels;
        });
    }

    private static Map<Item, Integer> itemLevels(ExposureDef def) {
        return ITEM_LEVELS.computeIfAbsent(def, held -> {
            Map<Item, Integer> levels = new IdentityHashMap<>();
            for (Map.Entry<ResourceLocation, Integer> entry : held.items.entrySet()) {
                Item item = resolve(ForgeRegistries.ITEMS, held, "item", entry.getKey());
                if (item != null) { levels.put(item, entry.getValue()); }
            }
            return levels;
        });
    }

    private static Set<Item> immuneItems(ExposureDef def) {
        return IMMUNE_ITEMS.computeIfAbsent(def, held -> {
            Set<Item> items = Collections.newSetFromMap(new IdentityHashMap<>());
            for (ResourceLocation name : held.immuneItems) {
                Item item = resolve(ForgeRegistries.ITEMS, held, "immune item", name);
                if (item != null) { items.add(item); }
            }
            return items;
        });
    }

    @Nullable private static <T extends IForgeRegistryEntry<T>> T resolve(IForgeRegistry<T> registry, ExposureDef def, String kind, ResourceLocation name) {
        T found = Registries.find(registry, name);
        if (found == null) { ContentLog.LOGGER.error("Exposure {} names {} {}, which is not registered, so it is ignored", def.registryName, kind, name); }
        return found;
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
