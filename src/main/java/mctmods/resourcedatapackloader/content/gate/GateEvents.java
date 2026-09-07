package mctmods.resourcedatapackloader.content.gate;

import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.GateDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class GateEvents {
    private static final long QUIET_MILLIS = 2000L;
    private static final Map<UUID, Long> SPOKEN = new HashMap<>();

    private GateEvents() {}

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { SPOKEN.remove(event.getEntity().getUUID()); }

    public static void onTravel(EntityTravelToDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        for (GateDef def : ContentGates.forDimension(event.getDimension().location())) {
            if (ContentGates.unlocked(player, def)) { continue; }
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
        ResourceLocation fallen = EntityType.getKey(event.getEntity().getType());
        for (GateDef def : ContentGates.all()) {
            if (def.killed().isEmpty() || ContentGates.unlocked(player, def)) { continue; }
            if (!fallen.equals(ResourceLocation.tryParse(def.killed()))) { continue; }
            int slain = def.global() ? GateStorage.tallyGlobally(player.server, def.id()) : GateStorage.tallyFor(player, def.id());
            if (slain < def.killedCount()) { continue; }
            if (def.killedDrops().isEmpty()) {
                ContentGates.unlock(player, def, true);
                continue;
            }
            if (def.global()) { GateStorage.clearTallyGlobally(player.server, def.id()); }
            else { GateStorage.clearTallyFor(player, def.id()); }
            reward(player, def);
        }
    }

    private static void reward(ServerPlayer player, GateDef def) {
        ItemStack key = ContentStacks.parse(def.key(), def.killedDrops(), 1);
        if (key.isEmpty()) { return; }
        ItemEntity drop = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), key);
        drop.setDefaultPickUpDelay();
        player.level().addFreshEntity(drop);
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
            if (def.advancement().isEmpty() || !def.global() || !def.advancement().equals(earned)) { continue; }
            ContentGates.unlock(player, def, true);
        }
    }

    private static void retreat(ServerPlayer player) {
        ServerLevel level = player.server.getLevel(player.getRespawnDimension());
        BlockPos target = player.getRespawnPosition();
        if (level == null || target == null) {
            level = player.server.overworld();
            target = level.getSharedSpawnPos();
        }
        BlockPos feet = ContentDimensions.landing(level, target, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES);
        player.teleportTo(level, feet.getX() + 0.5D, feet.getY(), feet.getZ() + 0.5D, player.getYRot(), player.getXRot());
    }

    private static boolean quiet(ServerPlayer player) {
        long now = System.currentTimeMillis();
        Long last = SPOKEN.get(player.getUUID());
        if (last != null && now - last < QUIET_MILLIS) { return true; }
        SPOKEN.put(player.getUUID(), now);
        return false;
    }
}
