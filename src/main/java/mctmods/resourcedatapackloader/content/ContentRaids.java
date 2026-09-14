package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.entity.ai.EntityAIHideIndoors;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAIRaidBreakDoor;
import mctmods.resourcedatapackloader.content.entity.ai.EntityAIRaidMarch;
import mctmods.resourcedatapackloader.content.raid.ActiveRaid;
import mctmods.resourcedatapackloader.content.raid.RaidStorage;
import mctmods.resourcedatapackloader.content.def.RaidDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentRaids {
    private static final int LOOK_EVERY = 20;
    private static final int VILLAGE_SEARCH = 32;
    private static final int BELL_REACH = 48;
    private static final int GLOW_TICKS = 60;
    private static final int HIDE_TICKS = 300;
    private static final String HEARD_BELL = "rdplHeardBell";
    private static final Set<Block> BELLS = new HashSet<>();
    private static final Map<ResourceLocation, RaidDef> DEFS = new LinkedHashMap<>();
    private static boolean armed;

    private ContentRaids() {}

    public static void load() {
        DEFS.clear();
        BELLS.clear();
        PackManager.get().forEach(PackManager.RAIDS, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            RaidDef def = ContentParser.raidFile(key, contents);
            if (def != null) { DEFS.put(key, def); }
        });
        if (DEFS.isEmpty()) { return; }
        for (RaidDef def : DEFS.values()) {
            for (String name : def.bells) {
                Block bell = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
                if (bell == null || bell == Blocks.AIR) { ContentLog.LOGGER.error("Raid {} names the bell {}, which nothing registers, so no block rings for it", def.registryName, name); }
                else { BELLS.add(bell); }
            }
        }
        Summary.info("raids", "Loaded " + DEFS.size() + " raid(s): " + DEFS.keySet());
        if (!armed) {
            MinecraftForge.EVENT_BUS.register(ContentRaids.class);
            armed = true;
        }
    }

    @Nullable public static RaidDef def(ResourceLocation key) { return DEFS.get(key); }

    public static boolean isBell(IBlockState state) { return BELLS.contains(state.getBlock()); }

    public static boolean hiding(EntityCreature mob) {
        if (!(mob.world instanceof WorldServer)) { return false; }
        NBTTagCompound data = mob.getEntityData();
        if (data.hasKey(HEARD_BELL) && mob.world.getTotalWorldTime() - data.getLong(HEARD_BELL) < HIDE_TICKS) { return true; }
        return RaidStorage.get((WorldServer) mob.world).underway(new BlockPos(mob));
    }

    public static void ring(WorldServer world, BlockPos bell) {
        world.playSound(null, bell, SoundEvents.BLOCK_NOTE_BELL, SoundCategory.BLOCKS, 2.0F, 1.0F);
        AxisAlignedBB around = new AxisAlignedBB(bell).grow(BELL_REACH);
        long now = world.getTotalWorldTime();
        for (EntityVillager villager : world.getEntitiesWithinAABB(EntityVillager.class, around)) { villager.getEntityData().setLong(HEARD_BELL, now); }
        for (EntityLivingBase raider : world.getEntitiesWithinAABB(EntityLivingBase.class, around)) {
            if (raider.getEntityData().hasKey(ActiveRaid.RAIDER)) { raider.addPotionEffect(new PotionEffect(MobEffects.GLOWING, GLOW_TICKS)); }
        }
    }

    @SubscribeEvent public static void onRing(PlayerInteractEvent.RightClickBlock event) {
        if (BELLS.isEmpty() || !(event.getWorld() instanceof WorldServer) || !isBell(event.getWorld().getBlockState(event.getPos()))) { return; }
        ring((WorldServer) event.getWorld(), event.getPos());
    }

    @SubscribeEvent public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.world instanceof WorldServer) || DEFS.isEmpty()) { return; }
        RaidStorage.get((WorldServer) event.world).tick((WorldServer) event.world);
    }

    @SubscribeEvent public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (event.phase != TickEvent.Phase.END || !(player.world instanceof WorldServer) || player.ticksExisted % LOOK_EVERY != 0 || DEFS.isEmpty() || player.isSpectator()) { return; }
        for (RaidDef def : DEFS.values()) {
            Potion omen = ForgeRegistries.POTIONS.getValue(new ResourceLocation(def.omen));
            if (omen == null || !player.isPotionActive(omen)) { continue; }
            WorldServer world = (WorldServer) player.world;
            BlockPos at = new BlockPos(player);
            Village village = ActiveRaid.nearestVillage(world, at, 0);
            if (village == null) { return; }
            RaidStorage storage = RaidStorage.get(world);
            if (storage.near(village.getCenter(), VILLAGE_SEARCH + village.getVillageRadius()) != null) { return; }
            player.removePotionEffect(omen);
            storage.add(new ActiveRaid(def, village.getCenter()));
            ContentLog.LOGGER.info("{} carries {} into the village at {}, {}, {}, so raid {} begins", player.getName(), def.omen, village.getCenter().getX(), village.getCenter().getY(), village.getCenter().getZ(), def.registryName);
            return;
        }
    }

    @SubscribeEvent public static void onRaiderHit(LivingAttackEvent event) {
        if (raider(event.getEntityLiving()) && raider(event.getSource().getTrueSource())) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onRaiderTarget(LivingSetAttackTargetEvent event) {
        if (!raider(event.getTarget()) || !raider(event.getEntityLiving()) || !(event.getEntityLiving() instanceof EntityLiving)) { return; }
        EntityLiving living = (EntityLiving) event.getEntityLiving();
        living.setRevengeTarget(null);
        living.setAttackTarget(null);
    }

    private static boolean raider(@Nullable Entity entity) { return entity != null && !entity.world.isRemote && entity.getEntityData().hasKey(ActiveRaid.RAIDER); }

    @SubscribeEvent public static void onJoin(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote || DEFS.isEmpty()) { return; }
        Entity entity = event.getEntity();
        if (entity instanceof EntityVillager) {
            ((EntityVillager) entity).tasks.addTask(1, new EntityAIHideIndoors((EntityVillager) entity));
            return;
        }
        if (!(entity instanceof EntityCreature) || !entity.getEntityData().hasKey(ActiveRaid.RAIDER)) { return; }
        EntityCreature raider = (EntityCreature) entity;
        raider.tasks.addTask(1, new EntityAIRaidBreakDoor(raider));
        raider.tasks.addTask(4, new EntityAIRaidMarch(raider));
        raider.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(raider, EntityPlayer.class, true));
        raider.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(raider, EntityVillager.class, false));
        raider.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(raider, EntityIronGolem.class, true));
    }
}
