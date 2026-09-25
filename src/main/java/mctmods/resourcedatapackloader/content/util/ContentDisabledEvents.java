package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.content.worldgen.ContentReplacements;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.SlotItemHandler;
import java.util.List;
import java.util.stream.IntStream;

public final class ContentDisabledEvents {
    private static final int SWEEP_EVERY = 20;

    private ContentDisabledEvents() {}

    public static void onUse(PlayerInteractEvent.RightClickItem event) {
        if (held(event.getEntity(), event.getHand())) { event.setCanceled(true); }
    }

    public static void onUseOn(PlayerInteractEvent.RightClickBlock event) {
        if (held(event.getEntity(), event.getHand())) { event.setUseItem(TriState.FALSE); }
    }

    public static void onDig(PlayerInteractEvent.LeftClickBlock event) {
        if (held(event.getEntity(), event.getHand())) { event.setCanceled(true); }
    }

    public static void onAttack(AttackEntityEvent event) {
        if (held(event.getEntity(), InteractionHand.MAIN_HAND)) { event.setCanceled(true); }
    }

    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (ContentDisabled.disabled(event.getPlacedBlock())) { event.setCanceled(true); }
    }

    public static void onPickup(ItemEntityPickupEvent.Pre event) {
        if (!ContentDisabled.disabled(event.getItemEntity().getItem())) { return; }
        event.setCanPickup(TriState.FALSE);
        event.getItemEntity().discard();
    }

    public static void onJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ItemEntity item && ContentDisabled.disabled(item.getItem())) { event.setCanceled(true); }
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        sweep(event.getEntity().getInventory());
        sweep(event.getEntity().getEnderChestInventory());
    }

    public static void onOpen(PlayerContainerEvent.Open event) {
        if (!ContentDisabled.any()) { return; }
        AbstractContainerMenu menu = event.getContainer();
        boolean changed = false;
        for (Slot slot : menu.slots) {
            ItemStack stack = slot.getItem();
            if (!ContentDisabled.disabled(stack)) { continue; }
            if (slot instanceof SlotItemHandler handled && !(handled.getItemHandler() instanceof IItemHandlerModifiable)) { slot.remove(stack.getCount()); }
            else { slot.set(ItemStack.EMPTY); }
            changed = true;
        }
        if (changed) { menu.broadcastChanges(); }
    }

    public static void onTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide() || event.getEntity().tickCount % SWEEP_EVERY != 0) { return; }
        sweep(event.getEntity().getInventory());
    }

    public static void onChunk(ChunkEvent.Load event) {
        if (!ContentDisabled.any() || !(event.getLevel() instanceof ServerLevel level) || !(event.getChunk() instanceof LevelChunk chunk)) { return; }
        int[] placed = placed(chunk);
        if (placed.length == 0 && chunk.getBlockEntities().isEmpty()) { return; }
        List<BlockEntity> entities = List.copyOf(chunk.getBlockEntities().values());
        MinecraftServer server = level.getServer();
        server.tell(new TickTask(server.getTickCount(), () -> {
            removePlaced(level, chunk, placed);
            sweepLoaded(entities);
        }));
    }

    private static int[] placed(LevelChunk chunk) {
        LevelChunkSection[] sections = chunk.getSections();
        return IntStream.range(0, sections.length).filter(index -> !sections[index].hasOnlyAir() && sections[index].getStates().maybeHas(ContentDisabled::disabled)).toArray();
    }

    private static void removePlaced(ServerLevel level, LevelChunk chunk, int[] placed) {
        ChunkPos at = chunk.getPos();
        if (placed.length == 0 || level.getChunkSource().getChunkNow(at.x, at.z) != chunk) { return; }
        int removed = 0;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int index : placed) {
            LevelChunkSection section = chunk.getSections()[index];
            int bottom = SectionPos.sectionToBlockCoord(chunk.getSectionYFromSectionIndex(index));
            for (int y = 0; y < SectionPos.SECTION_SIZE; y++) {
                for (int z = 0; z < SectionPos.SECTION_SIZE; z++) {
                    for (int x = 0; x < SectionPos.SECTION_SIZE; x++) {
                        BlockState state = section.getBlockState(x, y, z);
                        if (!ContentDisabled.disabled(state) || ContentReplacements.replaces(level, state, bottom + y)) { continue; }
                        pos.set(at.getBlockX(x), bottom + y, at.getBlockZ(z));
                        level.removeBlockEntity(pos);
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                        removed++;
                    }
                }
            }
        }
        if (removed > 0) { ContentLog.LOGGER.debug("Removed {} disabled block(s) from chunk {} of {}", removed, at, level.dimension().location()); }
    }

    private static void sweepLoaded(List<BlockEntity> entities) {
        for (BlockEntity entity : entities) {
            if (entity.isRemoved()) { continue; }
            try { sweep(entity); }
            catch (RuntimeException ex) { ContentLog.LOGGER.debug("Could not clear disabled items out of {} at {}: {}", entity.getClass().getName(), entity.getBlockPos(), ex.getMessage()); }
        }
    }

    private static boolean held(Player player, InteractionHand hand) {
        if (!ContentDisabled.disabled(player.getItemInHand(hand))) { return false; }
        if (!player.level().isClientSide()) { player.setItemInHand(hand, ItemStack.EMPTY); }
        return true;
    }

    private static void sweep(Container container) {
        if (!ContentDisabled.any()) { return; }
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            if (ContentDisabled.disabled(container.getItem(slot))) { container.setItem(slot, ItemStack.EMPTY); }
        }
    }

    private static void sweep(BlockEntity entity) {
        if (entity instanceof RandomizableContainer loot && loot.getLootTable() != null) { return; }
        if (entity instanceof Container container) {
            sweep(container);
            return;
        }
        IItemHandler handler = entity.getLevel() == null ? null : entity.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, entity.getBlockPos(), entity.getBlockState(), entity, null);
        if (handler != null) { sweep(handler); }
    }

    private static void sweep(IItemHandler handler) {
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (!ContentDisabled.disabled(stack)) { continue; }
            if (handler instanceof IItemHandlerModifiable modifiable) { modifiable.setStackInSlot(slot, ItemStack.EMPTY); }
            else { handler.extractItem(slot, stack.getCount(), false); }
        }
    }
}
