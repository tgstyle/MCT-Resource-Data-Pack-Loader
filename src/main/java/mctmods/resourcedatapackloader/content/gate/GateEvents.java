package mctmods.resourcedatapackloader.content.gate;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.GateDef;
import mctmods.resourcedatapackloader.content.entity.ContentRanks;
import mctmods.resourcedatapackloader.content.worldgen.ContentCelestial;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Stacks;
import mctmods.resourcedatapackloader.util.compat.GcCamera;
import mctmods.resourcedatapackloader.util.world.Travel;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public final class GateEvents {
    private static final long QUIET_MILLIS = 2000L;
    private static final Map<UUID, Long> SPOKEN = new HashMap<>();
    private static final int TURN_BACK_TICKS = 60;
    private static final Map<UUID, TurnBack> TURNING = new HashMap<>();

    private GateEvents() {}

    @SubscribeEvent public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        SPOKEN.remove(event.player.getUniqueID());
    }

    public static void serverStopped() { TURNING.clear(); }

    @SubscribeEvent public static void onArrive(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) { return; }
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        for (GateDef def : ContentGates.forDimension(event.toDim)) {
            if (ContentGates.admits(player, def)) { continue; }
            ContentGates.refuse(player, def);
            TURNING.put(player.getUniqueID(), new TurnBack(def, event.fromDim, event.toDim));
            return;
        }
    }

    @SubscribeEvent public static void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || TURNING.isEmpty()) { return; }
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            UUID id = player.getUniqueID();
            TurnBack back = TURNING.get(id);
            if (back == null) { continue; }
            if (player.dimension != back.to) {
                TURNING.remove(id);
                continue;
            }
            if (admitsAll(player, back.to)) {
                TURNING.remove(id);
                continue;
            }
            if (--back.ticks > 0) { continue; }
            TURNING.remove(id);
            turnBack(server, player, back);
        }
    }

    private static void turnBack(MinecraftServer server, EntityPlayerMP player, TurnBack back) {
        World from = worldOf(back.from);
        BlockPos spot = from == null ? null : landing(player, from, back.from);
        List<ItemStack> returned = new ArrayList<>();
        Entity vehicle = player.getRidingEntity();
        if (vehicle instanceof IInventory) {
            IInventory inventory = (IInventory) vehicle;
            for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
                ItemStack stack = inventory.removeStackFromSlot(slot);
                if (!stack.isEmpty()) { returned.add(stack); }
            }
        }
        if (ContentCelestial.present()) { returned.addAll(GcCamera.chestCargo(player)); }
        player.dismountRidingEntity();
        boolean sent = spot != null && spot.getY() > 0 && Travel.to(player, back.from, spot.getX() + 0.5D, spot.getY(), spot.getZ() + 0.5D, player.rotationYaw, player.rotationPitch);
        if (!sent) {
            World overworld = server.getWorld(0);
            BlockPos home = ground(overworld, overworld.getSpawnPoint());
            Travel.to(player, 0, home.getX() + 0.5D, home.getY(), home.getZ() + 0.5D, player.rotationYaw, player.rotationPitch);
        }
        if (vehicle != null && ContentCelestial.present()) { GcCamera.restore(player, vehicle); }
        for (ItemStack stack : returned) { ItemHandlerHelper.giveItemToPlayer(player, stack); }
        if (player.dimension == back.to) { ContentLog.LOGGER.warn("Gate {} could not send {} back from dimension {}", back.def.getKey(), player.getName(), back.to); } else { ContentLog.LOGGER.info("Gate {} sent {} back to dimension {}, reached without passing the gate", back.def.getKey(), player.getName(), player.dimension); }
    }

    @Nullable private static World worldOf(int dimension) { return DimensionManager.getWorld(dimension); }

    private static boolean admitsAll(EntityPlayerMP player, int dimension) {
        for (GateDef def : ContentGates.forDimension(dimension)) {
            if (!ContentGates.admits(player, def)) { return false; }
        }
        return true;
    }

    private static final class TurnBack {
        private final GateDef def;
        private final int from;
        private final int to;
        private int ticks = TURN_BACK_TICKS;

        private TurnBack(GateDef def, int from, int to) {
            this.def = def;
            this.from = from;
            this.to = to;
        }
    }

    @SubscribeEvent public static void onTravel(EntityTravelToDimensionEvent event) {
        if (!(event.getEntity() instanceof EntityPlayerMP)) { return; }
        EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
        List<GateDef> defs = ContentGates.forDimension(event.getDimension());
        for (GateDef def : defs) {
            if (ContentGates.admits(player, def)) { continue; }
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
            if (!def.killedRank.isEmpty() && !def.killedRank.equals(ContentRanks.rankOf(event.getEntity()))) { continue; }
            int slain = GateStorage.tally(player, def);
            if (slain < def.killedCount) { continue; }
            if (def.killedDrops.isEmpty()) {
                ContentGates.unlock(player, def, true);
                continue;
            }
            GateStorage.clearTally(player, def);
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
            if (!Stacks.matches(ContentGates.stack(def.craft), event.crafting)) { continue; }
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
            if (!Stacks.matches(ContentGates.stack(def.consume), held)) { continue; }
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
            if (def.advancement.isEmpty() || def.perPlayer()) { continue; }
            if (!def.advancement.equals(earned.toString())) { continue; }
            ContentGates.unlock(player, def, true);
        }
    }

    private static void retreat(EntityPlayerMP player) {
        BlockPos target = landing(player, player.world, player.dimension);
        player.setPositionAndUpdate(target.getX() + 0.5D, target.getY(), target.getZ() + 0.5D);
    }

    private static BlockPos landing(EntityPlayerMP player, World world, int dimension) {
        BlockPos bed = safeBed(player, world, player.getBedLocation(dimension), dimension);
        return ground(world, bed == null ? world.getSpawnPoint() : bed);
    }

    private static BlockPos ground(World world, BlockPos target) { return new BlockPos(target.getX(), world.getTopSolidOrLiquidBlock(target).getY(), target.getZ()); }

    @Nullable private static BlockPos safeBed(EntityPlayerMP player, World world, @Nullable BlockPos bed, int dimension) {
        if (bed == null) { return null; }
        return EntityPlayer.getBedSpawnLocation(world, bed, player.isSpawnForced(dimension));
    }

    private static boolean quiet(EntityPlayerMP player) {
        long now = System.currentTimeMillis();
        Long last = SPOKEN.get(player.getUniqueID());
        if (last != null && now - last < QUIET_MILLIS) { return true; }
        SPOKEN.put(player.getUniqueID(), now);
        return false;
    }
}
