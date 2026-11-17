package mctmods.resourcedatapackloader.common.raids.base;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimOwners;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimSettings;
import mctmods.resourcedatapackloader.common.pack.definitions.villages.RaidDef;
import mctmods.resourcedatapackloader.common.raids.RaidTarget;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public final class BaseSignals {
    private static final int SAMPLE_EVERY = 200;
    private static final int MIN_SLOTS = 9;
    private static boolean armed;
    private static boolean creative;

    private BaseSignals() {}

    public static void arm(Collection<RaidDef> defs) {
        armed = false;
        creative = false;
        for (RaidDef def : defs) {
            if (!def.target().contains(RaidTarget.Kind.BASE) && !def.target().contains(RaidTarget.Kind.PLAYER)) { continue; }
            armed = true;
            creative |= def.base() != null && def.base().creative;
        }
    }

    public static void sample(EntityPlayer player) {
        if (!armed || player.ticksExisted % SAMPLE_EVERY != 0 || !(player.world instanceof WorldServer) || player.isSpectator() || player.isCreative() && !creative) { return; }
        BaseLedger.get(player.world).ticks(player, new BlockPos(player), SAMPLE_EVERY);
    }

    @SubscribeEvent public static void onSpawn(PlayerSetSpawnEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (!armed || player.world.isRemote) { return; }
        BlockPos spawn = event.getNewSpawn();
        BaseLedger ledger = BaseLedger.get(player.world);
        if (spawn == null) { ledger.clearBed(player.getUniqueID()); }
        else { ledger.setBed(player.getUniqueID(), spawn); }
    }

    @SubscribeEvent public static void onWake(PlayerWakeUpEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (!armed || player.world.isRemote || player.bedLocation == null) { return; }
        BaseLedger.get(player.world).setBed(player.getUniqueID(), player.bedLocation);
    }

    @SubscribeEvent public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        if (!armed || player.world.isRemote) { return; }
        BaseLedger ledger = BaseLedger.get(player.world);
        if (ledger.bed(player.getUniqueID()) != null) { return; }
        BlockPos bed = player.getBedLocation(player.dimension);
        if (bed != null) { ledger.setBed(player.getUniqueID(), bed); }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (!armed || event.getWorld().isRemote || event.isCanceled()) { return; }
        EntityPlayer player = counted(event.getEntity());
        if (player != null) { BaseLedger.get(event.getWorld()).placed(player, event.getPos(), 1); }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onBreak(BlockEvent.BreakEvent event) {
        if (!armed || event.getWorld().isRemote || event.isCanceled()) { return; }
        EntityPlayer player = counted(event.getPlayer());
        if (player != null) { BaseLedger.get(event.getWorld()).placed(player, event.getPos(), -1); }
    }

    @SubscribeEvent public static void onOpen(PlayerContainerEvent.Open event) {
        EntityPlayer player = event.getEntityPlayer();
        if (!armed || player.world.isRemote || !(player instanceof EntityPlayerMP)) { return; }
        BlockPos container = containerAt(player, event);
        if (container != null) { BaseLedger.get(player.world).opened(player, container); }
    }

    @Nullable private static EntityPlayer counted(@Nullable Entity entity) {
        if (!(entity instanceof EntityPlayer)) { return null; }
        EntityPlayer player = (EntityPlayer) entity;
        return ClaimOwners.isFake(player) && ClaimSettings.fakePlayerBlocksNone() ? null : player;
    }

    @Nullable private static BlockPos containerAt(EntityPlayer player, PlayerContainerEvent.Open event) {
        for (Slot slot : event.getContainer().inventorySlots) {
            IInventory inventory = slot.inventory;
            if (inventory.getSizeInventory() < MIN_SLOTS || inventory instanceof InventoryPlayer) { continue; }
            if (inventory instanceof TileEntity) { return ((TileEntity) inventory).getPos(); }
            return inventory instanceof InventoryLargeChest ? pairAt(player, (InventoryLargeChest) inventory) : null;
        }
        return null;
    }

    @Nullable private static BlockPos pairAt(EntityPlayer player, InventoryLargeChest large) {
        double reach = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() + 2.0D;
        int fromX = MathHelper.floor(player.posX - reach) >> 4;
        int toX = MathHelper.floor(player.posX + reach) >> 4;
        int fromZ = MathHelper.floor(player.posZ - reach) >> 4;
        int toZ = MathHelper.floor(player.posZ + reach) >> 4;
        BlockPos lower = null;
        for (int chunkX = fromX; chunkX <= toX; chunkX++) {
            for (int chunkZ = fromZ; chunkZ <= toZ; chunkZ++) {
                Chunk chunk = player.world.getChunkProvider().getLoadedChunk(chunkX, chunkZ);
                if (chunk == null) { continue; }
                for (TileEntity tile : chunk.getTileEntityMap().values()) {
                    BlockPos at = tile.getPos();
                    if (!(tile instanceof TileEntityChest) || player.getDistanceSqToCenter(at) > reach * reach || !large.isPartOfLargeChest((TileEntityChest) tile)) { continue; }
                    if (lower == null || at.toLong() < lower.toLong()) { lower = at; }
                }
            }
        }
        return lower;
    }
}
