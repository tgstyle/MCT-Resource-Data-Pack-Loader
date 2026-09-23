package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.block.ContentBellBlock;
import mctmods.resourcedatapackloader.content.def.RaidDef;
import mctmods.resourcedatapackloader.content.entity.goal.RaidBreakDoorGoal;
import mctmods.resourcedatapackloader.content.entity.goal.RaidMarchGoal;
import mctmods.resourcedatapackloader.content.raid.ActiveRaid;
import mctmods.resourcedatapackloader.content.raid.RaidStorage;
import mctmods.resourcedatapackloader.content.raid.RaidVillage;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentRaids {
    private static final int LOOK_EVERY = 20;
    private static final int VILLAGE_SEARCH = 32;
    private static final int BELL_REACH = 48;
    private static final int GLOW_TICKS = 60;
    private static final Set<Block> BELLS = new HashSet<>();
    private static final Map<ResourceLocation, RaidDef> DEFS = new LinkedHashMap<>();
    private static final Map<String, Optional<MobEffect>> OMENS = new HashMap<>();

    private ContentRaids() {}

    public static void load() {
        DEFS.clear();
        BELLS.clear();
        OMENS.clear();
        Json.eachFile(PackManager.RAIDS, "raid file", (key, contents) -> {
            RaidDef def = ContentParserGames.raidFile(key, contents);
            if (def != null) { DEFS.put(key, def); }
        });
        if (DEFS.isEmpty()) { return; }
        for (RaidDef def : DEFS.values()) {
            for (String name : def.bells()) {
                Block bell = Registered.find(ForgeRegistries.BLOCKS, ResourceLocation.tryParse(name.trim()));
                if (bell == null || bell == Blocks.AIR) { ContentLog.LOGGER.error("Raid {} names the bell {}, which nothing registers, so no block rings for it", def.key(), name); }
                else if (!(bell instanceof ContentBellBlock)) { BELLS.add(bell); }
            }
        }
        Summary.info("raids", "Loaded " + DEFS.size() + " raid(s): " + DEFS.keySet());
    }

    @Nullable public static RaidDef def(@Nullable ResourceLocation key) { return key == null ? null : DEFS.get(key); }

    public static boolean isBell(BlockState state) { return state.getBlock() instanceof ContentBellBlock || BELLS.contains(state.getBlock()); }

    public static boolean answersBell(LivingEntity living) { return living.getPersistentData().contains(ActiveRaid.RAIDER) || living instanceof Raider; }

    public static void glow(LivingEntity raider) { raider.addEffect(new MobEffectInstance(MobEffects.GLOWING, GLOW_TICKS)); }

    public static void hear(Villager villager, long now) { villager.getBrain().setMemory(MemoryModuleType.HEARD_BELL_TIME, now); }

    public static int hide(ServerLevel level, BlockPos center, int reach) {
        long now = level.getGameTime();
        List<Villager> villagers = level.getEntitiesOfClass(Villager.class, new AABB(center).inflate(reach));
        for (Villager villager : villagers) {
            if (ContentLog.LOGGER.debugEnabled() && !villager.getBrain().hasMemoryValue(MemoryModuleType.HEARD_BELL_TIME)) { ContentLog.LOGGER.debug("{} at {}, {}, {} hides indoors", villager.getName().getString(), villager.getBlockX(), villager.getBlockY(), villager.getBlockZ()); }
            hear(villager, now);
        }
        return villagers.size();
    }

    public static void ring(ServerLevel level, BlockPos bell) {
        Block block = level.getBlockState(bell).getBlock();
        if (block instanceof ContentBellBlock pack) {
            pack.ring(level, bell, null);
            return;
        }
        level.playSound(null, bell, SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.BLOCKS, 2.0F, 1.0F);
        int hiding = hide(level, bell, BELL_REACH);
        int glowing = 0;
        for (LivingEntity raider : level.getEntitiesOfClass(LivingEntity.class, new AABB(bell).inflate(BELL_REACH))) {
            if (!answersBell(raider)) { continue; }
            glow(raider);
            glowing++;
        }
        ContentLog.LOGGER.debug("The bell at {}, {}, {} rings: {} villager(s) hide and {} raider(s) glow", bell.getX(), bell.getY(), bell.getZ(), hiding, glowing);
    }

    public static void onRing(PlayerInteractEvent.RightClickBlock event) {
        if (BELLS.isEmpty() || !(event.getLevel() instanceof ServerLevel level) || !BELLS.contains(level.getBlockState(event.getPos()).getBlock())) { return; }
        ring(level, event.getPos());
    }

    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level) || DEFS.isEmpty()) { return; }
        RaidStorage.get(level).tick(level);
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player) || !(player.level() instanceof ServerLevel level) || player.tickCount % LOOK_EVERY != 0 || DEFS.isEmpty() || player.isSpectator()) { return; }
        for (RaidDef def : DEFS.values()) {
            MobEffect omen = OMENS.computeIfAbsent(def.omen(), name -> Optional.ofNullable(Registered.find(ForgeRegistries.MOB_EFFECTS, ResourceLocation.tryParse(name)))).orElse(null);
            if (omen == null || !player.hasEffect(omen)) { continue; }
            RaidVillage village = RaidVillage.nearest(level, player.blockPosition(), 0);
            if (village == null) { return; }
            RaidStorage storage = RaidStorage.get(level);
            if (storage.raided(village.center(), VILLAGE_SEARCH + village.radius())) { return; }
            player.removeEffect(omen);
            storage.add(new ActiveRaid(def, village.center()));
            ContentLog.LOGGER.info("{} carries {} into the village at {}, {}, {}, so raid {} begins", player.getGameProfile().getName(), def.omen(), village.center().getX(), village.center().getY(), village.center().getZ(), def.key());
            return;
        }
    }

    public static void onRaiderHit(LivingAttackEvent event) {
        if (raider(event.getEntity()) && raider(event.getSource().getEntity())) { event.setCanceled(true); }
    }

    public static void onRaiderTarget(LivingChangeTargetEvent event) {
        if (!raider(event.getNewTarget()) || !raider(event.getEntity())) { return; }
        event.getEntity().setLastHurtByMob(null);
        event.setNewTarget(null);
    }

    private static boolean raider(@Nullable Entity entity) { return entity != null && !entity.level().isClientSide() && entity.getPersistentData().contains(ActiveRaid.RAIDER); }

    public static void onJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || DEFS.isEmpty() || !(event.getEntity() instanceof PathfinderMob raider) || !raider.getPersistentData().contains(ActiveRaid.RAIDER)) { return; }
        raider.goalSelector.addGoal(1, new RaidBreakDoorGoal(raider));
        raider.goalSelector.addGoal(4, new RaidMarchGoal(raider));
        raider.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(raider, Player.class, true));
        raider.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(raider, Villager.class, false));
        raider.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(raider, IronGolem.class, true));
    }
}
