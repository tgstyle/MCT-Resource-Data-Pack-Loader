package mctmods.resourcedatapackloader.common.raids;

import mctmods.resourcedatapackloader.common.blocks.bell.ContentBellBlock;
import mctmods.resourcedatapackloader.common.entities.ai.HideIndoorsGoal;
import mctmods.resourcedatapackloader.common.entities.ai.RaidBreakDoorGoal;
import mctmods.resourcedatapackloader.common.entities.ai.RaidMarchGoal;
import mctmods.resourcedatapackloader.common.entities.ai.RaidTargetGoal;
import mctmods.resourcedatapackloader.common.pack.definitions.villages.BaseDef;
import mctmods.resourcedatapackloader.common.pack.definitions.villages.RaidDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserGames;
import mctmods.resourcedatapackloader.common.raids.base.BaseLedger;
import mctmods.resourcedatapackloader.common.raids.base.BaseSignals;
import mctmods.resourcedatapackloader.common.rules.ContentRaidSwitch;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class ContentRaids {
    private static final int LOOK_EVERY = 20;
    private static final int BELL_REACH = 48;
    private static final int GLOW_TICKS = 60;
    private static final int HIDE_TICKS = 300;
    private static final String HEARD_BELL = "rdplHeardBell";
    private static final Set<Block> BELLS = new HashSet<>();
    private static final Map<ResourceLocation, RaidDef> DEFS = new LinkedHashMap<>();
    private static boolean belled;

    private ContentRaids() {}

    public static boolean load() {
        DEFS.clear();
        BELLS.clear();
        Json.eachFile(PackManager.RAIDS, "raid file", (key, contents) -> {
            RaidDef def = ContentParserGames.raidFile(key, contents);
            if (def != null) { DEFS.put(key, def); }
        });
        int largest = 0;
        for (RaidDef def : DEFS.values()) {
            if (def.base() != null) { largest = Math.max(largest, def.base().cellCap); }
        }
        BaseLedger.setCellCap(largest > 0 ? largest : BaseDef.DEFAULT_CELL_CAP);
        BaseSignals.arm(DEFS.values());
        belled = ContentRegistry.registeredBlocks().stream().anyMatch(entry -> entry.getValue() instanceof ContentBellBlock);
        if (DEFS.isEmpty() && !belled) { return false; }
        for (RaidDef def : DEFS.values()) {
            for (String name : def.bells) {
                Block bell = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
                if (bell == null || bell == Blocks.AIR) { ContentLog.LOGGER.error("Raid {} names the bell {}, which nothing registers, so no block rings for it", def.registryName, name); }
                else if (!(bell instanceof ContentBellBlock)) { BELLS.add(bell); }
            }
        }
        if (!DEFS.isEmpty()) { Summary.info("raids", "Loaded " + DEFS.size() + " raid(s): " + DEFS.keySet()); }
        return true;
    }

    public static boolean belled() { return belled; }

    @Nullable public static RaidDef def(ResourceLocation key) { return DEFS.get(key); }

    public static List<String> keys() {
        List<String> names = new ArrayList<>();
        for (ResourceLocation key : DEFS.keySet()) { names.add(key.toString()); }
        return names;
    }

    public static boolean begin(WorldServer world, RaidDef def, BlockPos at) {
        if (RaidStorage.get(world).near(at, def.reach) != null) { return false; }
        ActiveRaid.start(world, def, at, true);
        return true;
    }

    public static boolean isBell(IBlockState state) { return state.getBlock() instanceof ContentBellBlock || BELLS.contains(state.getBlock()); }

    public static boolean answersBell(EntityLivingBase living) { return living.getEntityData().hasKey(ActiveRaid.RAIDER) || living instanceof AbstractIllager || living instanceof EntityWitch; }

    public static void glow(EntityLivingBase raider) { raider.addPotionEffect(new PotionEffect(MobEffects.GLOWING, GLOW_TICKS)); }

    public static void hear(EntityVillager villager, long now) { villager.getEntityData().setLong(HEARD_BELL, now); }

    public static boolean hiding(EntityCreature mob) {
        if (!(mob.world instanceof WorldServer)) { return false; }
        NBTTagCompound data = mob.getEntityData();
        if (data.hasKey(HEARD_BELL) && mob.world.getTotalWorldTime() - data.getLong(HEARD_BELL) < HIDE_TICKS) { return true; }
        return RaidStorage.get((WorldServer) mob.world).underway(new BlockPos(mob));
    }

    public static void ring(WorldServer world, BlockPos bell) {
        Block block = world.getBlockState(bell).getBlock();
        if (block instanceof ContentBellBlock) {
            ((ContentBellBlock) block).ring(world, bell, null);
            return;
        }
        world.playSound(null, bell, SoundEvents.BLOCK_NOTE_BELL, SoundCategory.BLOCKS, 2.0F, 1.0F);
        AxisAlignedBB around = new AxisAlignedBB(bell).grow(BELL_REACH);
        long now = world.getTotalWorldTime();
        for (EntityVillager villager : world.getEntitiesWithinAABB(EntityVillager.class, around)) { hear(villager, now); }
        for (EntityLivingBase raider : world.getEntitiesWithinAABB(EntityLivingBase.class, around)) {
            if (answersBell(raider)) { glow(raider); }
        }
    }

    @SubscribeEvent public static void onRing(PlayerInteractEvent.RightClickBlock event) {
        if (BELLS.isEmpty() || !(event.getWorld() instanceof WorldServer) || !BELLS.contains(event.getWorld().getBlockState(event.getPos()).getBlock())) { return; }
        ring((WorldServer) event.getWorld(), event.getPos());
    }

    @SubscribeEvent public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.world instanceof WorldServer) || DEFS.isEmpty()) { return; }
        RaidStorage.get((WorldServer) event.world).tick((WorldServer) event.world);
    }

    @SubscribeEvent public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (event.phase != TickEvent.Phase.END || !(player.world instanceof WorldServer) || player.ticksExisted % LOOK_EVERY != 0 || DEFS.isEmpty() || player.isSpectator()) { return; }
        WorldServer world = (WorldServer) player.world;
        BaseSignals.sample(player);
        RaidDef due = ContentRaidSwitch.due(world, player, LOOK_EVERY);
        if (due != null && rolled(world, player, due)) { around(world, player, due, "carries the raid clock"); }
        for (RaidDef def : DEFS.values()) {
            Potion omen = ForgeRegistries.POTIONS.getValue(new ResourceLocation(def.omen));
            if (omen == null || !player.isPotionActive(omen)) { continue; }
            if (around(world, player, def, "carries " + def.omen)) { player.removePotionEffect(omen); }
            return;
        }
    }

    private static boolean rolled(WorldServer world, EntityPlayer player, RaidDef def) {
        BaseDef base = def.base();
        if (base == null || base.chance >= 1.0D || !def.target().contains(RaidTarget.Kind.BASE) && !def.target().contains(RaidTarget.Kind.PLAYER)) { return true; }
        return ContentRaidSwitch.roll(world, player, def);
    }

    private static boolean around(WorldServer world, EntityPlayer player, RaidDef def, String cause) {
        if (!(player instanceof EntityPlayerMP)) { return false; }
        RaidTarget target = RaidTargets.resolve(world, (EntityPlayerMP) player, def);
        if (target == null || !RaidTargets.launch(world, (EntityPlayerMP) player, def, target)) { return false; }
        BlockPos center = target.center();
        if (target.kind() == RaidTarget.Kind.FREE) {
            ContentLog.LOGGER.info("{} {} with no village near, so raid {} begins around them", player.getName(), cause, def.registryName);
            return true;
        }
        if (target.kind() == RaidTarget.Kind.VILLAGE) { ContentLog.LOGGER.info("{} {} into the village at {}, {}, {}, so raid {} begins", player.getName(), cause, center.getX(), center.getY(), center.getZ(), def.registryName); }
        else if (target.kind() == RaidTarget.Kind.BASE) { ContentLog.LOGGER.info("{} {} at their base at {}, {}, {}, so raid {} begins", player.getName(), cause, center.getX(), center.getY(), center.getZ(), def.registryName); }
        else { ContentLog.LOGGER.info("{} {}, so raid {} hunts them", player.getName(), cause, def.registryName); }
        return true;
    }

    @SubscribeEvent public static void onRaiderHit(LivingAttackEvent event) { if (raider(event.getEntityLiving()) && raider(event.getSource().getTrueSource())) { event.setCanceled(true); } }

    @SubscribeEvent public static void onRaiderTarget(LivingSetAttackTargetEvent event) {
        if (!raider(event.getTarget()) || !raider(event.getEntityLiving()) || !(event.getEntityLiving() instanceof EntityLiving)) { return; }
        EntityLiving living = (EntityLiving) event.getEntityLiving();
        living.setRevengeTarget(null);
        living.setAttackTarget(null);
    }

    private static boolean raider(@Nullable Entity entity) { return entity != null && !entity.world.isRemote && entity.getEntityData().hasKey(ActiveRaid.RAIDER); }

    @SubscribeEvent public static void onJoin(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote || DEFS.isEmpty() && !belled) { return; }
        Entity entity = event.getEntity();
        if (entity instanceof EntityVillager) {
            ((EntityVillager) entity).tasks.addTask(1, new HideIndoorsGoal((EntityVillager) entity));
            return;
        }
        if (DEFS.isEmpty() || !(entity instanceof EntityCreature) || !entity.getEntityData().hasKey(ActiveRaid.RAIDER)) { return; }
        EntityCreature raider = (EntityCreature) entity;
        NBTTagCompound tag = raider.getEntityData();
        ActiveRaid raid = RaidStorage.get((WorldServer) event.getWorld()).raidOf(NBTUtil.getPosFromTag(tag.getCompoundTag(ActiveRaid.RAIDER)));
        if (raid == null) {
            tag.removeTag(ActiveRaid.RAIDER);
            return;
        }
        raider.tasks.addTask(1, new RaidBreakDoorGoal(raider));
        raider.tasks.addTask(4, new RaidMarchGoal(raider));
        boolean guarded = raid.kind() == RaidTarget.Kind.BASE || raid.kind() == RaidTarget.Kind.PLAYER;
        raider.targetTasks.addTask(3, guarded ? new RaidTargetGoal(raider) : new EntityAINearestAttackableTarget<>(raider, EntityPlayer.class, true));
        raider.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(raider, EntityVillager.class, false));
        raider.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(raider, EntityIronGolem.class, true));
    }
}
