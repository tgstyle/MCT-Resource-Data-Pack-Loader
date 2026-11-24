package mctmods.resourcedatapackloader.common.gates;

import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.entities.behavior.ContentRanks;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.GateDef;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.common.util.PlayerLogout;
import mctmods.resourcedatapackloader.common.util.dimension.Travel;
import mctmods.resourcedatapackloader.common.util.loot.Stacks;
import mctmods.resourcedatapackloader.core.util.Compat;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public final class GateEvents {
    private static final long QUIET_MILLIS = 2000L;
    private static final int DROP_DELAY = 10;
    private static final Map<UUID, Long> SPOKEN = PlayerLogout.track(new HashMap<>());

    private GateEvents() {}

    public static void onTravel(EntityTravelToDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        for (GateDef def : ContentGates.forDimension(event.getDimension().identifier())) {
            if (ContentGates.admits(player, def)) { continue; }
            event.setCanceled(true);
            ContentLog.LOGGER.debug("Gate {} turned {} back from {}", def.key(), player.getName().getString(), def.dimension());
            if (quiet(player)) { return; }
            ContentGates.refuse(player, def);
            if (def.safeReturn()) { retreat(player); }
            return;
        }
    }

    public static void onKill(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) { return; }
        Identifier fallen = EntityType.getKey(event.getEntity().getType());
        for (GateDef def : ContentGates.all()) {
            if (def.killed() == null || ContentGates.unlocked(player, def)) { continue; }
            if (!fallen.equals(def.killed())) { continue; }
            if (!def.killedRank().isEmpty() && !def.killedRank().equals(ContentRanks.rankOf(event.getEntity()))) { continue; }
            int slain = GateStorage.tally(player, def);
            if (slain < def.killedCount()) { continue; }
            if (def.killedDrops().isEmpty()) {
                ContentGates.unlock(player, def, true);
                continue;
            }
            GateStorage.clearTally(player, def);
            reward(player, def);
        }
    }

    private static void reward(ServerPlayer player, GateDef def) {
        ItemStack key = ContentStacks.parse(def.key(), def.killedDrops(), 1);
        if (key.isEmpty()) { return; }
        Stacks.dropAt(player.level(), player.getX(), player.getY(), player.getZ(), key, DROP_DELAY);
    }

    public static void onCraft(PlayerEvent.ItemCraftedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        for (GateDef def : ContentGates.all()) {
            if (def.craft().isEmpty() || ContentGates.unlocked(player, def)) { continue; }
            if (!ContentStacks.matches(event.getCrafting(), ContentGates.stack(def.craft()))) { continue; }
            ContentGates.unlock(player, def, true);
        }
    }

    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof ServerPlayer player)) { return; }
        ItemStack held = event.getItemStack();
        if (held.isEmpty()) { return; }
        Block clicked = event.getLevel().getBlockState(event.getPos()).getBlock();
        String name = BuiltInRegistries.BLOCK.getKey(clicked).toString();
        for (GateDef def : ContentGates.all()) {
            if (def.consume().isEmpty() || ContentGates.unlocked(player, def)) { continue; }
            if (!def.portalBlocks().isEmpty() && !def.portalBlocks().contains(name)) { continue; }
            if (!ContentStacks.matches(held, ContentGates.stack(def.consume()))) { continue; }
            if (held.getCount() < def.consumeCount()) { continue; }
            if (!player.isCreative()) { held.shrink(def.consumeCount()); }
            ContentGates.unlock(player, def, true);
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }
    }

    public static void onAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        String earned = event.getAdvancement().id().toString();
        for (GateDef def : ContentGates.all()) {
            if (def.advancement().isEmpty() || def.perPlayer() || !def.advancement().equals(earned)) { continue; }
            ContentGates.unlock(player, def, true);
        }
    }

    static void sendOut(ServerPlayer player, ServerLevel level) {
        BlockPos feet = landing(player, level);
        List<ItemStack> returned = new ArrayList<>();
        if (player.getVehicle() instanceof Container inventory) {
            for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
                ItemStack stack = inventory.getItem(slot);
                if (!stack.isEmpty() && !GateLoot.stays(player, stack)) { returned.add(inventory.removeItemNoUpdate(slot)); }
            }
        }
        player.stopRiding();
        if (!Travel.to(player, level, feet, player.getYRot(), player.getXRot())) {
            ServerLevel overworld = player.level().getServer().overworld();
            BlockPos home = ContentDimensions.landing(overworld, overworld.getRespawnData().pos(), Heightmap.Types.MOTION_BLOCKING_NO_LEAVES);
            Travel.to(player, overworld, home, player.getYRot(), player.getXRot());
        }
        for (ItemStack stack : returned) {
            if (!player.getInventory().add(stack)) { player.level().addFreshEntity(new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stack)); }
        }
    }

    private static BlockPos landing(ServerPlayer player, ServerLevel level) {
        BlockPos bed = safeBed(player, level);
        return ContentDimensions.landing(level, bed == null ? level.getRespawnData().pos() : bed, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES);
    }

    private static void retreat(ServerPlayer player) {
        ServerLevel level = player.level();
        BlockPos feet = landing(player, level);
        Vec3 stand = Vec3.atBottomCenterOf(feet);
        player.teleportTo(level, stand.x, stand.y, stand.z, Set.of(), player.getYRot(), player.getXRot(), true);
    }

    @Nullable private static BlockPos safeBed(ServerPlayer player, ServerLevel level) {
        ServerPlayer.RespawnConfig respawn = player.getRespawnConfig();
        if (respawn == null || !respawn.respawnData().dimension().equals(level.dimension())) { return null; }
        BlockPos bed = respawn.respawnData().pos();
        BlockState state = level.getBlockState(bed);
        if (state.getBlock() instanceof BedBlock) { return BedBlock.findStandUpPosition(Compat.player(), level, bed, state.getValue(BedBlock.FACING), respawn.respawnData().yaw()).map(BlockPos::containing).orElse(null); }
        if (state.getBlock() instanceof RespawnAnchorBlock && state.getValue(RespawnAnchorBlock.CHARGE) > 0) { return RespawnAnchorBlock.findStandUpPosition(Compat.player(), level, bed).map(BlockPos::containing).orElse(null); }
        if (!respawn.forced()) { return null; }
        BlockState above = level.getBlockState(bed.above());
        return state.getBlock().isPossibleToRespawnInThis(state) && above.getBlock().isPossibleToRespawnInThis(above) ? bed : null;
    }

    private static boolean quiet(ServerPlayer player) {
        long now = System.currentTimeMillis();
        Long last = SPOKEN.get(player.getUUID());
        if (last != null && now - last < QUIET_MILLIS) { return true; }
        SPOKEN.put(player.getUUID(), now);
        return false;
    }
}
