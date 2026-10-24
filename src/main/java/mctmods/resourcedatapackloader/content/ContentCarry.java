package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.BlockMatchDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.PackGeneration;
import mctmods.resourcedatapackloader.util.PlayerPersisted;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class ContentCarry {
    private static final String BLOCKS = "blocks";
    private static final String MOBS = "mobs";
    private static final String CARRY = "rdplCarry";
    private static final String KIND = "kind";
    private static final String BLOCK = "block";
    private static final String MOB = "mob";
    private static final String STATE = "state";
    private static final String TILE = "tile";
    private static final String ID = "id";
    private static final int EVERY = 10;
    private static final UUID CARRIED = UUID.fromString("6d1f3a52-94c7-4e08-b3a1-7f2c5e9d0b46");
    private static final Set<EntityType<?>> MOB_RULES = new HashSet<>();
    private static final PackGeneration GENERATION = new PackGeneration();
    private static BlockGroup blockRules = new BlockGroup();
    private static boolean breaking;

    private ContentCarry() {}

    public static boolean breaking() { return breaking; }

    public static boolean listsMob(Entity target) {
        rules();
        return MOB_RULES.contains(target.getType());
    }

    private static void rules() {
        if (GENERATION.stale()) { reload(); }
    }

    private static void reload() {
        blockRules = new BlockGroup();
        MOB_RULES.clear();
        Json.eachObject(PackManager.CARRIES, "carries", ContentCarry::read);
        if (!blockRules.isEmpty() || !MOB_RULES.isEmpty()) { ContentLog.LOGGER.info("Loaded carrying: {} mob(s), blocks {}", MOB_RULES.size(), blockRules.isEmpty() ? "none" : "listed"); }
    }

    private static EntityType<?> mob(String name) { return Registered.find(ForgeRegistries.ENTITY_TYPES, ResourceLocation.tryParse(Settings.lower(name))); }

    private static void read(ResourceLocation key, JsonObject json) {
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        if (!json.has(BLOCKS) && !json.has(MOBS)) {
            ContentLog.LOGGER.error("Carry {} names no blocks and no mobs, ignoring it", key);
            return;
        }
        if (json.has(BLOCKS)) {
            for (BlockMatchDef match : ContentParser.matches(key, json, BLOCKS)) { blockRules.add(match, "Carry " + key, BLOCKS); }
        }
        if (json.has(MOBS)) {
            for (JsonElement element : Json.getJsonArray(json, MOBS)) {
                String name = Json.asString(element, MOBS, "");
                EntityType<?> type = mob(name);
                if (type == null || type == EntityType.PLAYER) {
                    ContentLog.LOGGER.error("Carry {} names the mob {}, which nothing registers, skipping it", key, name);
                    continue;
                }
                MOB_RULES.add(type);
            }
        }
    }

    private static boolean carrying(Player player) {
        if (player.level().isClientSide()) {
            AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
            return speed != null && speed.getModifier(CARRIED) != null;
        }
        return !PlayerPersisted.read(player, CARRY).isEmpty();
    }

    private static boolean notReady(PlayerInteractEvent event) {
        Player player = event.getEntity();
        return event.getHand() != InteractionHand.MAIN_HAND || !player.isShiftKeyDown() || player.isSpectator() || !player.getMainHandItem().isEmpty();
    }

    private static void consume(PlayerInteractEvent event) {
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (notReady(event)) { return; }
        rules();
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        if (level.isClientSide()) {
            if (carrying(player) || blockRules.contains(level.getBlockState(pos))) { consume(event); }
            return;
        }
        ServerPlayer mover = (ServerPlayer) player;
        CompoundTag held = PlayerPersisted.read(player, CARRY);
        if (!held.isEmpty()) {
            consume(event);
            setDown(mover, held, pos, event.getFace() == null ? Direction.UP : event.getFace());
            return;
        }
        BlockState state = level.getBlockState(pos);
        if (!blockRules.contains(state)) { return; }
        consume(event);
        pickUp(mover, pos, state);
    }

    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (notReady(event) || !listsMob(event.getTarget())) { return; }
        consume(event);
        Player player = event.getEntity();
        if (player.level().isClientSide()) { return; }
        Entity target = event.getTarget();
        if (carrying(player)) {
            refuse(player, EntityType.getKey(target.getType()), "already carrying", "rdpl.carry.full");
            return;
        }
        pickUp((ServerPlayer) player, target);
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player) || player.tickCount % EVERY != 0) { return; }
        CompoundTag held = PlayerPersisted.read(player, CARRY);
        boolean carrying = !held.isEmpty();
        if (carrying && unresolved(held)) {
            ContentLog.LOGGER.error("Carry: {} was carrying something that nothing registers any more, so the carry is cleared: {}", name(player), held);
            PlayerPersisted.of(player, CARRY).remove(CARRY);
            carrying = false;
        }
        slow(player, carrying);
    }

    private static boolean unresolved(CompoundTag held) {
        String kind = held.getString(KIND);
        if (BLOCK.equals(kind)) { return Registered.block(held.getCompound(STATE).getString("Name")) == null; }
        return !MOB.equals(kind) || mob(held.getCompound(MOB).getString(ID)) == null;
    }

    private static String name(Player player) { return player.getGameProfile().getName(); }

    private static void slow(Player player, boolean carrying) {
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null || carrying == (speed.getModifier(CARRIED) != null)) { return; }
        if (carrying) { speed.addTransientModifier(new AttributeModifier(CARRIED, "rdpl:carry", -0.5D, AttributeModifier.Operation.MULTIPLY_TOTAL)); }
        else { speed.removeModifier(CARRIED); }
    }

    private static void refuse(Player player, Object what, String why, String key) {
        ContentLog.LOGGER.info("Carry: {} was refused {} ({})", name(player), what, why);
        player.displayClientMessage(Component.translatable(key), true);
    }

    private static void taken(Player player, CompoundTag saved, Object what, Component label, String key) {
        PlayerPersisted.of(player, CARRY).put(CARRY, saved);
        slow(player, true);
        ContentLog.LOGGER.info("Carry: {} picked up {}", name(player), what);
        player.displayClientMessage(Component.translatable(key, label), true);
    }

    private static void given(Player player, Object what, Component label) {
        PlayerPersisted.of(player, CARRY).remove(CARRY);
        slow(player, false);
        ContentLog.LOGGER.info("Carry: {} set down {}", name(player), what);
        player.displayClientMessage(Component.translatable("rdpl.carry.down", label), true);
    }

    private static boolean twoBlock(BlockState state) { return state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) || state.hasProperty(BlockStateProperties.BED_PART); }

    private static boolean permits(ServerPlayer player, BlockPos pos) {
        breaking = true;
        try { return ForgeHooks.onBlockBreakEvent(player.level(), player.gameMode.getGameModeForPlayer(), player, pos) >= 0; }
        finally { breaking = false; }
    }

    private static void pickUp(ServerPlayer player, BlockPos pos, BlockState state) {
        ServerLevel level = player.serverLevel();
        ResourceLocation name = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        if (state.getDestroySpeed(level, pos) < 0.0F) { refuse(player, name, "unbreakable", "rdpl.carry.denied"); return; }
        if (twoBlock(state)) { refuse(player, name, "two blocks", "rdpl.carry.denied"); return; }
        if (!permits(player, pos)) { refuse(player, name, "not allowed to break it", "rdpl.carry.denied"); return; }
        CompoundTag saved = new CompoundTag();
        saved.putString(KIND, BLOCK);
        saved.put(STATE, NbtUtils.writeBlockState(state));
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity != null) {
            saved.put(TILE, entity.saveWithFullMetadata());
            level.removeBlockEntity(pos);
        }
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        taken(player, saved, name, state.getBlock().getName(), "rdpl.carry.block");
    }

    private static void pickUp(ServerPlayer player, Entity target) {
        ResourceLocation name = EntityType.getKey(target.getType());
        if (!target.isAlive()) { refuse(player, name, "not alive", "rdpl.carry.denied"); return; }
        target.ejectPassengers();
        target.stopRiding();
        CompoundTag data = new CompoundTag();
        if (!target.save(data)) { refuse(player, name, "cannot be saved", "rdpl.carry.denied"); return; }
        Component label = target.getDisplayName();
        target.discard();
        CompoundTag saved = new CompoundTag();
        saved.putString(KIND, MOB);
        saved.put(MOB, data);
        taken(player, saved, name, label, "rdpl.carry.mob");
    }

    private static void setDown(ServerPlayer player, CompoundTag held, BlockPos pos, Direction side) {
        BlockPos target = player.serverLevel().getBlockState(pos).canBeReplaced() ? pos : pos.relative(side);
        if (unresolved(held)) { refuse(player, held.getString(KIND), "nothing registers it", "rdpl.carry.denied"); return; }
        if (BLOCK.equals(held.getString(KIND))) { setDownBlock(player, held, target, side); }
        else { setDownMob(player, held, target); }
    }

    private static void setDownBlock(ServerPlayer player, CompoundTag held, BlockPos target, Direction side) {
        ServerLevel level = player.serverLevel();
        BlockState state = NbtUtils.readBlockState(level.holderLookup(Registries.BLOCK), held.getCompound(STATE));
        ResourceLocation name = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        if (!player.getAbilities().mayBuild || !level.mayInteract(player, target) || !level.getBlockState(target).canBeReplaced() || !state.canSurvive(level, target) || !level.isUnobstructed(state, target, CollisionContext.empty())) { refuse(player, name, "no room", "rdpl.carry.denied"); return; }
        BlockState placed = Block.updateFromNeighbourShapes(state, level, target);
        BlockSnapshot snapshot = BlockSnapshot.create(level.dimension(), level, target);
        if (!level.setBlock(target, placed, Block.UPDATE_ALL) || ForgeEventFactory.onBlockPlace(player, snapshot, side)) {
            snapshot.restore(true);
            refuse(player, name, "placement canceled", "rdpl.carry.denied");
            return;
        }
        BlockEntity entity = level.getBlockEntity(target);
        if (entity != null && held.contains(TILE, Tag.TAG_COMPOUND)) {
            entity.load(held.getCompound(TILE).copy());
            entity.setChanged();
            level.sendBlockUpdated(target, placed, placed, Block.UPDATE_ALL);
        }
        given(player, name, state.getBlock().getName());
    }

    private static void setDownMob(ServerPlayer player, CompoundTag held, BlockPos target) {
        ServerLevel level = player.serverLevel();
        ResourceLocation name = ResourceLocation.tryParse(held.getCompound(MOB).getString(ID));
        Entity entity = EntityType.loadEntityRecursive(held.getCompound(MOB).copy(), level, created -> created);
        if (entity == null) { refuse(player, name, "cannot be created", "rdpl.carry.denied"); return; }
        entity.moveTo(target.getX() + 0.5D, target.getY(), target.getZ() + 0.5D, entity.getYRot(), entity.getXRot());
        entity.setDeltaMovement(Vec3.ZERO);
        if (!player.getAbilities().mayBuild || !level.mayInteract(player, target) || !level.noCollision(entity) || !level.addFreshEntity(entity)) {
            refuse(player, name, "no room", "rdpl.carry.denied");
            return;
        }
        given(player, name, entity.getDisplayName());
    }
}
