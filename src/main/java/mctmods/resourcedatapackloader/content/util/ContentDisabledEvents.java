package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.worldgen.ContentReplacements;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IBlockStateContainer;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IBlockStatePaletteHashMap;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IBlockStatePaletteLinear;
import mctmods.resourcedatapackloader.mixin.rdpl.common.ITileEntityLockableLoot;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IBlockStatePalette;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.IntStream;

@Mod.EventBusSubscriber(modid = ResourceDataPackLoader.MOD_ID) public final class ContentDisabledEvents {
    private static final int SWEEP_EVERY = 20;
    private static final int SECTION = 16;
    private static final Queue<Placed> PLACED = new ConcurrentLinkedQueue<>();

    private ContentDisabledEvents() {}

    @SubscribeEvent public static void onUse(PlayerInteractEvent.RightClickItem event) {
        if (held(event.getEntityPlayer(), event.getHand())) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onUseOn(PlayerInteractEvent.RightClickBlock event) {
        if (held(event.getEntityPlayer(), event.getHand())) { event.setUseItem(Event.Result.DENY); }
    }

    @SubscribeEvent public static void onDig(PlayerInteractEvent.LeftClickBlock event) {
        if (held(event.getEntityPlayer(), event.getHand())) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onAttack(AttackEntityEvent event) {
        if (held(event.getEntityPlayer(), EnumHand.MAIN_HAND)) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (ContentDisabled.disabled(event.getPlacedBlock().getBlock())) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onPickup(EntityItemPickupEvent event) {
        if (!ContentDisabled.disabled(event.getItem().getItem())) { return; }
        event.setCanceled(true);
        event.getItem().setDead();
    }

    @SubscribeEvent public static void onJoin(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityItem && ContentDisabled.disabled(((EntityItem) event.getEntity()).getItem())) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onHarvest(BlockEvent.HarvestDropsEvent event) { event.getDrops().removeIf(ContentDisabled::disabled); }

    @SubscribeEvent public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        sweep(event.player.inventory);
        sweep(event.player.getInventoryEnderChest());
    }

    @SubscribeEvent public static void onOpen(PlayerContainerEvent.Open event) {
        if (!ContentDisabled.any()) { return; }
        Container container = event.getContainer();
        boolean changed = false;
        for (Slot slot : container.inventorySlots) {
            ItemStack stack = slot.getStack();
            if (!ContentDisabled.disabled(stack)) { continue; }
            if (slot instanceof SlotItemHandler && !(((SlotItemHandler) slot).getItemHandler() instanceof IItemHandlerModifiable)) { slot.decrStackSize(stack.getCount()); }
            else { slot.putStack(ItemStack.EMPTY); }
            changed = true;
        }
        if (changed) { container.detectAndSendChanges(); }
    }

    @SubscribeEvent public static void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote || event.player.ticksExisted % SWEEP_EVERY != 0) { return; }
        sweep(event.player.inventory);
    }

    @SubscribeEvent public static void onChunk(ChunkEvent.Load event) {
        if (!ContentDisabled.any() || event.getWorld().isRemote) { return; }
        int[] placed = placed(event.getChunk());
        if (placed.length > 0) { PLACED.add(new Placed(event.getChunk(), placed)); }
        for (TileEntity tile : event.getChunk().getTileEntityMap().values()) {
            try { sweep(tile); }
            catch (RuntimeException ex) { ContentLog.LOGGER.debug("Could not clear disabled items out of {} at {}: {}", tile.getClass().getName(), tile.getPos(), ex.getMessage()); }
        }
    }

    @SubscribeEvent public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) { return; }
        for (Placed next = PLACED.poll(); next != null; next = PLACED.poll()) { removePlaced(next.chunk, next.sections); }
    }

    private static int[] placed(Chunk chunk) {
        ExtendedBlockStorage[] sections = chunk.getBlockStorageArray();
        return IntStream.range(0, sections.length).filter(index -> sections[index] != Chunk.NULL_BLOCK_STORAGE && !sections[index].isEmpty() && holds(sections[index].getData())).toArray();
    }

    private static boolean holds(BlockStateContainer states) {
        IBlockStatePalette palette = ((IBlockStateContainer) states).rdpl$palette();
        if (palette instanceof IBlockStatePaletteLinear) {
            IBlockStatePaletteLinear linear = (IBlockStatePaletteLinear) palette;
            IBlockState[] held = linear.rdpl$states();
            for (int index = 0; index < linear.rdpl$arraySize(); index++) {
                if (held[index] != null && ContentDisabled.disabled(held[index].getBlock())) { return true; }
            }
            return false;
        }
        if (palette instanceof IBlockStatePaletteHashMap) {
            for (IBlockState state : ((IBlockStatePaletteHashMap) palette).rdpl$statePaletteMap()) {
                if (state != null && ContentDisabled.disabled(state.getBlock())) { return true; }
            }
            return false;
        }
        return true;
    }

    private static void removePlaced(Chunk chunk, int[] sections) {
        World world = chunk.getWorld();
        if (!chunk.isLoaded() || world.getChunkProvider().getLoadedChunk(chunk.x, chunk.z) != chunk) { return; }
        int removed = 0;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int index : sections) {
            ExtendedBlockStorage section = chunk.getBlockStorageArray()[index];
            if (section == Chunk.NULL_BLOCK_STORAGE) { continue; }
            int bottom = section.getYLocation();
            for (int y = 0; y < SECTION; y++) {
                for (int z = 0; z < SECTION; z++) {
                    for (int x = 0; x < SECTION; x++) {
                        IBlockState state = section.get(x, y, z);
                        if (!ContentDisabled.disabled(state.getBlock()) || ContentReplacements.replaces(world, state, bottom + y)) { continue; }
                        pos.setPos((chunk.x << 4) + x, bottom + y, (chunk.z << 4) + z);
                        world.removeTileEntity(pos);
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
                        removed++;
                    }
                }
            }
        }
        if (removed > 0) { ContentLog.LOGGER.debug("Removed {} disabled block(s) from chunk {}, {} of dimension {}", removed, chunk.x, chunk.z, world.provider.getDimension()); }
    }

    private static boolean held(EntityPlayer player, EnumHand hand) {
        if (!ContentDisabled.disabled(player.getHeldItem(hand))) { return false; }
        if (!player.world.isRemote) { player.setHeldItem(hand, ItemStack.EMPTY); }
        return true;
    }

    private static void sweep(IInventory inventory) {
        if (!ContentDisabled.any()) { return; }
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            if (ContentDisabled.disabled(inventory.getStackInSlot(slot))) { inventory.setInventorySlotContents(slot, ItemStack.EMPTY); }
        }
    }

    private static void sweep(TileEntity tile) {
        if (tile instanceof TileEntityLockableLoot && ((ITileEntityLockableLoot) tile).rdpl$lootTable() != null) { return; }
        if (tile instanceof IInventory) {
            sweep((IInventory) tile);
            return;
        }
        if (!tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) { return; }
        IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (handler == null) { return; }
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (!ContentDisabled.disabled(stack)) { continue; }
            if (handler instanceof IItemHandlerModifiable) { ((IItemHandlerModifiable) handler).setStackInSlot(slot, ItemStack.EMPTY); }
            else { handler.extractItem(slot, stack.getCount(), false); }
        }
    }

    private static final class Placed {
        private final Chunk chunk;
        private final int[] sections;

        private Placed(Chunk chunk, int[] sections) {
            this.chunk = chunk;
            this.sections = sections;
        }
    }
}
