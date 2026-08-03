package mctmods.resourcedatapackloader.content.gate;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.GateDef;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public final class GateEvents {
    private static final long QUIET_MILLIS = 2000L;
    private static final Map<UUID, Long> SPOKEN = new HashMap<>();

    private GateEvents() {}

    @SubscribeEvent public static void onTravel(EntityTravelToDimensionEvent event) {
        if (!(event.getEntity() instanceof EntityPlayerMP)) { return; }

        EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
        List<GateDef> defs = ContentGates.forDimension(event.getDimension());
        for (GateDef def : defs) {
            if (ContentGates.unlocked(player, def)) { continue; }

            event.setCanceled(true);
            if (quiet(player)) { return; }

            ContentGates.refuse(player, def);
            if (def.safeReturn) { retreat(player); }
            return;
        }
    }

    @SubscribeEvent public static void onKill(LivingDeathEvent event) {
        Entity slayer = event.getSource().getTrueSource();
        if (!(slayer instanceof EntityPlayerMP)) { return; }

        ResourceLocation fallen = EntityList.getKey(event.getEntity());
        if (fallen == null) { return; }

        EntityPlayerMP player = (EntityPlayerMP) slayer;
        for (GateDef def : ContentGates.all().values()) {
            if (def.killed.isEmpty() || ContentGates.unlocked(player, def)) { continue; }
            if (!fallen.equals(new ResourceLocation(def.killed))) { continue; }

            int slain = def.global ? GateStorage.tallyGlobally(player.world, def.getKey()) : GateStorage.tallyFor(player, def.getKey());
            if (slain < def.killedCount) { continue; }
            if (def.killedDrops.isEmpty()) {
                ContentGates.unlock(player, def, true);
                continue;
            }

            if (def.global) { GateStorage.clearTallyGlobally(player.world, def.getKey()); }
            else { GateStorage.clearTallyFor(player, def.getKey()); }
            reward(player, def);
        }
    }

    private static void reward(EntityPlayerMP player, GateDef def) {
        ItemStack key = ContentStacks.parse(def.registryName, def.killedDrops, 1);
        if (key.isEmpty()) { return; }

        EntityItem drop = new EntityItem(player.world, player.posX, player.posY, player.posZ, key);
        drop.setDefaultPickupDelay();
        player.world.spawnEntity(drop);
    }

    @SubscribeEvent public static void onCraft(PlayerEvent.ItemCraftedEvent event) {
        EntityPlayer player = event.player;
        if (!(player instanceof EntityPlayerMP)) { return; }

        for (GateDef def : ContentGates.all().values()) {
            if (def.craft.isEmpty() || ContentGates.unlocked(player, def)) { continue; }
            if (!ContentGates.matches(event.crafting, stack(def.craft))) { continue; }

            ContentGates.unlock(player, def, true);
        }
    }

    @SubscribeEvent public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        EntityPlayer player = event.getEntityPlayer();
        if (event.getWorld().isRemote || !(player instanceof EntityPlayerMP)) { return; }

        ItemStack held = event.getItemStack();
        if (held.isEmpty()) { return; }

        BlockPos pos = event.getPos();
        Block clicked = event.getWorld().getBlockState(pos).getBlock();
        ResourceLocation name = clicked.getRegistryName();
        if (name == null) { return; }

        for (GateDef def : ContentGates.all().values()) {
            if (def.consume.isEmpty() || ContentGates.unlocked(player, def)) { continue; }
            if (!def.portalBlocks.isEmpty() && !def.portalBlocks.contains(name.toString())) { continue; }
            if (!ContentGates.matches(held, stack(def.consume))) { continue; }
            if (held.getCount() < def.consumeCount) { continue; }

            if (!player.capabilities.isCreativeMode) { held.shrink(def.consumeCount); }
            ContentGates.unlock(player, def, true);
            event.setCanceled(true);
            return;
        }
    }

    @SubscribeEvent public static void onAdvancement(AdvancementEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (!(player instanceof EntityPlayerMP)) { return; }

        ResourceLocation earned = event.getAdvancement().getId();
        for (GateDef def : ContentGates.all().values()) {
            if (def.advancement.isEmpty() || !def.global) { continue; }
            if (!def.advancement.equals(earned.toString())) { continue; }

            ContentGates.unlock(player, def, true);
        }
    }

    private static void retreat(EntityPlayerMP player) {
        BlockPos bed = safeBed(player, player.getBedLocation(player.dimension));
        BlockPos target = bed == null ? player.world.getSpawnPoint() : bed;
        player.setPositionAndUpdate(target.getX() + 0.5D, player.world.getTopSolidOrLiquidBlock(target).getY(), target.getZ() + 0.5D);
    }

    @Nullable private static BlockPos safeBed(EntityPlayerMP player, @Nullable BlockPos bed) {
        if (bed == null) { return null; }
        return EntityPlayer.getBedSpawnLocation(player.world, bed, player.isSpawnForced(player.dimension));
    }

    private static boolean quiet(EntityPlayerMP player) {
        long now = System.currentTimeMillis();
        Long last = SPOKEN.get(player.getUniqueID());
        if (last != null && now - last < QUIET_MILLIS) { return true; }

        SPOKEN.put(player.getUniqueID(), now);
        return false;
    }

    private static ItemStack stack(String item) { return ContentStacks.parse(new ResourceLocation("rdpl", "gate"), item, 1); }
}
