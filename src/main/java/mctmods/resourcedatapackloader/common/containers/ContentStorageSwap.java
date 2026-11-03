package mctmods.resourcedatapackloader.common.containers;

import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.blocks.container.ContentChests;
import mctmods.resourcedatapackloader.common.blocks.container.ContentContainerBlock;
import mctmods.resourcedatapackloader.common.blocks.container.logic.TileEntityPackContainer;
import mctmods.resourcedatapackloader.common.carry.ContentCarry;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerUpgradeDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public final class ContentStorageSwap {
    private static final double GUARD_RANGE = 4.0D;
    private static final double NEAR = 256.0D;
    private static final String FACING = "facing";
    private static final String LOOT_TABLE = "LootTable";
    private static final String CANNOT_MOVE = "cannot_move";
    private static final List<String> CARRIED = Arrays.asList(FACING, "axis", "rotation", "waterlogged");
    private static final List<String> NAME_KEYS = Arrays.asList("CustomName", "Lock");
    private static final List<String> LOOT_KEYS = Arrays.asList(LOOT_TABLE, "LootTableSeed");
    @Nullable private static Guard guard;
    private final ContainerUpgradeDef upgrade;
    private final ContainerUpgradeDef.Path path;
    private final EntityPlayerMP player;
    private final World world;
    private final BlockPos pos;
    private final IBlockState state;
    private final boolean nbt;
    private final List<ItemStack> held = new ArrayList<>();
    private TileEntity tile;
    private IBlockState target;
    private NBTTagCompound before;
    private boolean waitingLoot;
    private boolean linked;
    private boolean returning;
    @Nullable private EnumFacing facing;
    @Nullable private IItemHandler source;
    private List<ItemStack> shown = new ArrayList<>();
    @Nullable private List<StorageContents.Placed> plan;
    @Nullable private TileEntity probe;

    private ContentStorageSwap(ContainerUpgradeDef upgrade, ContainerUpgradeDef.Path path, EntityPlayerMP player, World world, BlockPos pos, IBlockState state) {
        this.upgrade = upgrade;
        this.path = path;
        this.player = player;
        this.world = world;
        this.pos = pos;
        this.state = state;
        this.nbt = path.contents == ContainerUpgradeDef.Contents.NBT;
    }

    static EnumActionResult use(ContainerUpgradeDef upgrade, ContainerUpgradeDef.Path path, Item kit, EntityPlayer player, EnumHand hand, World world, BlockPos pos, EnumFacing side, IBlockState state) {
        if (player.getCooldownTracker().hasCooldown(kit)) { return EnumActionResult.FAIL; }
        if (world.isRemote) { return EnumActionResult.SUCCESS; }
        if (!(player instanceof EntityPlayerMP)) { return EnumActionResult.PASS; }
        ContentStorageSwap swap = new ContentStorageSwap(upgrade, path, (EntityPlayerMP) player, world, pos, state);
        EnumActionResult refused = swap.preflight(kit);
        if (refused != null) { return refused; }
        if (!player.canPlayerEdit(pos, side, player.getHeldItem(hand)) || !ContentCarry.asks((EntityPlayerMP) player, pos)) { return ContentContainerUpgrades.denied(player, pos, "not allowed to change it"); }
        return swap.move();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST) public static void onItemJoin(EntityJoinWorldEvent event) {
        Guard open = guard;
        if (open == null || event.getWorld() != open.world || !(event.getEntity() instanceof EntityItem)) { return; }
        Entity entity = event.getEntity();
        if (open.pos.distanceSqToCenter(entity.posX, entity.posY, entity.posZ) > GUARD_RANGE) { return; }
        event.setCanceled(true);
        open.keep(((EntityItem) entity).getItem());
    }

    @Nullable private EnumActionResult preflight(Item kit) {
        try { return checked(kit); }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("The storage at {} could not be read for an upgrade, so it is left alone", pos, ex);
            return ContentContainerUpgrades.refuse(player, "it could not be read", CANNOT_MOVE);
        }
    }

    @Nullable private EnumActionResult checked(Item kit) {
        tile = world.getTileEntity(pos);
        if (tile == null) { return ContentContainerUpgrades.refuse(player, "it holds nothing", CANNOT_MOVE); }
        IBlockState named = ContentStates.parse(path.to, kit.getRegistryName());
        if (named == null || !named.getBlock().hasTileEntity(named)) { return ContentContainerUpgrades.refuse(player, "nothing to become", "unavailable"); }
        facing = heldFacing();
        target = carried(named);
        if (target == state) { return ContentContainerUpgrades.refuse(player, "nothing to become", "unavailable"); }
        if (state.getBlockHardness(world, pos) < 0.0F) { return ContentContainerUpgrades.denied(player, pos, "unbreakable"); }
        if (ContentCarry.twoBlock(state) || ContentCarry.twoBlock(target)) { return ContentContainerUpgrades.denied(player, pos, "two blocks"); }
        if (inUse()) { return ContentContainerUpgrades.refuse(player, "someone has it open", "in_use"); }
        before = tile.writeToNBT(new NBTTagCompound());
        waitingLoot = before.hasKey(LOOT_TABLE, Constants.NBT.TAG_STRING) || tile instanceof TileEntityPackContainer && ((TileEntityPackContainer) tile).pendingLoot();
        if (waitingLoot && !upgrade.carryLoot) { return ContentContainerUpgrades.refuse(player, "loot not rolled", "loot"); }
        linked = StorageContents.linked(tile);
        if (linked && (nbt || !path.linkedHalf)) { return ContentContainerUpgrades.refuse(player, "it shares storage with another block", "linked"); }
        EnumActionResult refused = nbt ? null : readable();
        if (refused != null) { return refused; }
        return probed();
    }

    @Nullable private EnumActionResult readable() {
        if (path.strict && (StorageContents.holdsFluid(tile) || StorageContents.savesFluid(before))) { return ContentContainerUpgrades.refuse(player, "it holds a fluid", CANNOT_MOVE); }
        if (waitingLoot) { return tile instanceof TileEntityLockableLoot && StorageContents.census(before).isEmpty() ? null : ContentContainerUpgrades.refuse(player, "waiting loot cannot be kept apart from its items", CANNOT_MOVE); }
        StorageContents.Source found = StorageContents.find(tile, before, linked, path.strict);
        if (found.handler == null) { return ContentContainerUpgrades.refuse(player, found.why, CANNOT_MOVE); }
        source = found.handler;
        if (StorageContents.withholds(source)) { return ContentContainerUpgrades.refuse(player, "a slot will not give up what it shows", CANNOT_MOVE); }
        shown = StorageContents.shown(source);
        return null;
    }

    @Nullable private EnumActionResult probed() {
        try {
            probe = target.getBlock().createTileEntity(world, target);
            if (probe == null) { return ContentContainerUpgrades.refuse(player, "nothing to become", "unavailable"); }
            probe.setWorld(world);
            probe.setPos(pos);
            shaped(probe, target);
            if (nbt) { return roundTrips() ? null : ContentContainerUpgrades.refuse(player, "the target saves differently", CANNOT_MOVE); }
            if (waitingLoot) { return lootSurvives() ? null : ContentContainerUpgrades.refuse(player, "the target cannot hold waiting loot", CANNOT_MOVE); }
            return planned();
        }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.info("Container upgrade: the probe of {} threw {}", target, ex.toString());
            plan = null;
            probe = null;
            return nbt || waitingLoot ? ContentContainerUpgrades.refuse(player, "the target saves differently", CANNOT_MOVE) : null;
        }
    }

    private boolean roundTrips() {
        if (probe == null) { return false; }
        load(probe, before);
        NBTTagCompound saved = probe.writeToNBT(new NBTTagCompound());
        return StorageContents.census(saved).equals(StorageContents.census(before)) && (!waitingLoot || before.getString(LOOT_TABLE).equals(saved.getString(LOOT_TABLE)));
    }

    private boolean lootSurvives() {
        if (probe == null) { return false; }
        merge(probe, LOOT_KEYS);
        return before.getString(LOOT_TABLE).equals(probe.writeToNBT(new NBTTagCompound()).getString(LOOT_TABLE));
    }

    @Nullable private EnumActionResult planned() {
        IItemHandler into = StorageContents.forProbe(probe);
        if (into == null) { return ContentContainerUpgrades.refuse(player, "the target has no inventory", CANNOT_MOVE); }
        if (into.getSlots() < shown.size() && !path.fit) { return ContentContainerUpgrades.refuse(player, "target is smaller", "smaller"); }
        plan = StorageContents.plan(into, shown);
        return plan == null ? ContentContainerUpgrades.refuse(player, "the contents do not fit", CANNOT_MOVE) : null;
    }

    private boolean inUse() {
        if (tile instanceof TileEntityPackContainer && ((TileEntityPackContainer) tile).watched()) { return true; }
        for (EntityPlayer other : world.playerEntities) {
            if (other.openContainer == other.inventoryContainer) { continue; }
            for (Slot slot : other.openContainer.inventorySlots) {
                if (StorageContents.shows(slot, tile)) { return true; }
            }
            if (pos.distanceSqToCenter(other.posX, other.posY, other.posZ) <= NEAR && other.openContainer.canInteractWith(other)) { return true; }
        }
        return false;
    }

    private boolean namedState() { return !path.carryState || path.to.split(":").length >= 3; }

    @Nullable private EnumFacing heldFacing() {
        if (tile instanceof TileEntityPackContainer) { return ((TileEntityPackContainer) tile).facing(); }
        IProperty<?> property = state.getBlock().getBlockState().getProperty(FACING);
        Object value = property == null ? null : state.getValue(property);
        return value instanceof EnumFacing ? (EnumFacing) value : null;
    }

    private IBlockState carried(IBlockState named) {
        if (namedState()) { return named; }
        IBlockState result = named;
        for (IProperty<?> wanted : named.getPropertyKeys()) {
            if (!CARRIED.contains(wanted.getName())) { continue; }
            IProperty<?> had = state.getBlock().getBlockState().getProperty(wanted.getName());
            if (had != null) { result = withValue(result, wanted, state.getValue(had)); }
            if (tile instanceof TileEntityPackContainer && FACING.equals(wanted.getName())) { result = withValue(result, wanted, facing); }
        }
        return result;
    }

    private static <T extends Comparable<T>> IBlockState withValue(IBlockState into, IProperty<T> property, @Nullable Object value) {
        for (T allowed : property.getAllowedValues()) {
            if (allowed.equals(value)) { return into.withProperty(property, allowed); }
        }
        return into;
    }

    private EnumActionResult move() {
        if (path.contents == ContainerUpgradeDef.Contents.SLOTS && stuck()) { return ContentContainerUpgrades.refuse(player, "its contents would not come out", CANNOT_MOVE); }
        Guard forward = new Guard(world, pos, nbt, Item.getItemFromBlock(state.getBlock()));
        try { forward(forward); }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("The storage upgrade at {} failed, so the block is put back", pos, ex);
            recover(forward);
            return ContentContainerUpgrades.refuse(player, "the swap failed", CANNOT_MOVE);
        }
        spill(forward.kept);
        ContentLog.LOGGER.info("Container upgrade: {} upgraded the storage at {} to {}", player.getName(), pos, target.getBlock().getRegistryName());
        return EnumActionResult.SUCCESS;
    }

    @SuppressWarnings("DataFlowIssue") private void forgetLoot() { ((TileEntityLockableLoot) tile).setLootTable(null, 0L); }

    private boolean stuck() {
        if (waitingLoot) {
            forgetLoot();
            return false;
        }
        try {
            if (source != null && StorageContents.drain(source, held) && (!path.strict || emptied())) { return false; }
        }
        catch (RuntimeException ex) { ContentLog.LOGGER.error("The storage at {} failed while being emptied", pos, ex); }
        undo();
        return true;
    }

    private boolean emptied() { return StorageContents.census(tile.writeToNBT(new NBTTagCompound())).isEmpty() && StorageContents.totals(held).equals(StorageContents.census(before)); }

    private void undo() {
        try {
            if (path.strict) {
                tile.readFromNBT(before.copy());
                tile.markDirty();
            }
            else { putBack(source); }
        }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("The storage at {} could not be put back, its saved data was {}", pos, before, ex);
            spill(path.strict && cleared() ? StorageContents.stacksIn(before) : held);
        }
    }

    private boolean cleared() {
        try { return source != null && StorageContents.drain(source, new ArrayList<>()); }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("The storage at {} could not be cleared", pos, ex);
            return false;
        }
    }

    private void forward(Guard forward) {
        if (nbt) { removed(forward); }
        boolean set = placed(target, forward);
        TileEntity now = world.getTileEntity(pos);
        if (!set || now == null || probe != null && now.getClass() != probe.getClass()) { throw new IllegalStateException("the block became " + world.getBlockState(pos) + " holding " + now); }
        fill(now);
        finish(now);
    }

    private void removed(Guard open) {
        guard = open;
        try { world.removeTileEntity(pos); }
        finally { guard = null; }
    }

    private boolean placed(IBlockState into, Guard open) {
        guard = open;
        try { return ContentContainerUpgrades.place(world, pos, into); }
        finally { guard = null; }
    }

    private void fill(TileEntity now) {
        shaped(now, target);
        if (nbt) { load(now, before); }
        else {
            merge(now, NAME_KEYS);
            if (waitingLoot) { merge(now, LOOT_KEYS); }
            if (!held.isEmpty()) {
                IItemHandler into = StorageContents.forFill(now);
                if (into == null) { throw new IllegalStateException("the target has no inventory"); }
                StorageContents.fill(into, plan, held);
            }
        }
        if (now instanceof TileEntityPackContainer && facing != null && path.carryState) { ((TileEntityPackContainer) now).face(facing); }
        now.markDirty();
    }

    private void finish(TileEntity now) {
        IBlockState placed = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, placed, placed, 3);
        world.notifyNeighborsOfStateChange(pos, placed.getBlock(), false);
        SoundType sound = placed.getBlock().getSoundType(placed, world, now.getPos(), null);
        world.playSound(null, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
        if (linked) { ContentChests.parted(world, pos, state.getBlock()); }
    }

    private void recover(Guard forward) {
        boolean trusted = nbt || path.strict || waitingLoot;
        try {
            TileEntity back = restored();
            load(back, before);
            if (!trusted) { reinsert(back, forward); }
            back.markDirty();
            world.notifyBlockUpdate(pos, state, state, 3);
            ContentChests.recheck(world, pos);
        }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("The storage at {} could not be put back, so its items are dropped; its saved data was {}", pos, before, ex);
            if (!returning) { wipe(); }
            spill(nbt ? StorageContents.stacksIn(before) : held);
            if (!nbt) { spill(forward.kept); }
        }
    }

    private void reinsert(TileEntity back, Guard forward) {
        IItemHandler into = StorageContents.forFill(back);
        if (into == null) { throw new IllegalStateException("no inventory at " + pos); }
        if (!StorageContents.drain(into, new ArrayList<>())) { throw new IllegalStateException("could not clear " + pos); }
        returning = true;
        forward.kept.clear();
        putBack(into);
    }

    private void wipe() {
        try { removed(new Guard(world, pos, true, null)); }
        catch (RuntimeException ex) { ContentLog.LOGGER.error("The block entity at {} could not be cleared", pos, ex); }
    }

    private TileEntity restored() {
        if (world.getBlockState(pos) != state) { placed(state, new Guard(world, pos, true, null)); }
        if (world.getBlockState(pos).getBlock() != state.getBlock()) { throw new IllegalStateException("the block did not go back at " + pos); }
        TileEntity back = world.getTileEntity(pos);
        if (back == null) {
            back = state.getBlock().createTileEntity(world, state);
            if (back == null) { throw new IllegalStateException("no block entity at " + pos); }
            world.setTileEntity(pos, back);
        }
        shaped(back, state);
        return back;
    }

    private void putBack(IItemHandler into) {
        for (Iterator<ItemStack> stacks = held.iterator(); stacks.hasNext();) {
            ItemStack left = ItemHandlerHelper.insertItemStacked(into, stacks.next().copy(), false);
            stacks.remove();
            dropAt(left);
        }
    }

    private void spill(List<ItemStack> stacks) {
        for (ItemStack stack : stacks) { dropAt(stack); }
    }

    private void dropAt(ItemStack stack) {
        if (!stack.isEmpty()) { InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack.copy()); }
    }

    private void merge(TileEntity into, List<String> keys) {
        boolean any = false;
        for (String key : keys) { any |= before.hasKey(key); }
        if (!any) { return; }
        NBTTagCompound saved = into.writeToNBT(new NBTTagCompound());
        for (String key : keys) {
            if (before.hasKey(key)) { saved.setTag(key, before.getTag(key).copy()); }
        }
        into.readFromNBT(saved);
    }

    private void load(TileEntity into, NBTTagCompound tag) {
        NBTTagCompound copy = tag.copy();
        copy.setInteger("x", pos.getX());
        copy.setInteger("y", pos.getY());
        copy.setInteger("z", pos.getZ());
        if (into instanceof TileEntityPackContainer) { ((TileEntityPackContainer) into).adopt(copy); }
        else { into.readFromNBT(copy); }
    }

    private static void shaped(TileEntity into, IBlockState as) {
        if (!(into instanceof TileEntityPackContainer) || !(as.getBlock() instanceof ContentContainerBlock)) { return; }
        ((ContentContainerBlock) as.getBlock()).reshape((TileEntityPackContainer) into, as);
        ((TileEntityPackContainer) into).unloot();
    }

    private static final class Guard {
        private final World world;
        private final BlockPos pos;
        private final boolean discardAll;
        @Nullable private final Item own;
        private final List<ItemStack> kept = new ArrayList<>();
        private boolean ownSeen;

        private Guard(World world, BlockPos pos, boolean discardAll, @Nullable Item own) {
            this.world = world;
            this.pos = pos;
            this.discardAll = discardAll;
            this.own = own;
        }

        private void keep(ItemStack stack) {
            if (discardAll) { return; }
            if (stack.getItem() == own && stack.getCount() == 1 && !ownSeen) {
                ownSeen = true;
                return;
            }
            kept.add(stack.copy());
        }
    }
}
